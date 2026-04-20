package com.openmanagement.org.controller;

import com.openmanagement.common.result.R;
import com.openmanagement.org.domain.entity.SysDept;
import com.openmanagement.org.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/org/depts")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @GetMapping("/tree")
    public R<List<SysDept>> getDeptTree() {
        return R.ok(deptService.getDeptTree());
    }
}
