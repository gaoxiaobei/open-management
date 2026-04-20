package com.openmanagement.auth.service;

public interface PasswordPolicyService {
    boolean matches(String rawPassword, String encodedPassword);
    String encode(String rawPassword);
    /**
     * 检查密码强度：至少8位，包含大写字母、小写字母和数字。
     *
     * @param rawPassword 明文密码
     * @throws com.openmanagement.common.exception.BusinessException 密码强度不足时抛出
     */
    void checkStrength(String rawPassword);
}
