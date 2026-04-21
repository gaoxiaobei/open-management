package com.openmanagement.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.system.domain.entity.SysDictItem;
import com.openmanagement.system.domain.entity.SysDictType;
import com.openmanagement.system.mapper.DictItemMapper;
import com.openmanagement.system.mapper.DictTypeMapper;
import com.openmanagement.system.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final DictTypeMapper dictTypeMapper;
    private final DictItemMapper dictItemMapper;

    @Override
    public PageResult<SysDictType> pageDictTypes(PageQuery pageQuery, String dictType, String dictName) {
        Page<SysDictType> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<SysDictType>()
                .like(StringUtils.hasText(dictType), SysDictType::getDictType, dictType)
                .like(StringUtils.hasText(dictName), SysDictType::getDictName, dictName)
                .orderByDesc(SysDictType::getCreatedAt);
        Page<SysDictType> result = dictTypeMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDictType(SysDictType dictType) {
        if (!StringUtils.hasText(dictType.getStatus())) {
            dictType.setStatus(CommonConstants.STATUS_NORMAL);
        }
        dictTypeMapper.insert(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(Long id, SysDictType dictType) {
        dictType.setId(id);
        dictTypeMapper.updateById(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long id) {
        dictTypeMapper.deleteById(id);
    }

    @Override
    public List<SysDictItem> listDictItems(String dictType, boolean includeDisabled) {
        return dictItemMapper.selectList(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictType, dictType)
                .eq(!includeDisabled, SysDictItem::getStatus, CommonConstants.STATUS_NORMAL)
                .orderByAsc(SysDictItem::getSortOrder)
                .orderByAsc(SysDictItem::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDictItem(SysDictItem item) {
        if (!StringUtils.hasText(item.getStatus())) {
            item.setStatus(CommonConstants.STATUS_NORMAL);
        }
        dictItemMapper.insert(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictItem(Long id, SysDictItem item) {
        item.setId(id);
        dictItemMapper.updateById(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictItem(Long id) {
        dictItemMapper.deleteById(id);
    }
}
