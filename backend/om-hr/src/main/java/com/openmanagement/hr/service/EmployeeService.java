package com.openmanagement.hr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.hr.domain.entity.HrEmployee;

public interface EmployeeService extends IService<HrEmployee> {

    PageResult<HrEmployee> pageEmployees(PageQuery pageQuery, String empName, Long deptId, String empStatus);

    void createEmployee(HrEmployee employee);
}
