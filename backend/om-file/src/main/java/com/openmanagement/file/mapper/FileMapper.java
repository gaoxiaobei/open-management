package com.openmanagement.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openmanagement.file.domain.entity.SysFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<SysFile> {
}
