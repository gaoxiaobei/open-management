package com.openmanagement.asset.service.impl;

import com.openmanagement.asset.domain.entity.AssetInfo;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssetServiceImplTest {

    private AssetServiceImpl makeService() {
        return spy(new AssetServiceImpl());
    }

    private AssetInfo validAsset() {
        AssetInfo asset = new AssetInfo();
        asset.setAssetCode("A001");
        asset.setAssetName("Laptop");
        asset.setCategory("IT");
        return asset;
    }

    // --- createAsset validation ---

    @Test
    void createShouldThrowWhenAssetIsNull() {
        AssetServiceImpl service = makeService();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAsset(null));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldThrowWhenAssetCodeIsNull() {
        AssetServiceImpl service = makeService();
        AssetInfo asset = validAsset();
        asset.setAssetCode(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAsset(asset));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldThrowWhenAssetCodeIsBlank() {
        AssetServiceImpl service = makeService();
        AssetInfo asset = validAsset();
        asset.setAssetCode("  ");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAsset(asset));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldThrowWhenAssetNameIsNull() {
        AssetServiceImpl service = makeService();
        AssetInfo asset = validAsset();
        asset.setAssetName(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAsset(asset));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldThrowWhenCategoryIsNull() {
        AssetServiceImpl service = makeService();
        AssetInfo asset = validAsset();
        asset.setCategory(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAsset(asset));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldThrowWhenAssetCodeAlreadyExists() {
        AssetServiceImpl service = makeService();
        doReturn(1L).when(service).count(any());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAsset(validAsset()));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
        verify(service, never()).save(any());
    }

    @Test
    void createShouldThrowOnDuplicateKeyExceptionFromDatabase() {
        AssetServiceImpl service = makeService();
        doReturn(0L).when(service).count(any());
        doThrow(DuplicateKeyException.class).when(service).save(any(AssetInfo.class));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAsset(validAsset()));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldSaveAssetWithNullId() {
        AssetServiceImpl service = makeService();
        doReturn(0L).when(service).count(any());
        doReturn(true).when(service).save(any(AssetInfo.class));

        AssetInfo asset = validAsset();
        asset.setId(999L);

        service.createAsset(asset);

        verify(service).save(argThat(a -> a.getId() == null));
    }

    // --- updateAsset ---

    @Test
    void updateShouldThrowWhenAssetIsNull() {
        AssetServiceImpl service = makeService();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateAsset(1L, null));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void updateShouldThrowWhenAssetNotFound() {
        AssetServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateAsset(99L, validAsset()));
        assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void updateShouldThrowWhenNewAssetCodeAlreadyExistsOnAnotherAsset() {
        AssetServiceImpl service = makeService();
        AssetInfo existing = validAsset();
        existing.setId(1L);
        existing.setAssetCode("A001");
        doReturn(existing).when(service).getById(1L);
        doReturn(1L).when(service).count(any()); // duplicate found

        AssetInfo update = new AssetInfo();
        update.setAssetCode("A002"); // different from existing

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateAsset(1L, update));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void updateShouldAllowKeepingSameAssetCode() {
        AssetServiceImpl service = makeService();
        AssetInfo existing = validAsset();
        existing.setId(1L);
        existing.setAssetCode("A001");
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        AssetInfo update = new AssetInfo();
        update.setAssetCode("A001"); // same as existing — uniqueness check skipped

        assertDoesNotThrow(() -> service.updateAsset(1L, update));
        verify(service, never()).count(any());
    }

    @Test
    void updateShouldSetIdBeforeUpdate() {
        AssetServiceImpl service = makeService();
        AssetInfo existing = validAsset();
        existing.setId(1L);
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        AssetInfo update = new AssetInfo();
        update.setAssetName("New Name");

        service.updateAsset(1L, update);

        verify(service).updateById(argThat(a -> Long.valueOf(1L).equals(a.getId())));
    }

    @Test
    void updateShouldThrowOnDuplicateKeyExceptionFromDatabase() {
        AssetServiceImpl service = makeService();
        AssetInfo existing = validAsset();
        existing.setId(1L);
        existing.setAssetCode("A001");
        doReturn(existing).when(service).getById(1L);
        doThrow(DuplicateKeyException.class).when(service).updateById(any());

        AssetInfo update = new AssetInfo();
        update.setAssetCode("A001");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateAsset(1L, update));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    // --- updateAssetStatus ---

    @Test
    void updateStatusShouldThrowWhenAssetNotFound() {
        AssetServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateAssetStatus(99L, "IN_USE"));
        assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void updateStatusShouldThrowWhenStatusIsBlank() {
        AssetServiceImpl service = makeService();
        AssetInfo existing = validAsset();
        existing.setId(1L);
        doReturn(existing).when(service).getById(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateAssetStatus(1L, "  "));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void updateStatusShouldSetStatusAndUpdate() {
        AssetServiceImpl service = makeService();
        AssetInfo existing = validAsset();
        existing.setId(1L);
        existing.setAssetStatus("IDLE");
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        service.updateAssetStatus(1L, "IN_USE");

        assertEquals("IN_USE", existing.getAssetStatus());
        verify(service).updateById(existing);
    }

    // --- deleteAsset ---

    @Test
    void deleteShouldThrowWhenAssetNotFound() {
        AssetServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deleteAsset(99L));
        assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
        verify(service, never()).removeById(any());
    }

    @Test
    void deleteShouldCallRemoveByIdWhenAssetExists() {
        AssetServiceImpl service = makeService();
        AssetInfo existing = validAsset();
        existing.setId(1L);
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).removeById(1L);

        service.deleteAsset(1L);

        verify(service).removeById(1L);
    }
}
