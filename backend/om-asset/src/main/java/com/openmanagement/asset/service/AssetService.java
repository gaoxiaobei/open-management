package com.openmanagement.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openmanagement.asset.domain.entity.AssetInfo;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;

public interface AssetService extends IService<AssetInfo> {

    PageResult<AssetInfo> pageAssets(PageQuery pageQuery, String assetCode, String assetName,
                                     String category, String assetStatus);
}
