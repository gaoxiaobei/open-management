package com.openmanagement.org.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openmanagement.org.domain.entity.SysPosition;

import java.util.List;

public interface PositionService extends IService<SysPosition> {

    List<SysPosition> listByDeptId(Long deptId);
}
