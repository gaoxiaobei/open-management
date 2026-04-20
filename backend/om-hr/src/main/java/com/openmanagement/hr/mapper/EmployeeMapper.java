package com.openmanagement.hr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openmanagement.hr.domain.entity.HrEmployee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<HrEmployee> {
}
