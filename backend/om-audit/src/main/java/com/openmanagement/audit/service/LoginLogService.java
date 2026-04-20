package com.openmanagement.audit.service;

import com.openmanagement.audit.domain.entity.SysLoginLog;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;

public interface LoginLogService {

    void recordLogin(SysLoginLog log);

    PageResult<SysLoginLog> pageLoginLogs(PageQuery pageQuery, Long userId);
}
