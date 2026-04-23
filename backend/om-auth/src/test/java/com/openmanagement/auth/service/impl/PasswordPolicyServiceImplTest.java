package com.openmanagement.auth.service.impl;

import com.openmanagement.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyServiceImplTest {

    private final PasswordPolicyServiceImpl service =
            new PasswordPolicyServiceImpl(new BCryptPasswordEncoder());

    @Test
    void checkStrengthShouldThrowWhenPasswordIsNull() {
        assertThrows(BusinessException.class, () -> service.checkStrength(null));
    }

    @Test
    void checkStrengthShouldThrowWhenPasswordTooShort() {
        assertThrows(BusinessException.class, () -> service.checkStrength("Ab1"));
    }

    @Test
    void checkStrengthShouldThrowWhenMissingUpperCase() {
        assertThrows(BusinessException.class, () -> service.checkStrength("sixdefg1"));
    }

    @Test
    void checkStrengthShouldThrowWhenMissingLowerCase() {
        assertThrows(BusinessException.class, () -> service.checkStrength("ABCDEFG1"));
    }

    @Test
    void checkStrengthShouldThrowWhenMissingDigit() {
        assertThrows(BusinessException.class, () -> service.checkStrength("ABCDefgh"));
    }

    @Test
    void checkStrengthShouldPassForValidPassword() {
        assertDoesNotThrow(() -> service.checkStrength("Password1"));
    }

    @Test
    void checkStrengthShouldPassForMinimumEightCharsMixedPassword() {
        assertDoesNotThrow(() -> service.checkStrength("Passw0rd"));
    }

    @Test
    void encodedPasswordShouldMatchRawPassword() {
        String raw = "Password1";
        String encoded = service.encode(raw);
        assertTrue(service.matches(raw, encoded));
    }

    @Test
    void matchesShouldReturnFalseForWrongPassword() {
        String encoded = service.encode("Password1");
        assertFalse(service.matches("WrongPass1", encoded));
    }
}
