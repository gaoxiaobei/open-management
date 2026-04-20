package com.openmanagement.system.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import com.openmanagement.system.domain.entity.SysRole;
import com.openmanagement.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public R<PageResult<SysRole>> pageRoles(PageQuery pageQuery,
                                            @RequestParam(required = false) String roleName,
                                            @RequestParam(required = false) String roleCode,
                                            @RequestParam(required = false) String status) {
        return R.ok(roleService.pageRoles(pageQuery, roleName, roleCode, status));
    }

    @GetMapping("/all")
    public R<List<SysRole>> listAllEnabled() {
        return R.ok(roleService.listAllEnabled());
    }

    @GetMapping("/{id}")
    public R<SysRole> getRoleById(@PathVariable Long id) {
        return R.ok(roleService.getRoleById(id));
    }

    @PostMapping
    public R<Void> createRole(@RequestBody SysRole role) {
        roleService.createRole(role);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> updateRole(@PathVariable Long id, @RequestBody SysRole role) {
        roleService.updateRole(id, role);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.ok();
    }

    @GetMapping("/{id}/menu-ids")
    public R<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        return R.ok(roleService.getRoleMenuIds(id));
    }

    @PostMapping("/{id}/menus")
    public R<Void> assignRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignRoleMenus(id, menuIds);
        return R.ok();
    }
}
