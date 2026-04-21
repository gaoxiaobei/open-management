package com.openmanagement.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.system.domain.entity.SysMenu;
import com.openmanagement.system.domain.entity.SysRoleMenu;
import com.openmanagement.system.mapper.MenuMapper;
import com.openmanagement.system.mapper.RoleMenuMapper;
import com.openmanagement.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, SysMenu> implements MenuService {

    private final RoleMenuMapper roleMenuMapper;

    @Override
    public List<SysMenu> listAllMenus() {
        return list(new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSortOrder));
    }

    @Override
    public List<SysMenu> listMenuTree() {
        return buildMenuTree(listAllMenus());
    }

    @Override
    public SysMenu getMenuById(Long id) {
        SysMenu menu = getById(id);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND.getCode(), ErrorCode.MENU_NOT_FOUND.getMessage());
        }
        return menu;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMenu(SysMenu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(CommonConstants.ROOT_PARENT_ID);
        }
        if (!StringUtils.hasText(menu.getStatus())) {
            menu.setStatus(CommonConstants.STATUS_NORMAL);
        }
        save(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Long id, SysMenu menu) {
        SysMenu existing = getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND.getCode(), ErrorCode.MENU_NOT_FOUND.getMessage());
        }
        if (menu.getParentId() != null) existing.setParentId(menu.getParentId());
        if (StringUtils.hasText(menu.getMenuName())) existing.setMenuName(menu.getMenuName());
        if (StringUtils.hasText(menu.getMenuType())) existing.setMenuType(menu.getMenuType());
        if (menu.getPath() != null) existing.setPath(menu.getPath());
        if (menu.getComponent() != null) existing.setComponent(menu.getComponent());
        if (menu.getIcon() != null) existing.setIcon(menu.getIcon());
        if (menu.getPermissionCode() != null) existing.setPermissionCode(menu.getPermissionCode());
        if (menu.getSortOrder() != null) existing.setSortOrder(menu.getSortOrder());
        if (StringUtils.hasText(menu.getStatus())) existing.setStatus(menu.getStatus());
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        removeById(id);
    }

    @Override
    public List<SysMenu> listMenusByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return Collections.emptyList();
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds));
        if (roleMenus.isEmpty()) return Collections.emptyList();
        List<Long> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).distinct().collect(Collectors.toList());
        return listByIds(menuIds);
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) return Collections.emptyList();
        Map<Long, SysMenu> menuMap = menus.stream().collect(Collectors.toMap(SysMenu::getId, m -> m));
        List<SysMenu> roots = new ArrayList<>();
        menus.forEach(menu -> {
            if (menu.getParentId() == null || menu.getParentId().equals(0L)) {
                roots.add(menu);
            } else {
                SysMenu parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(menu);
                } else {
                    roots.add(menu);
                }
            }
        });
        return roots;
    }
}
