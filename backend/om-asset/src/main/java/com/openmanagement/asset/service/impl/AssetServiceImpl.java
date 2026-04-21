package com.openmanagement.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.asset.domain.entity.AssetInfo;
import com.openmanagement.asset.mapper.AssetInfoMapper;
import com.openmanagement.asset.service.AssetService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAsset(AssetInfo asset) {
        if (asset == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "资产信息不能为空");
        }
        if (!StringUtils.hasText(asset.getAssetCode())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "资产编号不能为空");
        }
        if (!StringUtils.hasText(asset.getAssetName())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "资产名称不能为空");
        }
        if (!StringUtils.hasText(asset.getCategory())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "资产类别不能为空");
        }
        long count = count(new LambdaQueryWrapper<AssetInfo>()
                .eq(AssetInfo::getAssetCode, asset.getAssetCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "资产编号已存在");
        }
        asset.setId(null);
        try {
            save(asset);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "资产编号已存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAsset(Long id, AssetInfo asset) {
        if (asset == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "资产信息不能为空");
        }
        AssetInfo existing = getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "资产不存在");
        }
        if (StringUtils.hasText(asset.getAssetCode())
                && !asset.getAssetCode().equals(existing.getAssetCode())) {
            long count = count(new LambdaQueryWrapper<AssetInfo>()
                    .eq(AssetInfo::getAssetCode, asset.getAssetCode()));
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "资产编号已存在");
            }
        }
        asset.setId(id);
        try {
            updateById(asset);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "资产编号已存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssetStatus(Long id, String assetStatus) {
        AssetInfo existing = getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "资产不存在");
        }
        if (!StringUtils.hasText(assetStatus)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "资产状态不能为空");
        }
        existing.setAssetStatus(assetStatus);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAsset(Long id) {
        if (getById(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "资产不存在");
        }
        removeById(id);
    }
}
