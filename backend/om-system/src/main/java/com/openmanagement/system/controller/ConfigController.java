package com.openmanagement.system.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.annotation.RequirePermission;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import com.openmanagement.system.domain.entity.SysConfig;
import com.openmanagement.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/configs")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping
    @RequirePermission("system:config:query")
    public R<PageResult<SysConfig>> pageConfigs(PageQuery pageQuery,
                                                @RequestParam(required = false) String configKey,
                                                @RequestParam(required = false) String configName) {
        return R.ok(configService.pageConfigs(pageQuery, configKey, configName));
    }

    @GetMapping("/key/{configKey}")
    @RequirePermission("system:config:query")
    public R<SysConfig> getConfigByKey(@PathVariable String configKey) {
        return R.ok(configService.getConfigByKey(configKey));
    }

    @PostMapping
    @RequirePermission("system:config:create")
    public R<Void> createConfig(@RequestBody SysConfig config) {
        configService.createConfig(config);
        return R.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("system:config:update")
    public R<Void> updateConfig(@PathVariable Long id, @RequestBody SysConfig config) {
        configService.updateConfig(id, config);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:config:delete")
    public R<Void> deleteConfig(@PathVariable Long id) {
        configService.deleteConfig(id);
        return R.ok();
    }
}
