package com.openmanagement.system.service.impl;

import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.system.domain.entity.SysConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConfigServiceImplTest {

    private ConfigServiceImpl makeService() {
        return spy(new ConfigServiceImpl());
    }

    // --- updateConfig ---

    @Test
    void updateConfigShouldThrowWhenConfigNotFound() {
        ConfigServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateConfig(99L, new SysConfig()));
        assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void updateConfigShouldApplyValueChange() {
        ConfigServiceImpl service = makeService();
        SysConfig existing = new SysConfig();
        existing.setId(1L);
        existing.setConfigKey("site.name");
        existing.setConfigValue("OldValue");
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        SysConfig update = new SysConfig();
        update.setConfigValue("NewValue");

        service.updateConfig(1L, update);

        assertEquals("NewValue", existing.getConfigValue());
        verify(service).updateById(existing);
    }

    @Test
    void updateConfigShouldApplyKeyChange() {
        ConfigServiceImpl service = makeService();
        SysConfig existing = new SysConfig();
        existing.setId(1L);
        existing.setConfigKey("old.key");
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        SysConfig update = new SysConfig();
        update.setConfigKey("new.key");

        service.updateConfig(1L, update);

        assertEquals("new.key", existing.getConfigKey());
    }

    @Test
    void updateConfigShouldNotOverwriteWithNullFields() {
        ConfigServiceImpl service = makeService();
        SysConfig existing = new SysConfig();
        existing.setId(1L);
        existing.setConfigKey("site.name");
        existing.setConfigValue("OriginalValue");
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        service.updateConfig(1L, new SysConfig()); // all fields null

        assertEquals("OriginalValue", existing.getConfigValue());
        assertEquals("site.name", existing.getConfigKey());
    }

    // --- getConfigValue ---

    @Test
    void getConfigValueShouldReturnStoredValueWhenKeyExists() {
        ConfigServiceImpl service = makeService();
        SysConfig config = new SysConfig();
        config.setConfigKey("max.users");
        config.setConfigValue("100");
        doReturn(config).when(service).getConfigByKey("max.users");

        String value = service.getConfigValue("max.users", "50");

        assertEquals("100", value);
    }

    @Test
    void getConfigValueShouldReturnDefaultWhenKeyNotFound() {
        ConfigServiceImpl service = makeService();
        doReturn(null).when(service).getConfigByKey("missing.key");

        String value = service.getConfigValue("missing.key", "default");

        assertEquals("default", value);
    }

    @Test
    void getConfigValueShouldReturnDefaultWhenValueIsBlank() {
        ConfigServiceImpl service = makeService();
        SysConfig config = new SysConfig();
        config.setConfigKey("some.key");
        config.setConfigValue("  ");
        doReturn(config).when(service).getConfigByKey("some.key");

        String value = service.getConfigValue("some.key", "fallback");

        assertEquals("fallback", value);
    }

    // --- deleteConfig ---

    @Test
    void deleteConfigShouldCallRemoveById() {
        ConfigServiceImpl service = makeService();
        doReturn(true).when(service).removeById(5L);

        service.deleteConfig(5L);

        verify(service).removeById(5L);
    }
}
