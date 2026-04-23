package com.openmanagement.auth.service.impl;

import com.openmanagement.auth.dto.CaptchaResponse;
import com.openmanagement.common.constant.CommonConstants;
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
class CaptchaServiceImplTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @InjectMocks
    private CaptchaServiceImpl captchaService;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    void generateCaptchaShouldReturnUniqueKeyEachTime() {
        CaptchaResponse r1 = captchaService.generateCaptcha();
        CaptchaResponse r2 = captchaService.generateCaptcha();

        assertNotNull(r1.getCaptchaKey());
        assertNotNull(r2.getCaptchaKey());
        assertNotEquals(r1.getCaptchaKey(), r2.getCaptchaKey());
    }

    @Test
    void generateCaptchaShouldReturnBase64PngImage() {
        CaptchaResponse response = captchaService.generateCaptcha();

        assertTrue(response.getCaptchaImage().startsWith("data:image/png;base64,"));
    }

    @Test
    void generateCaptchaShouldStoreCodeInRedisWithFiveMinuteTtl() {
        captchaService.generateCaptcha();

        verify(valueOps).set(
                startsWith(CommonConstants.CAPTCHA_CACHE_KEY),
                anyString(),
                eq(5L),
                eq(TimeUnit.MINUTES));
    }

    @Test
    void validateCaptchaShouldReturnFalseWhenKeyIsNull() {
        assertFalse(captchaService.validateCaptcha(null, "sixd"));
        verifyNoInteractions(valueOps);
    }

    @Test
    void validateCaptchaShouldReturnFalseWhenCodeIsNull() {
        assertFalse(captchaService.validateCaptcha("somekey", null));
        verifyNoInteractions(valueOps);
    }

    @Test
    void validateCaptchaShouldReturnFalseWhenKeyNotFoundInRedis() {
        when(valueOps.get(anyString())).thenReturn(null);

        assertFalse(captchaService.validateCaptcha("missingkey", "sixd"));
    }

    @Test
    void validateCaptchaShouldReturnFalseWhenCodeMismatch() {
        when(valueOps.get(anyString())).thenReturn("sixd");

        assertFalse(captchaService.validateCaptcha("key", "wxyz"));
    }

    @Test
    void validateCaptchaShouldBeCaseInsensitive() {
        when(valueOps.get(anyString())).thenReturn("sixd");

        assertTrue(captchaService.validateCaptcha("key", "ABCD"));
    }

    @Test
    void validateCaptchaShouldDeleteRedisKeyOnSuccessfulValidation() {
        when(valueOps.get(anyString())).thenReturn("sixd");

        captchaService.validateCaptcha("key", "sixd");

        verify(stringRedisTemplate).delete(CommonConstants.CAPTCHA_CACHE_KEY + "key");
    }

    @Test
    void validateCaptchaShouldNotDeleteKeyOnFailedValidation() {
        when(valueOps.get(anyString())).thenReturn("sixd");

        captchaService.validateCaptcha("key", "xxxx");

        verify(stringRedisTemplate, never()).delete(anyString());
    }
}
