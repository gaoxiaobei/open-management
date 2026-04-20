package com.openmanagement.audit.service.impl;

import com.openmanagement.audit.domain.entity.SysOperateLog;
import com.openmanagement.audit.service.OperateLogService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OperateLogServiceImpl implements OperateLogService {

    @Override
    public void recordOperate(SysOperateLog log) {
        // TODO: persist operate log to database
    }

    @Override
    public PageResult<SysOperateLog> pageOperateLogs(PageQuery pageQuery, Long userId) {
        // TODO: query operate logs with pagination, filter by userId
        return PageResult.of(0L, Collections.emptyList());
    }
}
