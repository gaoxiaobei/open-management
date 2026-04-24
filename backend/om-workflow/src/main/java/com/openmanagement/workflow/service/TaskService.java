package com.openmanagement.workflow.service;

import com.openmanagement.workflow.domain.entity.WfTask;
import com.openmanagement.workflow.vo.WfTaskVO;

import java.util.List;

public interface TaskService {

    void completeTask(Long taskId, String action, String comment, List<Long> nextAssigneeIds);

    List<WfTaskVO> listPendingTasks(Long assigneeId);
}
