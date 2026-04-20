package com.openmanagement.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.system.domain.entity.SysUser;
import com.openmanagement.system.dto.UserCreateRequest;
import com.openmanagement.system.mapper.UserMapper;
import com.openmanagement.system.service.UserService;
import com.openmanagement.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {

    @Override
    public PageResult<UserVO> pageUsers(PageQuery pageQuery, String username, String realName, Long deptId, String status) {
        // TODO: implement pagination query with filters
        return PageResult.of(0L, Collections.emptyList());
    }

    @Override
    public void createUser(UserCreateRequest request) {
        // TODO: encode password, assign roles, save user
    }

    @Override
    public void updateUser(Long id, UserCreateRequest request) {
        // TODO: update user fields and roles
    }

    @Override
    public void resetPassword(Long id) {
        // TODO: reset password to default value
    }
}
