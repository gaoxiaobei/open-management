package com.openmanagement.auth.service;

import com.openmanagement.auth.dto.CaptchaResponse;

public interface CaptchaService {
    CaptchaResponse generateCaptcha();
    boolean validateCaptcha(String captchaKey, String captchaCode);
}
