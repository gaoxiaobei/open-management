package com.openmanagement.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {

    private String token;
    private UserInfo userInfo;
    private List<Object> menus;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String realName;
        private Long deptId;
    }
}
