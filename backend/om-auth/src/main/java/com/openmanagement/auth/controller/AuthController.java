package com.openmanagement.auth.controller;

import com.openmanagement.auth.dto.LoginRequest;
import com.openmanagement.auth.dto.LoginResponse;
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

    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(loginService.login(request));
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        loginService.logout();
        return R.ok();
    }
}
