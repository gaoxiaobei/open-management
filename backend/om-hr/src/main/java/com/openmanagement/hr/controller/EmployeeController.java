package com.openmanagement.hr.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.annotation.RequirePermission;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import com.openmanagement.hr.domain.entity.HrEmployee;
import com.openmanagement.hr.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @RequirePermission("hr:employee:query")
    public R<PageResult<HrEmployee>> pageEmployees(PageQuery pageQuery,
                                                   @RequestParam(required = false) String empName,
                                                   @RequestParam(required = false) Long deptId,
                                                   @RequestParam(required = false) String empStatus) {
        return R.ok(employeeService.pageEmployees(pageQuery, empName, deptId, empStatus));
    }

    @PostMapping
    @RequirePermission("hr:employee:create")
    public R<Void> createEmployee(@RequestBody HrEmployee employee) {
        employeeService.createEmployee(employee);
        return R.ok();
    }

    @GetMapping("/{id}")
    @RequirePermission("hr:employee:query")
    public R<HrEmployee> getEmployee(@PathVariable Long id) {
        return R.ok(employeeService.getById(id));
    }
}
