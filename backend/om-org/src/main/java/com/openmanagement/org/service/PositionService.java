package com.openmanagement.org.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.org.domain.entity.SysPosition;

import java.util.List;

public interface PositionService extends IService<SysPosition> {

    List<SysPosition> listByDeptId(Long deptId);

    PageResult<SysPosition> pagePositions(PageQuery pageQuery, Long deptId, String positionName);

    void createPosition(SysPosition position);

    void updatePosition(Long id, SysPosition position);

    void deletePosition(Long id);
}
