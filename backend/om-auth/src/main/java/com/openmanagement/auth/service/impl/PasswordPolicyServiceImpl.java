package com.openmanagement.auth.service.impl;

import com.openmanagement.auth.service.PasswordPolicyService;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordPolicyServiceImpl implements PasswordPolicyService {

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public void checkStrength(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "密码长度不能少于8位");
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        for (char c : rawPassword.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            if (hasUpper && hasLower && hasDigit) break;
        }
        if (!hasUpper || !hasLower || !hasDigit) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "密码必须包含大写字母、小写字母和数字");
        }
    }
}
