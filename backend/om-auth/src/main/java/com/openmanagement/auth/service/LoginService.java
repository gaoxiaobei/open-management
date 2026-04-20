package com.openmanagement.auth.service;

import com.openmanagement.auth.dto.LoginRequest;
import com.openmanagement.auth.dto.LoginResponse;

public interface LoginService {

    LoginResponse login(LoginRequest request);

    void logout();
}
