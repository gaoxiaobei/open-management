package com.openmanagement.auth.controller;

import com.openmanagement.auth.dto.CaptchaResponse;
import com.openmanagement.auth.dto.LoginRequest;
import com.openmanagement.auth.dto.LoginResponse;
import com.openmanagement.auth.service.CaptchaService;
import com.openmanagement.auth.service.LoginService;
import com.openmanagement.common.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final CaptchaService captchaService;

    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(loginService.login(request));
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        loginService.logout();
        return R.ok();
    }

    @GetMapping("/captcha")
    public R<CaptchaResponse> captcha() {
        return R.ok(captchaService.generateCaptcha());
    }
}
