package com.openmanagement.system.service.impl;

import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.system.domain.entity.SysRole;
import com.openmanagement.system.domain.entity.SysRoleMenu;
import com.openmanagement.system.mapper.RoleMenuMapper;
import com.openmanagement.system.mapper.RoleUserMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoleServiceImplTest {

    private RoleServiceImpl makeService() {
        return spy(new RoleServiceImpl(mock(RoleMenuMapper.class), mock(RoleUserMapper.class)));
    }

    // --- createRole ---

    @Test
    void createRoleShouldThrowWhenRoleCodeAlreadyExists() {
        RoleServiceImpl service = makeService();
        doReturn(1L).when(service).count(any());

        SysRole role = new SysRole();
        role.setRoleCode("ADMIN");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.createRole(role));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
        verify(service, never()).save(any());
    }

    @Test
    void createRoleShouldSetDefaultStatusWhenBlank() {
        RoleServiceImpl service = makeService();
        doReturn(0L).when(service).count(any());
        doReturn(true).when(service).save(any(SysRole.class));

        SysRole role = new SysRole();
        role.setRoleCode("NEW_ROLE");

        service.createRole(role);

        assertEquals(CommonConstants.STATUS_NORMAL, role.getStatus());
    }

    @Test
    void createRoleShouldPreserveExplicitStatus() {
        RoleServiceImpl service = makeService();
        doReturn(0L).when(service).count(any());
        doReturn(true).when(service).save(any(SysRole.class));

        SysRole role = new SysRole();
        role.setRoleCode("VIEWER");
        role.setStatus(CommonConstants.STATUS_DISABLED);

        service.createRole(role);

        assertEquals(CommonConstants.STATUS_DISABLED, role.getStatus());
    }

    // --- updateRole ---

    @Test
    void updateRoleShouldThrowWhenRoleNotFound() {
        RoleServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateRole(99L, new SysRole()));
        assertEquals(ErrorCode.ROLE_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void updateRoleShouldApplyRoleNameChange() {
        RoleServiceImpl service = makeService();
        SysRole existing = new SysRole();
        existing.setId(1L);
        existing.setRoleName("Old Name");
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        SysRole update = new SysRole();
        update.setRoleName("New Name");

        service.updateRole(1L, update);

        assertEquals("New Name", existing.getRoleName());
        verify(service).updateById(existing);
    }

    // --- getRoleById ---

    @Test
    void getRoleByIdShouldThrowWhenRoleNotFound() {
        RoleServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.getRoleById(99L));
        assertEquals(ErrorCode.ROLE_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void getRoleByIdShouldReturnRoleWhenFound() {
        RoleServiceImpl service = makeService();
        SysRole role = new SysRole();
        role.setId(1L);
        doReturn(role).when(service).getById(1L);

        SysRole result = service.getRoleById(1L);
        assertSame(role, result);
    }

    // --- deleteRole ---

    @Test
    void deleteRoleShouldCascadeDeleteMenuAndUserMappings() {
        RoleMenuMapper roleMenuMapper = mock(RoleMenuMapper.class);
        RoleUserMapper roleUserMapper = mock(RoleUserMapper.class);
        RoleServiceImpl service = spy(new RoleServiceImpl(roleMenuMapper, roleUserMapper));
        doReturn(true).when(service).removeById(1L);

        service.deleteRole(1L);

        verify(service).removeById(1L);
        verify(roleMenuMapper).delete(any());
        verify(roleUserMapper).delete(any());
    }

    // --- assignRoleMenus ---

    @Test
    void assignRoleMenusShouldThrowWhenRoleNotFound() {
        RoleServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.assignRoleMenus(99L, List.of(1L, 2L)));
        assertEquals(ErrorCode.ROLE_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void assignRoleMenusShouldReplaceAllExistingMenuMappings() {
        RoleMenuMapper roleMenuMapper = mock(RoleMenuMapper.class);
        RoleServiceImpl service = spy(new RoleServiceImpl(roleMenuMapper, mock(RoleUserMapper.class)));
        SysRole role = new SysRole();
        role.setId(1L);
        doReturn(role).when(service).getById(1L);

        service.assignRoleMenus(1L, List.of(10L, 20L, 30L));

        verify(roleMenuMapper).delete(any()); // existing mappings deleted
        verify(roleMenuMapper, times(3)).insert(any(SysRoleMenu.class)); // three new mappings created
    }

    @Test
    void assignRoleMenusShouldOnlyClearWhenMenuIdsEmpty() {
        RoleMenuMapper roleMenuMapper = mock(RoleMenuMapper.class);
        RoleServiceImpl service = spy(new RoleServiceImpl(roleMenuMapper, mock(RoleUserMapper.class)));
        SysRole role = new SysRole();
        role.setId(1L);
        doReturn(role).when(service).getById(1L);

        service.assignRoleMenus(1L, List.of());

        verify(roleMenuMapper).delete(any());
        verify(roleMenuMapper, never()).insert(any(SysRoleMenu.class));
    }
}
