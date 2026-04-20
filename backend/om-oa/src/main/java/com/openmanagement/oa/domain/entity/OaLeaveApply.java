package com.openmanagement.oa.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.openmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_leave_apply")
public class OaLeaveApply extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String applyNo;

    private Long applicantId;

    private String leaveType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal leaveDays;

    private String reason;

    private Long processInstanceId;

    private String applyStatus;
}
