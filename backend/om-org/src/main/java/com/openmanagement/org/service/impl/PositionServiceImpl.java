package com.openmanagement.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.org.domain.entity.SysPosition;
import com.openmanagement.org.mapper.PositionMapper;
import com.openmanagement.org.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl extends ServiceImpl<PositionMapper, SysPosition> implements PositionService {

    @Override
    public List<SysPosition> listByDeptId(Long deptId) {
        return list(new LambdaQueryWrapper<SysPosition>()
                .eq(SysPosition::getDeptId, deptId)
                .orderByAsc(SysPosition::getId));
    }
}
