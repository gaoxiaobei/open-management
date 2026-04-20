package com.openmanagement.system.controller;

import com.openmanagement.common.result.R;
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
    public R<Void> createMenu(@RequestBody SysMenu menu) {
        menuService.createMenu(menu);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> updateMenu(@PathVariable Long id, @RequestBody SysMenu menu) {
        menuService.updateMenu(id, menu);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return R.ok();
    }
}
