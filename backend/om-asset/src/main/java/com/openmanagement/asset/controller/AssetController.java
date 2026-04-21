package com.openmanagement.asset.controller;

import com.openmanagement.asset.domain.entity.AssetInfo;
import com.openmanagement.asset.service.AssetService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.annotation.RequirePermission;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    @RequirePermission("asset:info:query")
    public R<PageResult<AssetInfo>> pageAssets(PageQuery pageQuery,
                                               @RequestParam(required = false) String assetCode,
                                               @RequestParam(required = false) String assetName,
                                               @RequestParam(required = false) String category,
                                               @RequestParam(required = false) String assetStatus) {
        return R.ok(assetService.pageAssets(pageQuery, assetCode, assetName, category, assetStatus));
    }

    @GetMapping("/{id}")
    @RequirePermission("asset:info:query")
    public R<AssetInfo> getAsset(@PathVariable Long id) {
        return R.ok(assetService.getById(id));
    }
}
