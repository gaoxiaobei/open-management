package com.openmanagement.audit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openmanagement.audit.domain.entity.SysOperateLog;
import com.openmanagement.audit.mapper.OperateLogMapper;
import com.openmanagement.audit.service.OperateLogService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperateLogServiceImpl implements OperateLogService {

    private final OperateLogMapper operateLogMapper;

    @Override
    public void recordOperate(SysOperateLog log) {
        operateLogMapper.insert(log);
    }

    @Override
    public PageResult<SysOperateLog> pageOperateLogs(PageQuery pageQuery, Long userId) {
        Page<SysOperateLog> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<SysOperateLog> wrapper = new LambdaQueryWrapper<SysOperateLog>()
                .eq(userId != null, SysOperateLog::getUserId, userId)
                .orderByDesc(SysOperateLog::getOperateTime)
                .orderByDesc(SysOperateLog::getId);
        Page<SysOperateLog> result = operateLogMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
}
