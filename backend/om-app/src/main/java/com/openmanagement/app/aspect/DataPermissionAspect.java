package com.openmanagement.app.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.openmanagement.common.annotation.DataPermission;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.context.DataPermissionContext;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.org.domain.entity.SysDept;
import com.openmanagement.org.mapper.DeptMapper;
import com.openmanagement.system.domain.entity.SysRole;
import com.openmanagement.system.domain.entity.SysRoleUser;
import com.openmanagement.system.domain.entity.SysUser;
import com.openmanagement.system.mapper.RoleMapper;
import com.openmanagement.system.mapper.RoleUserMapper;
import com.openmanagement.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class DataPermissionAspect {

    private final RoleUserMapper roleUserMapper;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final DeptMapper deptMapper;

    @Around("@annotation(dataPermission)")
    public Object applyDataPermission(ProceedingJoinPoint joinPoint, DataPermission dataPermission) throws Throwable {
        Long userId = UserContext.getUserId();
        if (userId == null || CommonConstants.ADMIN_USER_ID.equals(userId)) {
            return joinPoint.proceed();
        }

        DataPermissionContext.set(buildDataScope(userId));
        try {
            return joinPoint.proceed();
        } finally {
            DataPermissionContext.clear();
        }
    }

    private DataPermissionContext.DataScope buildDataScope(Long userId) {
        List<SysRoleUser> roleUsers = roleUserMapper.selectList(
                new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, userId));
        if (roleUsers.isEmpty()) {
            return new DataPermissionContext.DataScope(false, Set.of(), userId);
        }

        List<Long> roleIds = roleUsers.stream().map(SysRoleUser::getRoleId).collect(Collectors.toList());
        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);

        Set<String> scopes = roles.stream()
                .map(SysRole::getDataScope)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (scopes.contains(CommonConstants.DATA_SCOPE_ALL)) {
            return new DataPermissionContext.DataScope(true, Set.of(), null);
        }

        SysUser currentUser = userMapper.selectById(userId);
        Long currentDeptId = currentUser == null ? null : currentUser.getDeptId();

        Set<Long> deptIds = new HashSet<>();
        Long selfUserId = null;

        if (currentDeptId != null) {
            if (scopes.contains(CommonConstants.DATA_SCOPE_DEPT)) {
                deptIds.add(currentDeptId);
            }
            if (scopes.contains(CommonConstants.DATA_SCOPE_DEPT_AND_CHILD)) {
                deptIds.addAll(findDeptAndChildren(currentDeptId));
            }
        }

        if (scopes.contains(CommonConstants.DATA_SCOPE_SELF) || (deptIds.isEmpty() && scopes.isEmpty())) {
            selfUserId = userId;
        }

        return new DataPermissionContext.DataScope(false, deptIds, selfUserId);
    }

    private Set<Long> findDeptAndChildren(Long rootDeptId) {
        List<SysDept> allDepts = deptMapper.selectList(new LambdaQueryWrapper<>());
        Map<Long, List<Long>> parentChildrenMap = new HashMap<>();
        for (SysDept dept : allDepts) {
            if (dept.getParentId() == null || dept.getId() == null) {
                continue;
            }
            parentChildrenMap.computeIfAbsent(dept.getParentId(), k -> new java.util.ArrayList<>()).add(dept.getId());
        }

        Set<Long> result = new HashSet<>();
        ArrayDeque<Long> queue = new ArrayDeque<>();
        queue.add(rootDeptId);

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            if (!result.add(current)) {
                continue;
            }
            List<Long> children = parentChildrenMap.get(current);
            if (children != null) {
                queue.addAll(children);
            }
        }

        return result;
    }
}
