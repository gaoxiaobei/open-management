package com.openmanagement.system.service.impl;

import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.system.domain.entity.SysUser;
import com.openmanagement.system.domain.entity.SysRoleUser;
import com.openmanagement.system.dto.UserCreateRequest;
import com.openmanagement.system.mapper.RoleUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl makeService() {
        return spy(new UserServiceImpl(mock(RoleUserMapper.class), new BCryptPasswordEncoder()));
    }

    // --- createUser ---

    @Test
    void createUserShouldThrowWhenUsernameAlreadyExists() {
        UserServiceImpl service = makeService();
        doReturn(1L).when(service).count(any());

        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("existing");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.createUser(req));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
        verify(service, never()).save(any());
    }

    @Test
    void createUserShouldSaveNewUserWithDefaultStatus() {
        UserServiceImpl service = makeService();
        doReturn(0L).when(service).count(any());
        doReturn(true).when(service).save(any(SysUser.class));

        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("newuser");
        req.setRealName("New User");

        assertDoesNotThrow(() -> service.createUser(req));
        verify(service).save(argThat(u -> CommonConstants.STATUS_NORMAL.equals(u.getStatus())));
    }

    @Test
    void createUserShouldHashDefaultPassword() {
        UserServiceImpl service = makeService();
        doReturn(0L).when(service).count(any());
        doReturn(true).when(service).save(any(SysUser.class));

        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("newuser");

        service.createUser(req);

        verify(service).save(argThat(u -> u.getPasswordHash() != null
                && !u.getPasswordHash().equals(CommonConstants.DEFAULT_PASSWORD)));
    }

    // --- updateUser ---

    @Test
    void updateUserShouldThrowWhenUserNotFound() {
        UserServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateUser(99L, new UserCreateRequest()));
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void updateUserShouldApplyRealNameChange() {
        UserServiceImpl service = makeService();
        SysUser existing = new SysUser();
        existing.setId(1L);
        existing.setRealName("Old Name");
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        UserCreateRequest req = new UserCreateRequest();
        req.setRealName("New Name");

        service.updateUser(1L, req);

        assertEquals("New Name", existing.getRealName());
        verify(service).updateById(existing);
    }

    @Test
    void updateUserShouldReplaceRolesWhenRoleIdsProvided() {
        RoleUserMapper roleUserMapper = mock(RoleUserMapper.class);
        UserServiceImpl service = spy(new UserServiceImpl(roleUserMapper, new BCryptPasswordEncoder()));
        SysUser existing = new SysUser();
        existing.setId(1L);
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        UserCreateRequest req = new UserCreateRequest();
        req.setRoleIds(List.of(10L, 20L));

        service.updateUser(1L, req);

        verify(roleUserMapper).delete(any()); // old roles removed
        verify(roleUserMapper, times(2)).insert(any(SysRoleUser.class)); // two new roles inserted
    }

    // --- resetPassword ---

    @Test
    void resetPasswordShouldThrowWhenUserNotFound() {
        UserServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.resetPassword(99L));
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void resetPasswordShouldSetNewHashedPasswordForExistingUser() {
        UserServiceImpl service = makeService();
        SysUser user = new SysUser();
        user.setId(1L);
        user.setPasswordHash("oldHash");
        doReturn(user).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        service.resetPassword(1L);

        assertNotEquals("oldHash", user.getPasswordHash());
        assertNotNull(user.getPasswordHash());
        verify(service).updateById(user);
    }

    // --- changeUserStatus ---

    @Test
    void changeUserStatusShouldThrowWhenUserNotFound() {
        UserServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.changeUserStatus(99L, "DISABLED"));
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void changeUserStatusShouldUpdateStatusField() {
        UserServiceImpl service = makeService();
        SysUser user = new SysUser();
        user.setId(1L);
        user.setStatus(CommonConstants.STATUS_NORMAL);
        doReturn(user).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        service.changeUserStatus(1L, CommonConstants.STATUS_DISABLED);

        assertEquals(CommonConstants.STATUS_DISABLED, user.getStatus());
        verify(service).updateById(user);
    }

    // --- getUserById ---

    @Test
    void getUserByIdShouldThrowWhenUserNotFound() {
        UserServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.getUserById(99L));
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), ex.getCode());
    }

    // --- deleteUser ---

    @Test
    void deleteUserShouldCleanRoleMappingsBeforeRemoval() {
        RoleUserMapper roleUserMapper = mock(RoleUserMapper.class);
        UserServiceImpl service = spy(new UserServiceImpl(roleUserMapper, new BCryptPasswordEncoder()));
        doReturn(true).when(service).removeById(1L);

        service.deleteUser(1L);

        verify(roleUserMapper).delete(any());
        verify(service).removeById(1L);
    }
}
