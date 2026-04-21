package com.openmanagement.asset.controller;

import com.openmanagement.asset.domain.entity.AssetInfo;
import com.openmanagement.asset.service.AssetService;
import com.openmanagement.common.annotation.OperateLog;
import com.openmanagement.common.annotation.RequirePermission;
import com.openmanagement.common.base.PageQuery;
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

    @PostMapping
    @OperateLog(module = "资产管理", name = "新增资产")
    @RequirePermission("asset:info:create")
    public R<Void> createAsset(@RequestBody AssetInfo asset) {
        assetService.createAsset(asset);
        return R.ok();
    }

    @GetMapping("/{id}")
    @RequirePermission("asset:info:query")
    public R<AssetInfo> getAsset(@PathVariable Long id) {
        return R.ok(assetService.getById(id));
    }

    @PutMapping("/{id}")
    @OperateLog(module = "资产管理", name = "更新资产")
    @RequirePermission("asset:info:update")
    public R<Void> updateAsset(@PathVariable Long id, @RequestBody AssetInfo asset) {
        assetService.updateAsset(id, asset);
        return R.ok();
    }

    @PutMapping("/{id}/status")
    @OperateLog(module = "资产管理", name = "修改资产状态")
    @RequirePermission("asset:info:update")
    public R<Void> updateAssetStatus(@PathVariable Long id, @RequestParam String assetStatus) {
        assetService.updateAssetStatus(id, assetStatus);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @OperateLog(module = "资产管理", name = "删除资产")
    @RequirePermission("asset:info:delete")
    public R<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return R.ok();
    }
}
