package com.openmanagement.workflow.service;

import com.openmanagement.workflow.domain.entity.WfProcessInstance;

import java.util.Map;

public interface ProcessInstanceService {

    void startProcess(String processKey, String businessKey, String businessType,
                      Long starterId, Map<String, Object> variables);

    WfProcessInstance getByBusinessKey(String businessKey);
}
