package com.openmanagement.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openmanagement.audit.domain.entity.SysOperateLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperateLogMapper extends BaseMapper<SysOperateLog> {
}
