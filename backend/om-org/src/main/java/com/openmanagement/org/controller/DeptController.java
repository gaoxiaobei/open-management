package com.openmanagement.org.controller;

import com.openmanagement.common.result.R;
import com.openmanagement.org.domain.entity.SysDept;
import com.openmanagement.org.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public R<Void> createDept(@RequestBody SysDept dept) {
        deptService.createDept(dept);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> updateDept(@PathVariable Long id, @RequestBody SysDept dept) {
        deptService.updateDept(id, dept);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteDept(@PathVariable Long id) {
        deptService.deleteDept(id);
        return R.ok();
    }
}
