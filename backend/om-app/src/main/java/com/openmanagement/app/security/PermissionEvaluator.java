package com.openmanagement.app.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.system.domain.entity.SysMenu;
import com.openmanagement.system.domain.entity.SysRole;
import com.openmanagement.system.domain.entity.SysRoleMenu;
import com.openmanagement.system.domain.entity.SysRoleUser;
import com.openmanagement.system.mapper.MenuMapper;
import com.openmanagement.system.mapper.RoleMapper;
import com.openmanagement.system.mapper.RoleMenuMapper;
import com.openmanagement.system.mapper.RoleUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionEvaluator {

    private final RoleUserMapper roleUserMapper;
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final MenuMapper menuMapper;

    public boolean hasPermission(Long userId, String permission) {
        if (userId == null) {
            return false;
        }
        if (CommonConstants.ADMIN_USER_ID.equals(userId)) {
            return true;
        }

        List<SysRoleUser> roleUsers = roleUserMapper.selectList(
                new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, userId));
        if (roleUsers.isEmpty()) {
            return false;
        }

        List<Long> roleIds = roleUsers.stream()
                .map(SysRoleUser::getRoleId)
                .distinct()
                .toList();
        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
        roles = roles == null ? Collections.emptyList() : roles.stream()
                .filter(role -> CommonConstants.STATUS_NORMAL.equals(role.getStatus()))
                .toList();
        if (roles.isEmpty()) {
            return false;
        }
        boolean isSuperAdmin = roles.stream().anyMatch(role ->
                CommonConstants.ADMIN_ROLE_CODE.equals(role.getRoleCode()));
        if (isSuperAdmin) {
            return true;
        }

        roleIds = roles.stream()
                .map(SysRole::getId)
                .distinct()
                .toList();

        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds));
        if (roleMenus.isEmpty()) {
            return false;
        }

        List<Long> menuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .toList();
        if (menuIds.isEmpty()) {
            return false;
        }

        List<SysMenu> menus = menuMapper.selectBatchIds(menuIds);
        Set<String> permissions = menus == null ? Collections.emptySet() : menus.stream()
                .filter(menu -> CommonConstants.STATUS_NORMAL.equals(menu.getStatus()))
                .map(SysMenu::getPermissionCode)
                .filter(code -> code != null && !code.isBlank())
                .collect(Collectors.toSet());
        return permissions.contains(permission);
    }
}
