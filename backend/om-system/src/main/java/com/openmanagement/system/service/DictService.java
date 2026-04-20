package com.openmanagement.system.service;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.system.domain.entity.SysDictItem;
import com.openmanagement.system.domain.entity.SysDictType;

import java.util.List;

public interface DictService {
    PageResult<SysDictType> pageDictTypes(PageQuery pageQuery, String dictType, String dictName);
    void createDictType(SysDictType dictType);
    void updateDictType(Long id, SysDictType dictType);
    void deleteDictType(Long id);
    List<SysDictItem> listDictItems(String dictType);
    void createDictItem(SysDictItem item);
    void updateDictItem(Long id, SysDictItem item);
    void deleteDictItem(Long id);
}
