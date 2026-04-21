package com.openmanagement.workflow.controller;

import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.common.result.R;
import com.openmanagement.workflow.domain.entity.WfProcessInstance;
import com.openmanagement.workflow.domain.entity.WfTask;
import com.openmanagement.workflow.service.ProcessInstanceService;
import com.openmanagement.workflow.service.TaskService;
import com.openmanagement.workflow.vo.ProcessProgressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final ProcessInstanceService processInstanceService;
    private final TaskService taskService;

    @PostMapping("/process/start")
    public R<Void> startProcess(@RequestBody Map<String, Object> body) {
        Long currentUserId = requireCurrentUserId();
        String processKey = (String) body.get("processKey");
        String businessKey = (String) body.get("businessKey");
        String businessType = (String) body.get("businessType");
        Map<String, Object> variables = extractVariables(body);
        processInstanceService.startProcess(processKey, businessKey, businessType, currentUserId, variables);
        return R.ok();
    }

    @PostMapping("/tasks/{taskId}/complete")
    public R<Void> completeTask(@PathVariable Long taskId, @RequestBody Map<String, Object> body) {
        String action = (String) body.get("action");
        String comment = (String) body.get("comment");
        List<Long> nextAssigneeIds = toLongList(body.get("nextAssigneeIds"));
        taskService.completeTask(taskId, action, comment, nextAssigneeIds);
        return R.ok();
    }

    @GetMapping("/tasks/pending")
    public R<List<WfTask>> pendingTasks(@RequestParam(required = false) Long assigneeId) {
        Long currentUserId = requireCurrentUserId();
        Long targetAssigneeId = currentUserId;
        if (assigneeId != null) {
            if (!assigneeId.equals(currentUserId) && !isAdmin(currentUserId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "无权限查看其他用户待办");
            }
            targetAssigneeId = assigneeId;
        }
        return R.ok(taskService.listPendingTasks(targetAssigneeId));
    }

    @GetMapping("/process/by-business-key")
    public R<WfProcessInstance> processByBusinessKey(@RequestParam String businessKey) {
        return R.ok(processInstanceService.getByBusinessKey(businessKey));
    }

    @GetMapping("/process/progress")
    public R<ProcessProgressVO> processProgress(@RequestParam String businessKey) {
        return R.ok(processInstanceService.getProgress(businessKey));
    }

    private List<Long> toLongList(Object value) {
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof List<?> rawList)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "nextAssigneeIds 必须为数组");
        }
        List<Long> result = new java.util.ArrayList<>(rawList.size());
        for (int i = 0; i < rawList.size(); i++) {
            Object item = rawList.get(i);
            if (item == null) {
                continue;
            }
            try {
                result.add(Long.valueOf(item.toString()));
            } catch (NumberFormatException ex) {
                throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(),
                        "nextAssigneeIds[" + i + "] 必须为数字");
            }
        }
        return result;
    }

    private Map<String, Object> extractVariables(Map<String, Object> body) {
        Object variables = body.containsKey("variables") ? body.get("variables") : body.get("formData");
        if (variables == null) {
            return Map.of();
        }
        if (!(variables instanceof Map<?, ?> rawMap)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "variables/formData 必须为对象");
        }

        Map<String, Object> result = new LinkedHashMap<>(rawMap.size());
        rawMap.forEach((key, value) -> result.put(String.valueOf(key), value));
        return result;
    }

    private Long requireCurrentUserId() {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());
        }
        return currentUserId;
    }

    private boolean isAdmin(Long userId) {
        return CommonConstants.ADMIN_USER_ID.equals(userId);
    }
}
