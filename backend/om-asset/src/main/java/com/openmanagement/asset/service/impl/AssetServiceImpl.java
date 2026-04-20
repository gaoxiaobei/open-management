package com.openmanagement.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.asset.domain.entity.AssetInfo;
import com.openmanagement.asset.mapper.AssetInfoMapper;
import com.openmanagement.asset.service.AssetService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl extends ServiceImpl<AssetInfoMapper, AssetInfo> implements AssetService {

    @Override
    public PageResult<AssetInfo> pageAssets(PageQuery pageQuery, String assetCode, String assetName,
                                            String category, String assetStatus) {
        // TODO: implement pagination query with filters
        return PageResult.of(0L, Collections.emptyList());
    }
}
