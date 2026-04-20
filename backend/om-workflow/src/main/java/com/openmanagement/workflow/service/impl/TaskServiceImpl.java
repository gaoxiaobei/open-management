package com.openmanagement.workflow.service.impl;

import com.openmanagement.workflow.domain.entity.WfTask;
import com.openmanagement.workflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    @Override
    public void completeTask(Long taskId, String action, String comment, List<Long> nextAssigneeIds) {
        // TODO: call Flowable TaskService to complete task, update WfTask record
    }

    @Override
    public List<WfTask> listPendingTasks(Long assigneeId) {
        // TODO: query pending tasks for assignee
        return Collections.emptyList();
    }
}
