package com.openmanagement.workflow.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ProcessProgressVO {

    private String processKey;
    private String businessKey;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Map<String, Object>> taskList;
}
