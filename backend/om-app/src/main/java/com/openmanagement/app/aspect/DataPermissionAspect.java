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
import org.springframework.util.StringUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class DataPermissionAspect {

    private static final String DEFAULT_DEPT_COLUMN = "dept_id";
    private static final String DEFAULT_USER_COLUMN = "created_by";

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

        DataPermissionContext.set(buildDataScope(userId, dataPermission));
        try {
            return joinPoint.proceed();
        } finally {
            DataPermissionContext.clear();
        }
    }

    private DataPermissionContext.DataScope buildDataScope(Long userId, DataPermission dataPermission) {
        String deptAlias = StringUtils.hasText(dataPermission.deptAlias()) ? dataPermission.deptAlias() : DEFAULT_DEPT_COLUMN;
        String userAlias = StringUtils.hasText(dataPermission.userAlias()) ? dataPermission.userAlias() : DEFAULT_USER_COLUMN;

        List<SysRoleUser> roleUsers = roleUserMapper.selectList(
                new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, userId));
        if (roleUsers.isEmpty()) {
            return new DataPermissionContext.DataScope(false, Set.of(), userId, deptAlias, userAlias);
        }

        List<Long> roleIds = roleUsers.stream().map(SysRoleUser::getRoleId).collect(Collectors.toList());
        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);

        Set<String> scopes = roles.stream()
                .map(SysRole::getDataScope)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (scopes.contains(CommonConstants.DATA_SCOPE_ALL)) {
            return new DataPermissionContext.DataScope(true, Set.of(), null, deptAlias, userAlias);
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

        return new DataPermissionContext.DataScope(false, deptIds, selfUserId, deptAlias, userAlias);
    }

    private Set<Long> findDeptAndChildren(Long rootDeptId) {
        Set<Long> result = new HashSet<>();
        ArrayDeque<Long> pending = new ArrayDeque<>();
        pending.add(rootDeptId);

        while (!pending.isEmpty()) {
            List<Long> currentLevel = new ArrayList<>(pending);
            pending.clear();
            result.addAll(currentLevel);

            List<SysDept> children = deptMapper.selectList(
                    new LambdaQueryWrapper<SysDept>().in(SysDept::getParentId, currentLevel).select(SysDept::getId));
            for (SysDept child : children) {
                if (child.getId() != null && !result.contains(child.getId())) {
                    pending.add(child.getId());
                }
            }
        }

        return result;
    }
}
