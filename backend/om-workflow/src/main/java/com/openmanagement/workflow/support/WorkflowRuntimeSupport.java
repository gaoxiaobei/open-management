package com.openmanagement.workflow.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.workflow.domain.entity.WfProcessInstance;
import com.openmanagement.workflow.domain.entity.WfTask;
import com.openmanagement.workflow.mapper.ProcessInstanceMapper;
import com.openmanagement.workflow.mapper.TaskMapper;
import com.openmanagement.workflow.vo.ProcessProgressVO;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WorkflowRuntimeSupport {

    public static final String PROCESS_STATUS_RUNNING = "RUNNING";
    public static final String PROCESS_STATUS_COMPLETED = "COMPLETED";
    public static final String PROCESS_STATUS_REJECTED = "REJECTED";

    public static final String TASK_STATUS_PENDING = "PENDING";
    public static final String TASK_STATUS_COMPLETED = "COMPLETED";

    public static final String ACTION_APPROVE = "APPROVE";
    public static final String ACTION_REJECT = "REJECT";
    public static final String ACTION_TRANSFER = "TRANSFER";

    private static final String VARIABLE_ASSIGNEE = "assignee";
    private static final String VARIABLE_APPROVED = "approved";
    private static final String VARIABLE_NEXT_ASSIGNEE_IDS = "nextAssigneeIds";

    private final ProcessInstanceMapper processInstanceMapper;
    private final TaskMapper taskMapper;
    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final HistoryService historyService;
    private final org.flowable.engine.TaskService flowableTaskService;

    public Map<String, Object> prepareStartVariables(Map<String, Object> variables, Long starterId) {
        Map<String, Object> startVariables = new HashMap<>();
        if (variables != null) {
            startVariables.putAll(variables);
        }
        if (!startVariables.containsKey(VARIABLE_ASSIGNEE)) {
            startVariables.put(VARIABLE_ASSIGNEE, String.valueOf(starterId));
        } else {
            Object assigneeValue = startVariables.get(VARIABLE_ASSIGNEE);
            startVariables.put(VARIABLE_ASSIGNEE, String.valueOf(assigneeValue));
        }
        return startVariables;
    }

    public String resolveProcessName(String processDefinitionId, String fallbackProcessKey) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        if (processDefinition != null && processDefinition.getName() != null) {
            return processDefinition.getName();
        }
        return fallbackProcessKey;
    }

    public void syncActiveTasks(WfProcessInstance processInstance) {
        syncRuntimeTasks(processInstance);
    }

    public void refreshProcessStatus(Long processInstanceId, String action) {
        WfProcessInstance processInstance = processInstanceMapper.selectById(processInstanceId);
        if (processInstance != null) {
            refreshProcessStatus(processInstance, action);
        }
    }

    public void refreshProcessStatus(WfProcessInstance processInstance) {
        refreshProcessStatus(processInstance, null);
    }

    public void refreshProcessStatus(WfProcessInstance processInstance, String action) {
        org.flowable.engine.runtime.ProcessInstance runtimeProcess = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getFlowableInstanceId())
                .singleResult();
        if (runtimeProcess != null) {
            syncRuntimeTasks(processInstance);
            if (!Objects.equals(processInstance.getStatus(), PROCESS_STATUS_RUNNING)) {
                processInstance.setStatus(PROCESS_STATUS_RUNNING);
                processInstance.setEndTime(null);
                processInstanceMapper.updateById(processInstance);
            }
            return;
        }

        org.flowable.engine.history.HistoricProcessInstance historicProcess = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstance.getFlowableInstanceId())
                .singleResult();
        String targetStatus = resolveTerminalStatus(processInstance.getId(), action);
        processInstance.setStatus(targetStatus);
        processInstance.setEndTime(historicProcess != null ? toLocalDateTime(historicProcess.getEndTime()) : now());
        processInstanceMapper.updateById(processInstance);
    }

    public ProcessProgressVO buildProgress(String businessKey) {
        WfProcessInstance processInstance = processInstanceMapper.selectOne(new LambdaQueryWrapper<WfProcessInstance>()
                .eq(WfProcessInstance::getBusinessKey, businessKey)
                .last("limit 1"));
        if (processInstance == null) {
            return null;
        }
        refreshProcessStatus(processInstance);

        List<WfTask> tasks = taskMapper.selectList(new LambdaQueryWrapper<WfTask>()
                .eq(WfTask::getProcessInstanceId, processInstance.getId())
                .orderByAsc(WfTask::getId));

        ProcessProgressVO vo = new ProcessProgressVO();
        vo.setProcessKey(processInstance.getProcessKey());
        vo.setBusinessKey(processInstance.getBusinessKey());
        vo.setStatus(processInstance.getStatus());
        vo.setStartTime(processInstance.getStartTime());
        vo.setEndTime(processInstance.getEndTime());
        vo.setTaskList(tasks.stream().map(task -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", task.getId());
            item.put("taskName", task.getTaskName());
            item.put("assigneeId", task.getAssigneeId());
            item.put("status", task.getStatus());
            item.put("action", task.getAction());
            item.put("comment", task.getComment());
            item.put("claimTime", task.getClaimTime());
            item.put("completeTime", task.getCompleteTime());
            return item;
        }).toList());
        return vo;
    }

    public Map<String, Object> buildCompleteVariables(String action, List<Long> nextAssigneeIds) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(VARIABLE_APPROVED, !ACTION_REJECT.equals(action));
        if (nextAssigneeIds != null && !nextAssigneeIds.isEmpty()) {
            variables.put(VARIABLE_NEXT_ASSIGNEE_IDS, nextAssigneeIds);
            variables.put(VARIABLE_ASSIGNEE, String.valueOf(nextAssigneeIds.get(0)));
        }
        return variables;
    }

    public String normalizeAction(String action) {
        if (action == null || action.isBlank()) {
            return ACTION_APPROVE;
        }
        String normalized = action.trim().toUpperCase();
        if (!ACTION_APPROVE.equals(normalized) && !ACTION_REJECT.equals(normalized) && !ACTION_TRANSFER.equals(normalized)) {
            throw taskCompleteException("不支持的审批动作: " + action);
        }
        return normalized;
    }

    public Long extractTransferAssignee(List<Long> nextAssigneeIds) {
        if (nextAssigneeIds == null || nextAssigneeIds.isEmpty() || nextAssigneeIds.get(0) == null) {
            throw taskCompleteException("转办必须指定下一处理人");
        }
        return nextAssigneeIds.get(0);
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    public LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    }

    public BusinessException processStartException(String message) {
        return new BusinessException(ErrorCode.PROCESS_START_FAIL.getCode(), message);
    }

    public BusinessException processStartException(String message, Throwable cause) {
        return new BusinessException(ErrorCode.PROCESS_START_FAIL.getCode(), message, cause);
    }

    public BusinessException taskCompleteException(String message) {
        return new BusinessException(ErrorCode.TASK_COMPLETE_FAIL.getCode(), message);
    }

    public BusinessException taskCompleteException(String message, Throwable cause) {
        return new BusinessException(ErrorCode.TASK_COMPLETE_FAIL.getCode(), message, cause);
    }

    private void syncRuntimeTasks(WfProcessInstance processInstance) {
        List<Task> activeTasks = flowableTaskService.createTaskQuery()
                .processInstanceId(processInstance.getFlowableInstanceId())
                .active()
                .list();
        for (Task activeTask : activeTasks) {
            upsertPendingTask(processInstance, activeTask);
        }
    }

    private void upsertPendingTask(WfProcessInstance processInstance, Task activeTask) {
        WfTask existing = taskMapper.selectOne(new LambdaQueryWrapper<WfTask>()
                .eq(WfTask::getFlowableTaskId, activeTask.getId())
                .eq(WfTask::getStatus, TASK_STATUS_PENDING)
                .orderByDesc(WfTask::getId)
                .last("limit 1"));

        Long assigneeId = resolveAssigneeId(activeTask, existing);
        if (existing == null) {
            WfTask newTask = new WfTask();
            newTask.setProcessInstanceId(processInstance.getId());
            newTask.setFlowableTaskId(activeTask.getId());
            newTask.setTaskName(activeTask.getName());
            newTask.setAssigneeId(assigneeId);
            newTask.setClaimTime(toLocalDateTime(activeTask.getCreateTime()));
            newTask.setStatus(TASK_STATUS_PENDING);
            taskMapper.insert(newTask);
            return;
        }

        existing.setTaskName(activeTask.getName());
        existing.setAssigneeId(assigneeId);
        existing.setClaimTime(toLocalDateTime(activeTask.getCreateTime()));
        taskMapper.updateById(existing);
    }

    private Long resolveAssigneeId(Task activeTask, WfTask existing) {
        if (StringUtils.hasText(activeTask.getAssignee())) {
            try {
                return Long.valueOf(activeTask.getAssignee());
            } catch (NumberFormatException ex) {
                throw taskCompleteException("任务审批人必须为数字用户ID: " + activeTask.getAssignee(), ex);
            }
        }
        if (existing != null && existing.getAssigneeId() != null) {
            return existing.getAssigneeId();
        }
        throw taskCompleteException("流程任务未配置审批人: " + activeTask.getName());
    }

    private String resolveTerminalStatus(Long processInstanceId, String action) {
        if (ACTION_REJECT.equals(action)) {
            return PROCESS_STATUS_REJECTED;
        }
        WfTask latestTask = taskMapper.selectOne(new LambdaQueryWrapper<WfTask>()
                .eq(WfTask::getProcessInstanceId, processInstanceId)
                .isNotNull(WfTask::getAction)
                .orderByDesc(WfTask::getId)
                .last("limit 1"));
        if (latestTask != null && ACTION_REJECT.equals(latestTask.getAction())) {
            return PROCESS_STATUS_REJECTED;
        }
        return PROCESS_STATUS_COMPLETED;
    }
}
