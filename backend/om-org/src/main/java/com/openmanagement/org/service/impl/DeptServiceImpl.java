package com.openmanagement.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.org.domain.entity.SysDept;
import com.openmanagement.org.mapper.DeptMapper;
import com.openmanagement.org.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, SysDept> implements DeptService {

    @Override
    public List<SysDept> getDeptTree() {
        List<SysDept> depts = list(new LambdaQueryWrapper<SysDept>()
                .orderByAsc(SysDept::getSortOrder)
                .orderByAsc(SysDept::getId));
        if (depts.isEmpty()) {
            return List.of();
        }
        Map<Long, SysDept> deptMap = depts.stream()
                .collect(Collectors.toMap(SysDept::getId, dept -> dept));
        List<SysDept> roots = new ArrayList<>();
        for (SysDept dept : depts) {
            Long parentId = dept.getParentId();
            if (parentId == null || parentId.equals(CommonConstants.ROOT_PARENT_ID)) {
                roots.add(dept);
                continue;
            }
            SysDept parent = deptMap.get(parentId);
            if (parent == null) {
                roots.add(dept);
                continue;
            }
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(dept);
        }
        return roots;
    }
}
