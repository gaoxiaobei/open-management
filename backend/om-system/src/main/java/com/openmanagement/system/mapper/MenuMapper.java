package com.openmanagement.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openmanagement.system.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MenuMapper extends BaseMapper<SysMenu> {
}
