package com.openmanagement.audit.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_login_log")
public class SysLoginLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private Long userId;

    private String ipAddr;

    private String browser;

    private String os;

    private String loginStatus;

    private LocalDateTime loginTime;

    private String msg;
}
