package com.openmanagement.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.org.domain.entity.SysDept;
import com.openmanagement.org.mapper.DeptMapper;
import com.openmanagement.org.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, SysDept> implements DeptService {

    @Override
    public List<SysDept> getDeptTree() {
        // TODO: build hierarchical dept tree from flat list
        return Collections.emptyList();
    }
}
