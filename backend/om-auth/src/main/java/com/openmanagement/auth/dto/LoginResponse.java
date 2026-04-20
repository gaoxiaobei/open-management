package com.openmanagement.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginResponse {

    private String token;
    private UserInfo userInfo;
    private List<MenuVO> menus;

    @Data
    public static class UserInfo {
        private Long userId;
        private String username;
        private String realName;
        private String avatar;
        private List<String> roles;
        private List<String> permissions;
    }

    @Data
    public static class MenuVO {
        private Long id;
        private Long parentId;
        private String menuName;
        private String menuType;
        private String path;
        private String component;
        private String icon;
        private String permissionCode;
        private Integer sortOrder;
        private List<MenuVO> children;
    }
}
