package com.openmanagement.workflow.service.impl;

import com.openmanagement.workflow.domain.entity.WfProcessInstance;
import com.openmanagement.workflow.service.ProcessInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    @Override
    public void startProcess(String processKey, String businessKey, String businessType,
                             Long starterId, Map<String, Object> variables) {
        // TODO: call Flowable RuntimeService to start process, persist WfProcessInstance
    }

    @Override
    public WfProcessInstance getByBusinessKey(String businessKey) {
        // TODO: query WfProcessInstance by businessKey
        return null;
    }
}
