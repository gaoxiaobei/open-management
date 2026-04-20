package com.openmanagement.org.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.openmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deptCode;

    private String deptName;

    private Long parentId;

    private String deptType;

    private Long leaderUserId;

    private Integer sortOrder;

    private String status;

    @TableField(exist = false)
    private List<SysDept> children;
}
