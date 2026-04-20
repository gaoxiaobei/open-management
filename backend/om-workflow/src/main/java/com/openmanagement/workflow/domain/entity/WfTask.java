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
@TableName("wf_task")
public class WfTask extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long processInstanceId;

    private String flowableTaskId;

    private String taskName;

    private Long assigneeId;

    private LocalDateTime claimTime;

    private LocalDateTime completeTime;

    private String action;

    private String comment;

    private String status;
}
