package com.openmanagement.system.controller;

import com.openmanagement.common.result.R;
import com.openmanagement.common.annotation.OperateLog;
import com.openmanagement.system.domain.entity.SysMenu;
import com.openmanagement.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public R<List<SysMenu>> listMenuTree() {
        return R.ok(menuService.listMenuTree());
    }

    @GetMapping("/{id}")
    public R<SysMenu> getMenuById(@PathVariable Long id) {
        return R.ok(menuService.getMenuById(id));
    }

    @PostMapping
    @OperateLog(module = "系统管理-菜单", name = "创建菜单")
    public R<Void> createMenu(@RequestBody SysMenu menu) {
        menuService.createMenu(menu);
        return R.ok();
    }

    @PutMapping("/{id}")
    @OperateLog(module = "系统管理-菜单", name = "更新菜单")
    public R<Void> updateMenu(@PathVariable Long id, @RequestBody SysMenu menu) {
        menuService.updateMenu(id, menu);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @OperateLog(module = "系统管理-菜单", name = "删除菜单")
    public R<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return R.ok();
    }
}
