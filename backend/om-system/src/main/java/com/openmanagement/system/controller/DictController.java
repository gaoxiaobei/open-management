package com.openmanagement.system.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.annotation.RequirePermission;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import com.openmanagement.system.domain.entity.SysDictItem;
import com.openmanagement.system.domain.entity.SysDictType;
import com.openmanagement.system.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @GetMapping("/types")
    @RequirePermission("system:dict:query")
    public R<PageResult<SysDictType>> pageDictTypes(PageQuery pageQuery,
                                                    @RequestParam(required = false) String dictType,
                                                    @RequestParam(required = false) String dictName) {
        return R.ok(dictService.pageDictTypes(pageQuery, dictType, dictName));
    }

    @PostMapping("/types")
    @RequirePermission("system:dict:create")
    public R<Void> createDictType(@RequestBody SysDictType dictType) {
        dictService.createDictType(dictType);
        return R.ok();
    }

    @PutMapping("/types/{id}")
    @RequirePermission("system:dict:update")
    public R<Void> updateDictType(@PathVariable Long id, @RequestBody SysDictType dictType) {
        dictService.updateDictType(id, dictType);
        return R.ok();
    }

    @DeleteMapping("/types/{id}")
    @RequirePermission("system:dict:delete")
    public R<Void> deleteDictType(@PathVariable Long id) {
        dictService.deleteDictType(id);
        return R.ok();
    }

    @GetMapping("/items/{dictType}")
    @RequirePermission("system:dict:query")
    public R<List<SysDictItem>> listDictItems(@PathVariable String dictType,
                                              @RequestParam(defaultValue = "false") boolean includeDisabled) {
        return R.ok(dictService.listDictItems(dictType, includeDisabled));
    }

    @PostMapping("/items")
    @RequirePermission("system:dict:create")
    public R<Void> createDictItem(@RequestBody SysDictItem item) {
        dictService.createDictItem(item);
        return R.ok();
    }

    @PutMapping("/items/{id}")
    @RequirePermission("system:dict:update")
    public R<Void> updateDictItem(@PathVariable Long id, @RequestBody SysDictItem item) {
        dictService.updateDictItem(id, item);
        return R.ok();
    }

    @DeleteMapping("/items/{id}")
    @RequirePermission("system:dict:delete")
    public R<Void> deleteDictItem(@PathVariable Long id) {
        dictService.deleteDictItem(id);
        return R.ok();
    }
}
