package com.openmanagement.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.system.domain.entity.SysConfig;
import com.openmanagement.system.mapper.ConfigMapper;
import com.openmanagement.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, SysConfig> implements ConfigService {

    @Override
    public PageResult<SysConfig> pageConfigs(PageQuery pageQuery, String configKey, String configName) {
        Page<SysConfig> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<SysConfig>()
                .like(StringUtils.hasText(configKey), SysConfig::getConfigKey, configKey)
                .like(StringUtils.hasText(configName), SysConfig::getConfigName, configName)
                .orderByDesc(SysConfig::getCreatedAt);
        Page<SysConfig> result = page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createConfig(SysConfig config) {
        save(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(Long id, SysConfig config) {
        SysConfig existing = getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "参数配置不存在");
        }
        if (config.getConfigKey() != null) existing.setConfigKey(config.getConfigKey());
        if (config.getConfigValue() != null) existing.setConfigValue(config.getConfigValue());
        if (config.getConfigName() != null) existing.setConfigName(config.getConfigName());
        if (config.getRemark() != null) existing.setRemark(config.getRemark());
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        removeById(id);
    }

    @Override
    public SysConfig getConfigByKey(String configKey) {
        return getOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, configKey));
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        SysConfig config = getConfigByKey(configKey);
        return (config != null && StringUtils.hasText(config.getConfigValue()))
                ? config.getConfigValue()
                : defaultValue;
    }
}
