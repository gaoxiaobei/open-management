package com.openmanagement.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.workflow.domain.entity.WfTask;
import com.openmanagement.workflow.mapper.TaskMapper;
import com.openmanagement.workflow.service.TaskService;
import com.openmanagement.workflow.support.WorkflowRuntimeSupport;
import com.openmanagement.workflow.vo.WfTaskVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final org.flowable.engine.TaskService flowableTaskService;
    private final WorkflowRuntimeSupport workflowRuntimeSupport;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Long taskId, String action, String comment, List<Long> nextAssigneeIds) {
        Long currentUserId = requireCurrentUserId();
        WfTask localTask = taskMapper.selectById(taskId);
        if (localTask == null) {
            log.warn("任务不存在: taskId={}", taskId);
            throw workflowRuntimeSupport.taskCompleteException("任务不存在");
        }
        if (!WorkflowRuntimeSupport.TASK_STATUS_PENDING.equals(localTask.getStatus())) {
            log.warn("任务已处理: taskId={}, status={}", taskId, localTask.getStatus());
            throw workflowRuntimeSupport.taskCompleteException("任务已处理");
        }
        if (!canOperateTask(currentUserId, localTask)) {
            log.warn("无权限处理任务: taskId={}, currentUser={}, assigneeId={}", taskId, currentUserId, localTask.getAssigneeId());
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "无权限处理该任务");
        }

        org.flowable.task.api.Task flowableTask = flowableTaskService.createTaskQuery()
                .taskId(localTask.getFlowableTaskId())
                .singleResult();
        if (flowableTask == null) {
            log.warn("Flowable任务不存在或已结束: flowableTaskId={}", localTask.getFlowableTaskId());
            throw workflowRuntimeSupport.taskCompleteException("Flowable任务不存在或已结束");
        }

        String normalizedAction = workflowRuntimeSupport.normalizeAction(action);
        if (WorkflowRuntimeSupport.ACTION_TRANSFER.equals(normalizedAction)) {
            Long nextAssigneeId = workflowRuntimeSupport.extractTransferAssignee(nextAssigneeIds);
            flowableTaskService.setAssignee(flowableTask.getId(), String.valueOf(nextAssigneeId));

            localTask.setAction(normalizedAction);
            localTask.setComment(comment);
            localTask.setCompleteTime(workflowRuntimeSupport.now());
            localTask.setStatus(WorkflowRuntimeSupport.TASK_STATUS_COMPLETED);
            taskMapper.updateById(localTask);

            WfTask transferTask = new WfTask();
            transferTask.setProcessInstanceId(localTask.getProcessInstanceId());
            transferTask.setFlowableTaskId(flowableTask.getId());
            transferTask.setTaskName(flowableTask.getName());
            transferTask.setAssigneeId(nextAssigneeId);
            transferTask.setClaimTime(workflowRuntimeSupport.now());
            transferTask.setStatus(WorkflowRuntimeSupport.TASK_STATUS_PENDING);
            taskMapper.insert(transferTask);
            return;
        }

        try {
            if (comment != null && !comment.isBlank()) {
                flowableTaskService.addComment(flowableTask.getId(), flowableTask.getProcessInstanceId(), comment);
            }

            Map<String, Object> variables = workflowRuntimeSupport.buildCompleteVariables(normalizedAction, nextAssigneeIds);
            flowableTaskService.complete(flowableTask.getId(), variables);

            localTask.setAction(normalizedAction);
            localTask.setComment(comment);
            localTask.setCompleteTime(workflowRuntimeSupport.now());
            localTask.setStatus(WorkflowRuntimeSupport.TASK_STATUS_COMPLETED);
            taskMapper.updateById(localTask);
            workflowRuntimeSupport.refreshProcessStatus(localTask.getProcessInstanceId(), normalizedAction);
        } catch (Exception ex) {
            log.error("任务处理失败: taskId={}, flowableTaskId={}, action={}", taskId, localTask.getFlowableTaskId(), action, ex);
            throw workflowRuntimeSupport.taskCompleteException("任务处理失败: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<WfTaskVO> listPendingTasks(Long assigneeId) {
        return taskMapper.selectPendingTasksWithAssigneeName(assigneeId);
    }

    private Long requireCurrentUserId() {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());
        }
        return currentUserId;
    }

    private boolean canOperateTask(Long currentUserId, WfTask localTask) {
        return CommonConstants.ADMIN_USER_ID.equals(currentUserId)
                || currentUserId.equals(localTask.getAssigneeId());
    }
}
