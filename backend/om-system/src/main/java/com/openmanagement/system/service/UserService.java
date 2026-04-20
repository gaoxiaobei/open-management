package com.openmanagement.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.system.domain.entity.SysUser;
import com.openmanagement.system.dto.UserCreateRequest;
import com.openmanagement.system.vo.UserVO;

public interface UserService extends IService<SysUser> {

    PageResult<UserVO> pageUsers(PageQuery pageQuery, String username, String realName, Long deptId, String status);

    void createUser(UserCreateRequest request);

    void updateUser(Long id, UserCreateRequest request);

    void resetPassword(Long id);
}
