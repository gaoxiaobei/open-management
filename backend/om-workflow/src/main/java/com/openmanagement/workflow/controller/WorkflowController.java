package com.openmanagement.workflow.controller;

import com.openmanagement.common.result.R;
import com.openmanagement.workflow.service.ProcessInstanceService;
import com.openmanagement.workflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        Long starterId = body.get("starterId") != null ? Long.valueOf(body.get("starterId").toString()) : null;
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) body.getOrDefault("variables", Map.of());
        processInstanceService.startProcess(processKey, businessKey, businessType, starterId, variables);
        return R.ok();
    }

    @PostMapping("/tasks/{taskId}/complete")
    public R<Void> completeTask(@PathVariable Long taskId, @RequestBody Map<String, Object> body) {
        String action = (String) body.get("action");
        String comment = (String) body.get("comment");
        taskService.completeTask(taskId, action, comment, null);
        return R.ok();
    }
}
