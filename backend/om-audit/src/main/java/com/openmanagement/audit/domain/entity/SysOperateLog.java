package com.openmanagement.audit.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operate_log")
public class SysOperateLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String moduleName;

    private String operationName;

    private String requestMethod;

    private String requestUrl;

    private String requestParams;

    private String responseResult;

    private Long userId;

    private String username;

    private String ipAddr;

    private LocalDateTime operateTime;

    private Long costTime;
}
