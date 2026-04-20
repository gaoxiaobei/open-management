package com.openmanagement.auth.service;

public interface PasswordPolicyService {
    boolean matches(String rawPassword, String encodedPassword);
    String encode(String rawPassword);
}
