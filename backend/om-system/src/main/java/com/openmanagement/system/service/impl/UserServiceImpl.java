package com.openmanagement.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.common.annotation.DataPermission;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.context.DataPermissionContext;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.system.domain.entity.SysRoleUser;
import com.openmanagement.system.domain.entity.SysUser;
import com.openmanagement.system.dto.UserCreateRequest;
import com.openmanagement.system.mapper.RoleUserMapper;
import com.openmanagement.system.mapper.UserMapper;
import com.openmanagement.system.service.UserService;
import com.openmanagement.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {

    private final RoleUserMapper roleUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @DataPermission
    public PageResult<UserVO> pageUsers(PageQuery pageQuery, String username, String realName, Long deptId, String status) {
        Page<SysUser> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(username), SysUser::getUsername, username)
                .like(StringUtils.hasText(realName), SysUser::getRealName, realName)
                .eq(deptId != null, SysUser::getDeptId, deptId)
                .eq(StringUtils.hasText(status), SysUser::getStatus, status)
                .orderByDesc(SysUser::getCreatedAt);

        DataPermissionContext.DataScope dataScope = DataPermissionContext.get();
        if (dataScope != null && !dataScope.isAllData()) {
            wrapper.and(w -> {
                boolean hasCondition = false;
                if (!dataScope.safeDeptIds().isEmpty()) {
                    w.in(SysUser::getDeptId, dataScope.safeDeptIds());
                    hasCondition = true;
                }
                if (dataScope.getUserId() != null) {
                    if (hasCondition) {
                        w.or();
                    }
                    w.eq(SysUser::getId, dataScope.getUserId());
                    hasCondition = true;
                }
                if (!hasCondition) {
                    w.eq(SysUser::getId, -1L);
                }
            });
        }
        Page<SysUser> result = page(page, wrapper);
        List<UserVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(result.getTotal(), voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserCreateRequest request) {
        long count = count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        user.setDeptId(request.getDeptId());
        user.setPositionId(request.getPositionId());
        user.setStatus(CommonConstants.STATUS_NORMAL);
        user.setPasswordHash(passwordEncoder.encode(CommonConstants.DEFAULT_PASSWORD));
        save(user);
        assignRoles(user.getId(), request.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserCreateRequest request) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
        }
        if (StringUtils.hasText(request.getRealName())) user.setRealName(request.getRealName());
        if (StringUtils.hasText(request.getMobile())) user.setMobile(request.getMobile());
        if (StringUtils.hasText(request.getEmail())) user.setEmail(request.getEmail());
        if (request.getDeptId() != null) user.setDeptId(request.getDeptId());
        if (request.getPositionId() != null) user.setPositionId(request.getPositionId());
        updateById(user);
        if (request.getRoleIds() != null) {
            roleUserMapper.delete(new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, id));
            assignRoles(id, request.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
        }
        user.setPasswordHash(passwordEncoder.encode(CommonConstants.DEFAULT_PASSWORD));
        updateById(user);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
        }
        UserVO vo = toVO(user);
        vo.setRoleIds(getUserRoleIds(id));
        return vo;
    }

    @Override
    public SysUser getUserByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        List<SysRoleUser> roleUsers = roleUserMapper.selectList(
                new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, userId));
        return roleUsers.stream().map(SysRoleUser::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeUserStatus(Long id, String status) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
        }
        user.setStatus(status);
        updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        roleUserMapper.delete(new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, id));
        removeById(id);
    }

    private void assignRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return;
        roleIds.forEach(roleId -> {
            SysRoleUser ru = new SysRoleUser();
            ru.setUserId(userId);
            ru.setRoleId(roleId);
            roleUserMapper.insert(ru);
        });
    }

    private UserVO toVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setMobile(user.getMobile());
        vo.setEmail(user.getEmail());
        vo.setDeptId(user.getDeptId());
        vo.setPositionId(user.getPositionId());
        vo.setStatus(user.getStatus());
        vo.setLastLoginTime(user.getLastLoginTime());
        return vo;
    }
}
