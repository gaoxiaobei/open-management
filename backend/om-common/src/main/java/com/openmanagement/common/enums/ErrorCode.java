package com.openmanagement.common.enums;

public enum ErrorCode {
    SUCCESS(200, "操作成功"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(400, "参数校验失败"),
    INTERNAL_ERROR(500, "系统内部错误"),
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    PASSWORD_ERROR(1003, "用户名或密码错误"),
    CAPTCHA_ERROR(1004, "验证码错误或已过期"),
    ACCOUNT_LOCKED(1005, "账号已被锁定"),
    DEPT_NOT_FOUND(2001, "部门不存在"),
    ROLE_NOT_FOUND(3001, "角色不存在"),
    MENU_NOT_FOUND(4001, "菜单不存在"),
    FILE_NOT_FOUND(5001, "文件不存在"),
    FILE_UPLOAD_FAIL(5002, "文件上传失败"),
    PROCESS_START_FAIL(6001, "流程启动失败"),
    TASK_COMPLETE_FAIL(6002, "任务完成失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}
