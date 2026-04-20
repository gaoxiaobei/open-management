package com.openmanagement.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openmanagement.message.domain.entity.SysMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<SysMessage> {
}
