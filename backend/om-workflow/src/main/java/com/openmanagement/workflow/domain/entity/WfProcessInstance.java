package com.openmanagement.workflow.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.openmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_process_instance")
public class WfProcessInstance extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String processKey;

    private String processName;

    private String businessKey;

    private String businessType;

    private Long starterId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    private String flowableInstanceId;
}
