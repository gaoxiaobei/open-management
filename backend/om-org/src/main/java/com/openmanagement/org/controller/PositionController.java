package com.openmanagement.org.controller;

import com.openmanagement.common.result.R;
import com.openmanagement.org.domain.entity.SysPosition;
import com.openmanagement.org.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/org/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public R<List<SysPosition>> listPositions(@RequestParam(required = false) Long deptId) {
        if (deptId != null) {
            return R.ok(positionService.listByDeptId(deptId));
        }
        return R.ok(positionService.list());
    }
}
