package com.openmanagement.org.service.impl;

import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.org.domain.entity.SysPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class PositionServiceImplTest {

    @Test
    void updatePositionShouldThrowWhenPositionDoesNotExist() {
        PositionServiceImpl service = spy(new PositionServiceImpl());

        doReturn(null).when(service).getById(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.updatePosition(1L, new SysPosition()));

        assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
        verify(service, never()).updateById(any(SysPosition.class));
    }

    @Test
    void updatePositionShouldAllowIdempotentUpdates() {
        PositionServiceImpl service = spy(new PositionServiceImpl());
        SysPosition existing = new SysPosition();
        SysPosition update = new SysPosition();

        doReturn(existing).when(service).getById(1L);
        doReturn(false).when(service).updateById(any(SysPosition.class));

        assertDoesNotThrow(() -> service.updatePosition(1L, update));
        assertEquals(1L, update.getId());
        verify(service).updateById(update);
    }
}
