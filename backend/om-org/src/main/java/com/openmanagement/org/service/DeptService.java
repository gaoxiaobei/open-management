package com.openmanagement.org.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openmanagement.org.domain.entity.SysDept;

import java.util.List;

public interface DeptService extends IService<SysDept> {

    List<SysDept> getDeptTree();

    void createDept(SysDept dept);

    void updateDept(Long id, SysDept dept);

    void deleteDept(Long id);
}
