package com.openmanagement.workflow.vo;

import com.openmanagement.workflow.domain.entity.WfTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WfTaskVO extends WfTask {

    private String assigneeName;
}
