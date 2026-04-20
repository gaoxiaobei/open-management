package com.openmanagement.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.openmanagement.auth.dto.LoginRequest;
import com.openmanagement.auth.dto.LoginResponse;
import com.openmanagement.auth.service.CaptchaService;
import com.openmanagement.auth.service.LoginService;
import com.openmanagement.auth.service.PasswordPolicyService;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.system.domain.entity.SysMenu;
import com.openmanagement.system.domain.entity.SysRole;
import com.openmanagement.system.domain.entity.SysUser;
import com.openmanagement.system.service.MenuService;
import com.openmanagement.system.service.RoleService;
import com.openmanagement.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final CaptchaService captchaService;
    private final PasswordPolicyService passwordPolicyService;
    private final UserService userService;
    private final RoleService roleService;
    private final MenuService menuService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (!captchaService.validateCaptcha(request.getCaptchaKey(), request.getCaptcha())) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR.getCode(), ErrorCode.CAPTCHA_ERROR.getMessage());
        }
        String failKey = CommonConstants.LOGIN_FAIL_CACHE_KEY + request.getUsername();
        String failCountStr = stringRedisTemplate.opsForValue().get(failKey);
        int failCount = failCountStr == null ? 0 : Integer.parseInt(failCountStr);
        if (failCount >= CommonConstants.LOGIN_FAIL_MAX) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED.getCode(), ErrorCode.ACCOUNT_LOCKED.getMessage());
        }
        SysUser user = userService.getUserByUsername(request.getUsername());
        if (user == null) {
            incrementFailCount(failKey, failCount);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
        }
        if (CommonConstants.STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED.getCode(), ErrorCode.USER_DISABLED.getMessage());
        }
        if (!passwordPolicyService.matches(request.getPassword(), user.getPasswordHash())) {
            incrementFailCount(failKey, failCount);
            throw new BusinessException(ErrorCode.PASSWORD_ERROR.getCode(), ErrorCode.PASSWORD_ERROR.getMessage());
        }
        stringRedisTemplate.delete(failKey);
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();
        List<SysRole> roles = roleService.listRolesByUserId(user.getId());
        List<String> roleNames = roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());
        List<Long> roleIds = roles.stream().map(SysRole::getId).collect(Collectors.toList());
        List<SysMenu> allMenus = menuService.listMenusByRoleIds(roleIds);
        List<String> permissions = allMenus.stream()
                .filter(m -> m.getPermissionCode() != null && !m.getPermissionCode().isEmpty())
                .map(SysMenu::getPermissionCode)
                .distinct()
                .collect(Collectors.toList());
        List<SysMenu> navMenus = allMenus.stream()
                .filter(m -> !"button".equals(m.getMenuType()))
                .collect(Collectors.toList());
        List<SysMenu> menuTree = menuService.buildMenuTree(navMenus);
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setRoles(roleNames);
        userInfo.setPermissions(permissions);
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(userInfo);
        response.setMenus(toMenuVOList(menuTree));
        return response;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    private void incrementFailCount(String failKey, int currentCount) {
        int newCount = currentCount + 1;
        stringRedisTemplate.opsForValue().set(failKey, String.valueOf(newCount),
                CommonConstants.LOGIN_LOCK_SECONDS, TimeUnit.SECONDS);
    }

    private List<LoginResponse.MenuVO> toMenuVOList(List<SysMenu> menus) {
        if (menus == null) return null;
        return menus.stream().map(this::toMenuVO).collect(Collectors.toList());
    }

    private LoginResponse.MenuVO toMenuVO(SysMenu menu) {
        LoginResponse.MenuVO vo = new LoginResponse.MenuVO();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setMenuName(menu.getMenuName());
        vo.setMenuType(menu.getMenuType());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setIcon(menu.getIcon());
        vo.setPermissionCode(menu.getPermissionCode());
        vo.setSortOrder(menu.getSortOrder());
        vo.setChildren(toMenuVOList(menu.getChildren()));
        return vo;
    }
}
