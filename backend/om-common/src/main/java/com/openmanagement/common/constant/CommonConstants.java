package com.openmanagement.common.constant;

public final class CommonConstants {
    private CommonConstants() {}

    // Status
    public static final String STATUS_NORMAL = "ENABLED";
    public static final String STATUS_DISABLED = "DISABLED";

    // Logic delete
    public static final Integer NOT_DELETED = 0;
    public static final Integer DELETED = 1;

    // Admin role
    public static final String ADMIN_ROLE_CODE = "SUPER_ADMIN";
    public static final Long ADMIN_USER_ID = 1L;

    // Root parent id
    public static final Long ROOT_PARENT_ID = 0L;

    // Cache keys
    public static final String CAPTCHA_CACHE_KEY = "captcha:";
    public static final String USER_PERMISSION_CACHE_KEY = "user:perms:";
    public static final String USER_MENU_CACHE_KEY = "user:menus:";
    public static final String USER_INFO_CACHE_KEY = "user:info:";

    // Login fail count cache key
    public static final String LOGIN_FAIL_CACHE_KEY = "login:fail:";
    public static final int LOGIN_FAIL_MAX = 5;
    public static final long LOGIN_LOCK_SECONDS = 1800L; // 30 min

    // Default password
    public static final String DEFAULT_PASSWORD = "Om@123456";

    // Data scope
    public static final String DATA_SCOPE_ALL = "ALL";
    public static final String DATA_SCOPE_SELF = "SELF";
    public static final String DATA_SCOPE_DEPT = "DEPT";
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "DEPT_AND_CHILD";

    // Apply status
    public static final String APPLY_STATUS_DRAFT = "DRAFT";
    public static final String APPLY_STATUS_SUBMITTED = "SUBMITTED";
}
