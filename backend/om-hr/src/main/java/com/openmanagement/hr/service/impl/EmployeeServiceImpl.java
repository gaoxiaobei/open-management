package com.openmanagement.hr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.hr.domain.entity.HrEmployee;
import com.openmanagement.hr.mapper.EmployeeMapper;
import com.openmanagement.hr.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, HrEmployee> implements EmployeeService {

    @Override
    public PageResult<HrEmployee> pageEmployees(PageQuery pageQuery, String empName, Long deptId, String empStatus) {
        Page<HrEmployee> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<HrEmployee> result = page(page, new LambdaQueryWrapper<HrEmployee>()
                .like(StringUtils.hasText(empName), HrEmployee::getEmpName, empName)
                .eq(deptId != null, HrEmployee::getDeptId, deptId)
                .eq(StringUtils.hasText(empStatus), HrEmployee::getEmpStatus, empStatus)
                .orderByDesc(HrEmployee::getCreatedAt, HrEmployee::getId));
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createEmployee(HrEmployee employee) {
        if (employee == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "员工信息不能为空");
        }
        if (!StringUtils.hasText(employee.getEmpNo())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "工号不能为空");
        }
        if (!StringUtils.hasText(employee.getEmpName())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "姓名不能为空");
        }
        if (employee.getDeptId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "部门不能为空");
        }
        if (employee.getHireDate() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "入职日期不能为空");
        }
        long count = count(new LambdaQueryWrapper<HrEmployee>()
                .eq(HrEmployee::getEmpNo, employee.getEmpNo()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "工号已存在");
        }
        employee.setId(null);
        try {
            save(employee);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "工号已存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmployee(Long id, HrEmployee employee) {
        if (employee == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "员工信息不能为空");
        }
        HrEmployee existing = getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "员工不存在");
        }
        if (StringUtils.hasText(employee.getEmpNo())
                && !employee.getEmpNo().equals(existing.getEmpNo())) {
            long count = count(new LambdaQueryWrapper<HrEmployee>()
                    .eq(HrEmployee::getEmpNo, employee.getEmpNo()));
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "工号已存在");
            }
        }
        employee.setId(id);
        try {
            updateById(employee);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "工号已存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeEmpStatus(Long id, String empStatus) {
        HrEmployee existing = getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "员工不存在");
        }
        if (!StringUtils.hasText(empStatus)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "在职状态不能为空");
        }
        existing.setEmpStatus(empStatus);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployee(Long id) {
        if (getById(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "员工不存在");
        }
        removeById(id);
    }
}
