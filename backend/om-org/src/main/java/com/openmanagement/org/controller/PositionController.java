package com.openmanagement.org.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.annotation.RequirePermission;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import com.openmanagement.org.domain.entity.SysPosition;
import com.openmanagement.org.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/org/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    @RequirePermission("org:position:query")
    public R<List<SysPosition>> listPositions(@RequestParam(required = false) Long deptId) {
        if (deptId != null) {
            return R.ok(positionService.listByDeptId(deptId));
        }
        return R.ok(positionService.list());
    }

    @GetMapping("/page")
    @RequirePermission("org:position:query")
    public R<PageResult<SysPosition>> pagePositions(PageQuery pageQuery,
                                                    @RequestParam(required = false) Long deptId,
                                                    @RequestParam(required = false) String positionName) {
        return R.ok(positionService.pagePositions(pageQuery, deptId, positionName));
    }

    @PostMapping
    @RequirePermission("org:position:create")
    public R<Void> createPosition(@RequestBody SysPosition position) {
        positionService.createPosition(position);
        return R.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("org:position:update")
    public R<Void> updatePosition(@PathVariable Long id, @RequestBody SysPosition position) {
        positionService.updatePosition(id, position);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("org:position:delete")
    public R<Void> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return R.ok();
    }
}
