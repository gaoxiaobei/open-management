package com.openmanagement.audit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openmanagement.audit.domain.entity.SysLoginLog;
import com.openmanagement.audit.mapper.LoginLogMapper;
import com.openmanagement.audit.service.LoginLogService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    @Override
    public void recordLogin(SysLoginLog log) {
        loginLogMapper.insert(log);
    }

    @Override
    public PageResult<SysLoginLog> pageLoginLogs(PageQuery pageQuery, Long userId) {
        Page<SysLoginLog> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<SysLoginLog>()
                .eq(userId != null, SysLoginLog::getUserId, userId)
                .orderByDesc(SysLoginLog::getLoginTime)
                .orderByDesc(SysLoginLog::getId);
        Page<SysLoginLog> result = loginLogMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
}
