package com.openmanagement.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openmanagement.audit.domain.entity.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper extends BaseMapper<SysLoginLog> {
}
