package com.openmanagement.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openmanagement.system.domain.entity.SysMenu;

import java.util.List;

public interface MenuService extends IService<SysMenu> {
    List<SysMenu> listAllMenus();
    List<SysMenu> listMenuTree();
    SysMenu getMenuById(Long id);
    void createMenu(SysMenu menu);
    void updateMenu(Long id, SysMenu menu);
    void deleteMenu(Long id);
    List<SysMenu> listMenusByRoleIds(List<Long> roleIds);
    List<SysMenu> buildMenuTree(List<SysMenu> menus);
}
