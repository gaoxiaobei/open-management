package com.openmanagement.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.openmanagement.workflow.domain.entity.WfProcessInstance;
import com.openmanagement.workflow.mapper.ProcessInstanceMapper;
import com.openmanagement.workflow.service.ProcessInstanceService;
import com.openmanagement.workflow.support.WorkflowRuntimeSupport;
import com.openmanagement.workflow.vo.ProcessProgressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    private final ProcessInstanceMapper processInstanceMapper;
    private final org.flowable.engine.RuntimeService runtimeService;
    private final WorkflowRuntimeSupport workflowRuntimeSupport;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startProcess(String processKey, String businessKey, String businessType,
                             Long starterId, Map<String, Object> variables) {
        if (!StringUtils.hasText(processKey)) {
            throw workflowRuntimeSupport.processStartException("流程标识不能为空");
        }
        if (!StringUtils.hasText(businessKey)) {
            throw workflowRuntimeSupport.processStartException("业务单号不能为空");
        }
        if (starterId == null) {
            throw workflowRuntimeSupport.processStartException("发起人不能为空");
        }

        long exists = processInstanceMapper.selectCount(new LambdaQueryWrapper<WfProcessInstance>()
                .eq(WfProcessInstance::getBusinessKey, businessKey));
        if (exists > 0) {
            throw workflowRuntimeSupport.processStartException("业务单号已存在流程实例");
        }

        try {
            Map<String, Object> startVariables = workflowRuntimeSupport.prepareStartVariables(variables, starterId);
            org.flowable.engine.runtime.ProcessInstance flowableInstance =
                    runtimeService.startProcessInstanceByKey(processKey, businessKey, startVariables);

            WfProcessInstance processInstance = new WfProcessInstance();
            processInstance.setProcessKey(processKey);
            processInstance.setProcessName(
                    workflowRuntimeSupport.resolveProcessName(flowableInstance.getProcessDefinitionId(), processKey));
            processInstance.setBusinessKey(businessKey);
            processInstance.setBusinessType(businessType);
            processInstance.setStarterId(starterId);
            processInstance.setStartTime(workflowRuntimeSupport.now());
            processInstance.setStatus(WorkflowRuntimeSupport.PROCESS_STATUS_RUNNING);
            processInstance.setFlowableInstanceId(flowableInstance.getId());
            processInstanceMapper.insert(processInstance);
            workflowRuntimeSupport.syncActiveTasks(processInstance);
        } catch (DuplicateKeyException ex) {
            throw workflowRuntimeSupport.processStartException("业务单号已存在流程实例");
        } catch (Exception ex) {
            throw workflowRuntimeSupport.processStartException("流程启动失败", ex);
        }
    }

    @Override
    public WfProcessInstance getByBusinessKey(String businessKey) {
        WfProcessInstance processInstance = processInstanceMapper.selectOne(new LambdaQueryWrapper<WfProcessInstance>()
                .eq(WfProcessInstance::getBusinessKey, businessKey)
                .last("limit 1"));
        if (processInstance != null) {
            workflowRuntimeSupport.refreshProcessStatus(processInstance);
        }
        return processInstance;
    }

    @Override
    public ProcessProgressVO getProgress(String businessKey) {
        return workflowRuntimeSupport.buildProgress(businessKey);
    }
}
