package com.openmanagement.workflow.controller;

import com.openmanagement.common.result.R;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.workflow.domain.entity.WfProcessInstance;
import com.openmanagement.workflow.domain.entity.WfTask;
import com.openmanagement.workflow.service.ProcessInstanceService;
import com.openmanagement.workflow.service.TaskService;
import com.openmanagement.workflow.vo.ProcessProgressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        String processKey = (String) body.get("processKey");
        String businessKey = (String) body.get("businessKey");
        String businessType = (String) body.get("businessType");
        Long starterId = body.get("starterId") != null
                ? Long.valueOf(body.get("starterId").toString())
                : UserContext.getUserId();
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) body.getOrDefault(
                "variables", body.getOrDefault("formData", Map.of()));
        processInstanceService.startProcess(processKey, businessKey, businessType, starterId, variables);
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
        Long currentAssigneeId = assigneeId != null ? assigneeId : UserContext.getUserId();
        if (currentAssigneeId == null) {
            return R.ok(List.of());
        }
        return R.ok(taskService.listPendingTasks(currentAssigneeId));
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
        if (!(value instanceof List<?> rawList)) {
            return List.of();
        }
        List<Long> result = new ArrayList<>(rawList.size());
        for (Object item : rawList) {
            if (item != null) {
                result.add(Long.valueOf(item.toString()));
            }
        }
        return result;
    }
}
