package com.openmanagement.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openmanagement.workflow.domain.entity.WfProcessInstance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProcessInstanceMapper extends BaseMapper<WfProcessInstance> {
}
