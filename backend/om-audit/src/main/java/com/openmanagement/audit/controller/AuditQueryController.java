package com.openmanagement.audit.controller;

import com.openmanagement.audit.domain.entity.SysLoginLog;
import com.openmanagement.audit.domain.entity.SysOperateLog;
import com.openmanagement.audit.service.LoginLogService;
import com.openmanagement.audit.service.OperateLogService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditQueryController {

    private final LoginLogService loginLogService;
    private final OperateLogService operateLogService;

    @GetMapping("/login-logs")
    public R<PageResult<SysLoginLog>> loginLogs(PageQuery pageQuery,
                                                @RequestParam(required = false) Long userId) {
        return R.ok(loginLogService.pageLoginLogs(pageQuery, userId));
    }

    @GetMapping("/operate-logs")
    public R<PageResult<SysOperateLog>> operateLogs(PageQuery pageQuery,
                                                    @RequestParam(required = false) Long userId) {
        return R.ok(operateLogService.pageOperateLogs(pageQuery, userId));
    }
}
