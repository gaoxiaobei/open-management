package com.openmanagement.audit.service;

import com.openmanagement.audit.domain.entity.SysOperateLog;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;

public interface OperateLogService {

    void recordOperate(SysOperateLog log);

    PageResult<SysOperateLog> pageOperateLogs(PageQuery pageQuery, Long userId);
}
