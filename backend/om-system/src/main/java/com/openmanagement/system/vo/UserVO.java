package com.openmanagement.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String realName;
    private String mobile;
    private String email;
    private Long deptId;
    private String deptName;
    private Long positionId;
    private String positionName;
    private String status;
    private LocalDateTime lastLoginTime;
}
