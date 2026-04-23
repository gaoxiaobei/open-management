package com.openmanagement.system.service.impl;

import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.system.domain.entity.SysDictItem;
import com.openmanagement.system.domain.entity.SysDictType;
import com.openmanagement.system.mapper.DictItemMapper;
import com.openmanagement.system.mapper.DictTypeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DictServiceImplTest {

    @Mock
    private DictTypeMapper dictTypeMapper;

    @Mock
    private DictItemMapper dictItemMapper;

    @InjectMocks
    private DictServiceImpl dictService;

    // --- createDictType ---

    @Test
    void createDictTypeShouldSetDefaultStatusWhenBlank() {
        SysDictType dictType = new SysDictType();
        dictType.setDictType("gender");

        dictService.createDictType(dictType);

        assertEquals(CommonConstants.STATUS_NORMAL, dictType.getStatus());
        verify(dictTypeMapper).insert(dictType);
    }

    @Test
    void createDictTypeShouldPreserveExplicitStatus() {
        SysDictType dictType = new SysDictType();
        dictType.setDictType("gender");
        dictType.setStatus(CommonConstants.STATUS_DISABLED);

        dictService.createDictType(dictType);

        assertEquals(CommonConstants.STATUS_DISABLED, dictType.getStatus());
    }

    // --- updateDictType ---

    @Test
    void updateDictTypeShouldThrowWhenTypeNotFound() {
        when(dictTypeMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> dictService.updateDictType(99L, new SysDictType()));
        assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void updateDictTypeShouldApplyNameChange() {
        SysDictType existing = new SysDictType();
        existing.setId(1L);
        existing.setDictName("Old Name");
        when(dictTypeMapper.selectById(1L)).thenReturn(existing);

        SysDictType update = new SysDictType();
        update.setDictName("New Name");

        dictService.updateDictType(1L, update);

        assertEquals("New Name", existing.getDictName());
        verify(dictTypeMapper).updateById(existing);
    }

    @Test
    void updateDictTypeShouldNotOverwriteWithNullFields() {
        SysDictType existing = new SysDictType();
        existing.setId(1L);
        existing.setDictType("gender");
        existing.setDictName("Gender");
        when(dictTypeMapper.selectById(1L)).thenReturn(existing);

        dictService.updateDictType(1L, new SysDictType()); // all null

        assertEquals("gender", existing.getDictType());
        assertEquals("Gender", existing.getDictName());
    }

    // --- deleteDictType ---

    @Test
    void deleteDictTypeShouldCallDeleteById() {
        dictService.deleteDictType(3L);
        verify(dictTypeMapper).deleteById(3L);
    }

    // --- createDictItem ---

    @Test
    void createDictItemShouldSetDefaultStatusWhenBlank() {
        SysDictItem item = new SysDictItem();
        item.setDictType("gender");
        item.setItemLabel("Male");
        item.setItemValue("M");

        dictService.createDictItem(item);

        assertEquals(CommonConstants.STATUS_NORMAL, item.getStatus());
        verify(dictItemMapper).insert(item);
    }

    // --- updateDictItem ---

    @Test
    void updateDictItemShouldThrowWhenItemNotFound() {
        when(dictItemMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> dictService.updateDictItem(99L, new SysDictItem()));
        assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void updateDictItemShouldApplyLabelAndValueChanges() {
        SysDictItem existing = new SysDictItem();
        existing.setId(1L);
        existing.setItemLabel("Old Label");
        existing.setItemValue("0");
        when(dictItemMapper.selectById(1L)).thenReturn(existing);

        SysDictItem update = new SysDictItem();
        update.setItemLabel("New Label");
        update.setItemValue("1");

        dictService.updateDictItem(1L, update);

        assertEquals("New Label", existing.getItemLabel());
        assertEquals("1", existing.getItemValue());
        verify(dictItemMapper).updateById(existing);
    }

    // --- deleteDictItem ---

    @Test
    void deleteDictItemShouldCallDeleteById() {
        dictService.deleteDictItem(7L);
        verify(dictItemMapper).deleteById(7L);
    }

    // --- listDictItems ---

    @Test
    void listDictItemsShouldQueryWithDictType() {
        dictService.listDictItems("gender", false);
        verify(dictItemMapper).selectList(any());
    }
}
