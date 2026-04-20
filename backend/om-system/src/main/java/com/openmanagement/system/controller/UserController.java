package com.openmanagement.system.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.annotation.OperateLog;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import com.openmanagement.system.dto.UserCreateRequest;
import com.openmanagement.system.service.UserService;
import com.openmanagement.system.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public R<PageResult<UserVO>> pageUsers(PageQuery pageQuery,
                                           @RequestParam(required = false) String username,
                                           @RequestParam(required = false) String realName,
                                           @RequestParam(required = false) Long deptId,
                                           @RequestParam(required = false) String status) {
        return R.ok(userService.pageUsers(pageQuery, username, realName, deptId, status));
    }

    @PostMapping
    @OperateLog(module = "系统管理-用户", name = "创建用户")
    public R<Void> createUser(@Valid @RequestBody UserCreateRequest request) {
        userService.createUser(request);
        return R.ok();
    }

    @PutMapping("/{id}")
    @OperateLog(module = "系统管理-用户", name = "更新用户")
    public R<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserCreateRequest request) {
        userService.updateUser(id, request);
        return R.ok();
    }

    @PostMapping("/{id}/reset-password")
    @OperateLog(module = "系统管理-用户", name = "重置密码")
    public R<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<UserVO> getUserById(@PathVariable Long id) {
        return R.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}/status")
    @OperateLog(module = "系统管理-用户", name = "修改用户状态")
    public R<Void> changeUserStatus(@PathVariable Long id, @RequestParam String status) {
        userService.changeUserStatus(id, status);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @OperateLog(module = "系统管理-用户", name = "删除用户")
    public R<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.ok();
    }
}
