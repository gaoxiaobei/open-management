package com.openmanagement.audit.service.impl;

import com.openmanagement.audit.domain.entity.SysLoginLog;
import com.openmanagement.audit.service.LoginLogService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    @Override
    public void recordLogin(SysLoginLog log) {
        // TODO: persist login log to database
    }

    @Override
    public PageResult<SysLoginLog> pageLoginLogs(PageQuery pageQuery, Long userId) {
        // TODO: query login logs with pagination, filter by userId
        return PageResult.of(0L, Collections.emptyList());
    }
}
