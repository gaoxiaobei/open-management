package com.openmanagement.org.service.impl;

import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.org.domain.entity.SysDept;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class DeptServiceImplTest {

    @Test
    void updateDeptShouldThrowWhenDepartmentDoesNotExist() {
        DeptServiceImpl service = spy(new DeptServiceImpl());

        doReturn(null).when(service).getById(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.updateDept(1L, new SysDept()));

        assertEquals(ErrorCode.DEPT_NOT_FOUND.getCode(), ex.getCode());
        verify(service, never()).updateById(any(SysDept.class));
    }

    @Test
    void updateDeptShouldAllowIdempotentUpdates() {
        DeptServiceImpl service = spy(new DeptServiceImpl());
        SysDept existing = new SysDept();
        SysDept update = new SysDept();

        doReturn(existing).when(service).getById(1L);
        doReturn(false).when(service).updateById(any(SysDept.class));

        assertDoesNotThrow(() -> service.updateDept(1L, update));
        assertEquals(1L, update.getId());
        verify(service).updateById(update);
    }

    @Test
    void deleteDeptShouldThrowWhenDepartmentHasChildren() {
        DeptServiceImpl service = spy(new DeptServiceImpl());
        SysDept existing = new SysDept();

        doReturn(existing).when(service).getById(1L);
        doReturn(1L).when(service).count(any());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.deleteDept(1L));

        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
        verify(service, never()).removeById(1L);
    }

    @Test
    void deleteDeptShouldRemoveLeafDepartment() {
        DeptServiceImpl service = spy(new DeptServiceImpl());
        SysDept existing = new SysDept();

        doReturn(existing).when(service).getById(1L);
        doReturn(0L).when(service).count(any());
        doReturn(true).when(service).removeById(1L);

        assertDoesNotThrow(() -> service.deleteDept(1L));
        verify(service).removeById(1L);
    }
}
