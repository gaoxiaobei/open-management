package com.openmanagement.hr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.hr.domain.entity.HrEmployee;
import com.openmanagement.hr.mapper.EmployeeMapper;
import com.openmanagement.hr.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, HrEmployee> implements EmployeeService {

    @Override
    public PageResult<HrEmployee> pageEmployees(PageQuery pageQuery, String empName, Long deptId, String empStatus) {
        // TODO: implement pagination query with filters
        return PageResult.of(0L, Collections.emptyList());
    }

    @Override
    public void createEmployee(HrEmployee employee) {
        // TODO: validate and persist employee record
    }
}
