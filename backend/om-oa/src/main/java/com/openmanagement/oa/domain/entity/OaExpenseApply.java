package com.openmanagement.oa.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.openmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_expense_apply")
public class OaExpenseApply extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String applyNo;

    private Long applicantId;

    private String expenseType;

    private BigDecimal totalAmount;

    private String reason;

    private Long processInstanceId;

    private String applyStatus;
}
