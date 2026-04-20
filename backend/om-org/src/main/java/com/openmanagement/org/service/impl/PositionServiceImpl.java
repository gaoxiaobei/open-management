package com.openmanagement.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.org.domain.entity.SysPosition;
import com.openmanagement.org.mapper.PositionMapper;
import com.openmanagement.org.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    @Override
    public PageResult<SysPosition> pagePositions(PageQuery pageQuery, Long deptId, String positionName) {
        Page<SysPosition> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<SysPosition> wrapper = new LambdaQueryWrapper<SysPosition>()
                .eq(deptId != null, SysPosition::getDeptId, deptId)
                .like(StringUtils.hasText(positionName), SysPosition::getPositionName, positionName)
                .orderByAsc(SysPosition::getId);
        Page<SysPosition> result = page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPosition(SysPosition position) {
        save(position);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePosition(Long id, SysPosition position) {
        position.setId(id);
        updateById(position);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePosition(Long id) {
        removeById(id);
    }
}
