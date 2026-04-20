package com.openmanagement.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openmanagement.system.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}
