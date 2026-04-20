package com.openmanagement.hr.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.openmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_employee")
public class HrEmployee extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String empNo;

    private String empName;

    private String gender;

    private String idNo;

    private Long deptId;

    private Long positionId;

    private LocalDate hireDate;

    private String empStatus;

    private String mobile;

    private String email;
}
