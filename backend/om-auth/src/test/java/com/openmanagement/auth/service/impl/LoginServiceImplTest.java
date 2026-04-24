package com.openmanagement.auth.service.impl;

import com.openmanagement.audit.service.LoginLogService;
import com.openmanagement.auth.dto.LoginRequest;
import com.openmanagement.auth.service.CaptchaService;
import com.openmanagement.auth.service.PasswordPolicyService;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.system.domain.entity.SysUser;
import com.openmanagement.system.service.MenuService;
import com.openmanagement.system.service.RoleService;
import com.openmanagement.system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock private CaptchaService captchaService;
    @Mock private PasswordPolicyService passwordPolicyService;
    @Mock private UserService userService;
    @Mock private RoleService roleService;
    @Mock private MenuService menuService;
    @Mock private StringRedisTemplate stringRedisTemplate;
    @Mock private LoginLogService loginLogService;
    @Mock private ValueOperations<String, String> valueOps;

    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
    }

    private LoginRequest buildRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setCaptchaKey("testkey");
        request.setCaptcha("sixd");
        return request;
    }

    private SysUser enabledUser(Long id, String username) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setPasswordHash("$2a$10$encodedHash");
        user.setStatus(CommonConstants.STATUS_NORMAL);
        return user;
    }

    @Test
    void loginShouldThrowCaptchaErrorWhenCaptchaInvalid() {
        when(captchaService.validateCaptcha(any(), any())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loginService.login(buildRequest("admin", "Password1")));

        assertEquals(ErrorCode.CAPTCHA_ERROR.getCode(), ex.getCode());
        verify(loginLogService).recordLogin(any());
    }

    @Test
    void loginShouldThrowAccountLockedWhenFailCountReachesMax() {
        when(captchaService.validateCaptcha(any(), any())).thenReturn(true);
        when(valueOps.get(anyString())).thenReturn(String.valueOf(CommonConstants.LOGIN_FAIL_MAX));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loginService.login(buildRequest("admin", "Password1")));

        assertEquals(ErrorCode.ACCOUNT_LOCKED.getCode(), ex.getCode());
    }

    @Test
    void loginShouldThrowUserNotFoundAndIncrementFailCount() {
        when(captchaService.validateCaptcha(any(), any())).thenReturn(true);
        when(valueOps.get(anyString())).thenReturn(null);
        when(userService.getUserByUsername("unknown")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loginService.login(buildRequest("unknown", "Password1")));

        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), ex.getCode());
        verify(valueOps).set(anyString(), eq("1"), eq(CommonConstants.LOGIN_LOCK_SECONDS), eq(TimeUnit.SECONDS));
    }

    @Test
    void loginShouldThrowUserNotFoundAndAccumulateExistingFailCount() {
        when(captchaService.validateCaptcha(any(), any())).thenReturn(true);
        when(valueOps.get(anyString())).thenReturn("2");
        when(userService.getUserByUsername("admin")).thenReturn(null);

        assertThrows(BusinessException.class, () -> loginService.login(buildRequest("admin", "Password1")));

        verify(valueOps).set(anyString(), eq("3"), anyLong(), any());
    }

    @Test
    void loginShouldThrowUserDisabledWithoutIncrementingFailCount() {
        SysUser user = enabledUser(1L, "admin");
        user.setStatus(CommonConstants.STATUS_DISABLED);

        when(captchaService.validateCaptcha(any(), any())).thenReturn(true);
        when(valueOps.get(anyString())).thenReturn(null);
        when(userService.getUserByUsername("admin")).thenReturn(user);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loginService.login(buildRequest("admin", "Password1")));

        assertEquals(ErrorCode.USER_DISABLED.getCode(), ex.getCode());
        verify(valueOps, never()).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void loginShouldThrowPasswordErrorAndIncrementFailCount() {
        SysUser user = enabledUser(1L, "admin");

        when(captchaService.validateCaptcha(any(), any())).thenReturn(true);
        when(valueOps.get(anyString())).thenReturn(null);
        when(userService.getUserByUsername("admin")).thenReturn(user);
        when(passwordPolicyService.matches(anyString(), anyString())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loginService.login(buildRequest("admin", "WrongPass1")));

        assertEquals(ErrorCode.PASSWORD_ERROR.getCode(), ex.getCode());
        verify(valueOps).set(anyString(), eq("1"), anyLong(), any());
    }
}
