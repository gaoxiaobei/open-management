package com.openmanagement.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.system.domain.entity.SysRole;

import java.util.List;

public interface RoleService extends IService<SysRole> {
    PageResult<SysRole> pageRoles(PageQuery pageQuery, String roleName, String roleCode, String status);
    void createRole(SysRole role);
    void updateRole(Long id, SysRole role);
    void deleteRole(Long id);
    SysRole getRoleById(Long id);
    List<Long> getRoleMenuIds(Long roleId);
    void assignRoleMenus(Long roleId, List<Long> menuIds);
    List<SysRole> listRolesByUserId(Long userId);
    List<SysRole> listAllEnabled();
}
