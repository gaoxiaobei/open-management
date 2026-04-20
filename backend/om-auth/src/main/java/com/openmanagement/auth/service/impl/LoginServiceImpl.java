package com.openmanagement.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.openmanagement.auth.dto.LoginRequest;
import com.openmanagement.auth.dto.LoginResponse;
import com.openmanagement.auth.service.LoginService;
import com.openmanagement.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    @Override
    public LoginResponse login(LoginRequest request) {
        // TODO: load user from database, verify password, build menus
        throw new BusinessException(401, "用户名或密码错误");
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }
}
