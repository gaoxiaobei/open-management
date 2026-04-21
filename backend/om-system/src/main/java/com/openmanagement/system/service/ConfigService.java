package com.openmanagement.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.system.domain.entity.SysConfig;

public interface ConfigService extends IService<SysConfig> {
    PageResult<SysConfig> pageConfigs(PageQuery pageQuery, String configKey, String configName);
    void createConfig(SysConfig config);
    void updateConfig(Long id, SysConfig config);
    void deleteConfig(Long id);
    SysConfig getConfigByKey(String configKey);
    String getConfigValue(String configKey, String defaultValue);
}
