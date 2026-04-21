package com.openmanagement.app.aspect;

import com.openmanagement.app.security.PermissionEvaluator;
import com.openmanagement.common.annotation.RequirePermission;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionEvaluator permissionEvaluator;

    @Before("@within(requirePermission)")
    public void checkTypePermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        if (!joinPoint.getSignature().getName().equals("toString")) {
            verifyPermission(requirePermission.value());
        }
    }

    @Before("@annotation(requirePermission)")
    public void checkMethodPermission(RequirePermission requirePermission) {
        verifyPermission(requirePermission.value());
    }

    private void verifyPermission(String permission) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());
        }
        if (!permissionEvaluator.hasPermission(userId, permission)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMessage());
        }
    }
}
