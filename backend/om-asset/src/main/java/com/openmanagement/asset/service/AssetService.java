package com.openmanagement.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openmanagement.asset.domain.entity.AssetInfo;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;

public interface AssetService extends IService<AssetInfo> {

    PageResult<AssetInfo> pageAssets(PageQuery pageQuery, String assetCode, String assetName,
                                     String category, String assetStatus);

    void createAsset(AssetInfo asset);

    void updateAsset(Long id, AssetInfo asset);

    void updateAssetStatus(Long id, String assetStatus);

    void deleteAsset(Long id);
}
