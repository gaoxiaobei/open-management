package com.openmanagement.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openmanagement.system.domain.entity.SysDictItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DictItemMapper extends BaseMapper<SysDictItem> {
}
