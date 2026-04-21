package com.openmanagement.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.asset.domain.entity.AssetInfo;
import com.openmanagement.asset.mapper.AssetInfoMapper;
import com.openmanagement.asset.service.AssetService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl extends ServiceImpl<AssetInfoMapper, AssetInfo> implements AssetService {

    @Override
    public PageResult<AssetInfo> pageAssets(PageQuery pageQuery, String assetCode, String assetName,
                                            String category, String assetStatus) {
        Page<AssetInfo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<AssetInfo> result = page(page, new LambdaQueryWrapper<AssetInfo>()
                .like(StringUtils.hasText(assetCode), AssetInfo::getAssetCode, assetCode)
                .like(StringUtils.hasText(assetName), AssetInfo::getAssetName, assetName)
                .eq(StringUtils.hasText(category), AssetInfo::getCategory, category)
                .eq(StringUtils.hasText(assetStatus), AssetInfo::getAssetStatus, assetStatus)
                .orderByDesc(AssetInfo::getCreatedAt, AssetInfo::getId));
        return PageResult.of(result.getTotal(), result.getRecords());
    }
}
