package com.openmanagement.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.system.domain.entity.SysRole;
import com.openmanagement.system.domain.entity.SysRoleMenu;
import com.openmanagement.system.domain.entity.SysRoleUser;
import com.openmanagement.system.mapper.RoleMapper;
import com.openmanagement.system.mapper.RoleMenuMapper;
import com.openmanagement.system.mapper.RoleUserMapper;
import com.openmanagement.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, SysRole> implements RoleService {

    private final RoleMenuMapper roleMenuMapper;
    private final RoleUserMapper roleUserMapper;

    @Override
    public PageResult<SysRole> pageRoles(PageQuery pageQuery, String roleName, String roleCode, String status) {
        Page<SysRole> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
                .like(StringUtils.hasText(roleCode), SysRole::getRoleCode, roleCode)
                .eq(StringUtils.hasText(status), SysRole::getStatus, status)
                .orderByDesc(SysRole::getCreatedAt);
        Page<SysRole> result = page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(SysRole role) {
        long count = count(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, role.getRoleCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "角色编码已存在");
        }
        if (!StringUtils.hasText(role.getStatus())) {
            role.setStatus(CommonConstants.STATUS_NORMAL);
        }
        save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long id, SysRole role) {
        SysRole existing = getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND.getCode(), ErrorCode.ROLE_NOT_FOUND.getMessage());
        }
        if (StringUtils.hasText(role.getRoleCode())) existing.setRoleCode(role.getRoleCode());
        if (StringUtils.hasText(role.getRoleName())) existing.setRoleName(role.getRoleName());
        if (StringUtils.hasText(role.getDataScope())) existing.setDataScope(role.getDataScope());
        if (StringUtils.hasText(role.getStatus())) existing.setStatus(role.getStatus());
        if (role.getRemark() != null) existing.setRemark(role.getRemark());
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        removeById(id);
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
        roleUserMapper.delete(new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getRoleId, id));
    }

    @Override
    public SysRole getRoleById(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND.getCode(), ErrorCode.ROLE_NOT_FOUND.getMessage());
        }
        return role;
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleMenus(Long roleId, List<Long> menuIds) {
        if (getById(roleId) == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND.getCode(), ErrorCode.ROLE_NOT_FOUND.getMessage());
        }
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds != null && !menuIds.isEmpty()) {
            menuIds.forEach(menuId -> {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                roleMenuMapper.insert(rm);
            });
        }
    }

    @Override
    public List<SysRole> listRolesByUserId(Long userId) {
        List<SysRoleUser> roleUsers = roleUserMapper.selectList(
                new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, userId));
        if (roleUsers.isEmpty()) return Collections.emptyList();
        List<Long> roleIds = roleUsers.stream().map(SysRoleUser::getRoleId).collect(Collectors.toList());
        return listByIds(roleIds);
    }

    @Override
    public List<SysRole> listAllEnabled() {
        return list(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, CommonConstants.STATUS_NORMAL)
                .orderByAsc(SysRole::getRoleName));
    }
}
