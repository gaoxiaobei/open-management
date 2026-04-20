package com.openmanagement.oa.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.openmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_travel_apply")
public class OaTravelApply extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String applyNo;

    private Long applicantId;

    private String destination;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer travelDays;

    private String purpose;

    private Long processInstanceId;

    private String applyStatus;
}
