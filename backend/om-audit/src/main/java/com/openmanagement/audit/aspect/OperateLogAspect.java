package com.openmanagement.audit.aspect;

import com.openmanagement.audit.domain.entity.SysOperateLog;
import com.openmanagement.audit.service.OperateLogService;
import com.openmanagement.common.annotation.OperateLog;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.common.util.IpUtils;
import com.openmanagement.common.result.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class OperateLogAspect {

    private static final int MAX_LOG_LENGTH = 2000;

    private final OperateLogService operateLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.openmanagement.common.annotation.OperateLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        LocalDateTime startTime = LocalDateTime.now();
        long start = System.currentTimeMillis();
        Object result = null;
        Throwable throwable = null;
        try {
            result = point.proceed();
            return result;
        } catch (Throwable ex) {
            throwable = ex;
            throw ex;
        } finally {
            saveOperateLog(point, result, throwable, startTime, System.currentTimeMillis() - start);
        }
    }

    private void saveOperateLog(ProceedingJoinPoint point,
                                Object result,
                                Throwable throwable,
                                LocalDateTime operateTime,
                                long costTime) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        OperateLog annotation = signature.getMethod().getAnnotation(OperateLog.class);
        HttpServletRequest request = getCurrentRequest();

        SysOperateLog log = new SysOperateLog();
        log.setModuleName(annotation.module());
        log.setOperationName(annotation.name());
        log.setRequestMethod(request == null ? null : request.getMethod());
        log.setRequestUrl(request == null ? null : request.getRequestURI());
        log.setRequestParams(truncate(safeToJson(point.getArgs())));
        log.setResponseResult(truncate(throwable == null
                ? safeToJson(result)
                : ("ERROR: " + throwable.getClass().getSimpleName() + " - " + throwable.getMessage())));
        log.setUserId(UserContext.getUserId());
        log.setUsername(UserContext.getUsername());
        log.setIpAddr(request == null ? null : IpUtils.getIpAddr(request));
        log.setOperateTime(operateTime);
        log.setCostTime(costTime);
        operateLogService.recordOperate(log);
    }

    private HttpServletRequest getCurrentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            return servletAttributes.getRequest();
        }
        return null;
    }

    private String safeToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof R<?>) {
            return obj.toString();
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return String.valueOf(obj);
        }
    }

    private String truncate(String value) {
        if (value == null || value.length() <= MAX_LOG_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_LOG_LENGTH);
    }
}
