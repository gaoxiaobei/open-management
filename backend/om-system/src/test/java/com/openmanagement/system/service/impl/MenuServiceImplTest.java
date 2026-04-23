package com.openmanagement.system.service.impl;

import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.system.domain.entity.SysMenu;
import com.openmanagement.system.mapper.RoleMenuMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MenuServiceImplTest {

    private MenuServiceImpl makeService() {
        return spy(new MenuServiceImpl(mock(RoleMenuMapper.class)));
    }

    // --- createMenu ---

    @Test
    void createMenuShouldDefaultParentIdToRootWhenNull() {
        MenuServiceImpl service = makeService();
        doReturn(true).when(service).save(any(SysMenu.class));

        SysMenu menu = new SysMenu();
        menu.setMenuName("Test Menu");

        service.createMenu(menu);

        assertEquals(CommonConstants.ROOT_PARENT_ID, menu.getParentId());
    }

    @Test
    void createMenuShouldDefaultStatusToEnabledWhenBlank() {
        MenuServiceImpl service = makeService();
        doReturn(true).when(service).save(any(SysMenu.class));

        SysMenu menu = new SysMenu();
        menu.setMenuName("Test Menu");

        service.createMenu(menu);

        assertEquals(CommonConstants.STATUS_NORMAL, menu.getStatus());
    }

    @Test
    void createMenuShouldPreserveExplicitParentId() {
        MenuServiceImpl service = makeService();
        doReturn(true).when(service).save(any(SysMenu.class));

        SysMenu menu = new SysMenu();
        menu.setParentId(5L);

        service.createMenu(menu);

        assertEquals(5L, menu.getParentId());
    }

    // --- updateMenu ---

    @Test
    void updateMenuShouldThrowWhenMenuNotFound() {
        MenuServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateMenu(99L, new SysMenu()));
        assertEquals(ErrorCode.MENU_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void updateMenuShouldApplyNameChange() {
        MenuServiceImpl service = makeService();
        SysMenu existing = new SysMenu();
        existing.setId(1L);
        existing.setMenuName("Old Name");
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        SysMenu update = new SysMenu();
        update.setMenuName("New Name");

        service.updateMenu(1L, update);

        assertEquals("New Name", existing.getMenuName());
        verify(service).updateById(existing);
    }

    // --- getMenuById ---

    @Test
    void getMenuByIdShouldThrowWhenMenuNotFound() {
        MenuServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.getMenuById(99L));
        assertEquals(ErrorCode.MENU_NOT_FOUND.getCode(), ex.getCode());
    }

    // --- buildMenuTree ---

    @Test
    void buildMenuTreeShouldReturnEmptyListWhenInputIsEmpty() {
        MenuServiceImpl service = makeService();
        List<SysMenu> result = service.buildMenuTree(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void buildMenuTreeShouldReturnEmptyListWhenInputIsNull() {
        MenuServiceImpl service = makeService();
        List<SysMenu> result = service.buildMenuTree(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void buildMenuTreeShouldNestChildrenUnderParent() {
        MenuServiceImpl service = makeService();

        SysMenu parent = new SysMenu();
        parent.setId(1L);
        parent.setParentId(0L);

        SysMenu child = new SysMenu();
        child.setId(2L);
        child.setParentId(1L);

        List<SysMenu> result = service.buildMenuTree(List.of(parent, child));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals(2L, result.get(0).getChildren().get(0).getId());
    }

    @Test
    void buildMenuTreeShouldPromoteOrphanedChildrenToRoot() {
        MenuServiceImpl service = makeService();

        SysMenu orphan = new SysMenu();
        orphan.setId(3L);
        orphan.setParentId(999L); // parent does not exist in list

        List<SysMenu> result = service.buildMenuTree(List.of(orphan));

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
    }

    @Test
    void buildMenuTreeShouldHandleMultipleRootNodes() {
        MenuServiceImpl service = makeService();

        SysMenu root1 = new SysMenu();
        root1.setId(1L);
        root1.setParentId(0L);

        SysMenu root2 = new SysMenu();
        root2.setId(2L);
        root2.setParentId(null);

        List<SysMenu> result = service.buildMenuTree(List.of(root1, root2));

        assertEquals(2, result.size());
    }

    // --- listMenusByRoleIds ---

    @Test
    void listMenusByRoleIdsShouldReturnEmptyWhenRoleIdsNull() {
        MenuServiceImpl service = makeService();
        List<SysMenu> result = service.listMenusByRoleIds(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void listMenusByRoleIdsShouldReturnEmptyWhenRoleIdsEmpty() {
        MenuServiceImpl service = makeService();
        List<SysMenu> result = service.listMenusByRoleIds(List.of());
        assertTrue(result.isEmpty());
    }
}
