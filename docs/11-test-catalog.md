# 测试用例目录

> 最后更新：2026-04-24

本文档列出项目后端所有单元测试用例，按模块分类。共 **17 个测试类**，覆盖 **7 个模块**，使用 JUnit 5 + Mockito。

---

## 1. om-auth — 认证授权模块

### 1.1 CaptchaServiceImplTest

被测类：`CaptchaServiceImpl` | Mock 依赖：`StringRedisTemplate`、`ValueOperations<String, String>`

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `generateCaptchaShouldReturnUniqueKeyEachTime` | `generateCaptcha()` | 连续两次调用生成不同的验证码 key（UUID 唯一性） |
| 2 | `generateCaptchaShouldReturnBase64PngImage` | `generateCaptcha()` | 返回的图片字符串以 `data:image/png;base64,` 开头 |
| 3 | `generateCaptchaShouldStoreCodeInRedisWithFiveMinuteTtl` | `generateCaptcha()` | 验证码答案存入 Redis，key 带正确前缀，TTL 为 5 分钟 |
| 4 | `validateCaptchaShouldReturnFalseWhenKeyIsNull` | `validateCaptcha(key, code)` | key 为 null 时返回 false，不访问 Redis |
| 5 | `validateCaptchaShouldReturnFalseWhenCodeIsNull` | `validateCaptcha(key, code)` | code 为 null 时返回 false，不访问 Redis |
| 6 | `validateCaptchaShouldReturnFalseWhenKeyNotFoundInRedis` | `validateCaptcha(key, code)` | Redis 中无对应 key 时返回 false |
| 7 | `validateCaptchaShouldReturnFalseWhenCodeMismatch` | `validateCaptcha(key, code)` | 输入验证码与存储的不匹配时返回 false |
| 8 | `validateCaptchaShouldBeCaseInsensitive` | `validateCaptcha(key, code)` | 验证码校验不区分大小写 |
| 9 | `validateCaptchaShouldDeleteRedisKeyOnSuccessfulValidation` | `validateCaptcha(key, code)` | 验证成功后删除 Redis key（一次性使用） |
| 10 | `validateCaptchaShouldNotDeleteKeyOnFailedValidation` | `validateCaptcha(key, code)` | 验证失败时保留 Redis key |

### 1.2 LoginServiceImplTest

被测类：`LoginServiceImpl` | Mock 依赖：`CaptchaService`、`PasswordPolicyService`、`UserService`、`RoleService`、`MenuService`、`StringRedisTemplate`、`LoginLogService`、`ValueOperations<String, String>`

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `loginShouldThrowCaptchaErrorWhenCaptchaInvalid` | `login(LoginRequest)` | 验证码校验失败时抛出 `CAPTCHA_ERROR` 并记录登录日志 |
| 2 | `loginShouldThrowAccountLockedWhenFailCountReachesMax` | `login(LoginRequest)` | Redis 中失败次数达到上限时抛出 `ACCOUNT_LOCKED` |
| 3 | `loginShouldThrowUserNotFoundAndIncrementFailCount` | `login(LoginRequest)` | 用户不存在时抛出 `USER_NOT_FOUND`，失败计数置 1 并设置 TTL |
| 4 | `loginShouldThrowUserNotFoundAndAccumulateExistingFailCount` | `login(LoginRequest)` | 用户不存在时累加已有失败计数（如 2→3） |
| 5 | `loginShouldThrowUserDisabledWithoutIncrementingFailCount` | `login(LoginRequest)` | 用户被禁用时抛出 `USER_DISABLED`，不增加失败计数 |
| 6 | `loginShouldThrowPasswordErrorAndIncrementFailCount` | `login(LoginRequest)` | 密码错误时抛出 `PASSWORD_ERROR` 并增加失败计数 |

### 1.3 PasswordPolicyServiceImplTest

被测类：`PasswordPolicyServiceImpl` | 依赖：`BCryptPasswordEncoder`（真实实例）

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `checkStrengthShouldThrowWhenPasswordIsNull` | `checkStrength(password)` | 密码为 null 时拒绝 |
| 2 | `checkStrengthShouldThrowWhenPasswordTooShort` | `checkStrength(password)` | 密码短于 8 位时拒绝 |
| 3 | `checkStrengthShouldThrowWhenMissingUpperCase` | `checkStrength(password)` | 密码缺少大写字母时拒绝 |
| 4 | `checkStrengthShouldThrowWhenMissingLowerCase` | `checkStrength(password)` | 密码缺少小写字母时拒绝 |
| 5 | `checkStrengthShouldThrowWhenMissingDigit` | `checkStrength(password)` | 密码缺少数字时拒绝 |
| 6 | `checkStrengthShouldPassForValidPassword` | `checkStrength(password)` | 合法密码 "Password1" 通过 |
| 7 | `checkStrengthShouldPassForMinimumEightCharsMixedPassword` | `checkStrength(password)` | 恰好 8 位混合密码 "Passw0rd" 通过 |
| 8 | `encodedPasswordShouldMatchRawPassword` | `encode(raw)` / `matches(raw, encoded)` | BCrypt 编码后密码与原文匹配 |
| 9 | `matchesShouldReturnFalseForWrongPassword` | `matches(raw, encoded)` | 错误密码与编码不匹配 |

---

## 2. om-oa — OA 审批模块

### 2.1 LeaveApplyServiceImplTest

被测类：`LeaveApplyServiceImpl` | Mock 依赖：`LeaveApplyMapper`

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `submitShouldThrowWhenApplyIsNull` | `submitLeaveApply(apply)` | 申请对象为 null 时抛出 `PARAM_ERROR` |
| 2 | `submitShouldThrowWhenApplicantIdIsNull` | `submitLeaveApply(apply)` | applicantId 为 null 时抛出 `PARAM_ERROR` |
| 3 | `submitShouldThrowWhenLeaveTypeIsNull` | `submitLeaveApply(apply)` | leaveType 为 null 时抛出 `PARAM_ERROR` |
| 4 | `submitShouldThrowWhenLeaveTypeIsBlank` | `submitLeaveApply(apply)` | leaveType 为空白时抛出 `PARAM_ERROR` |
| 5 | `submitShouldThrowWhenStartTimeIsNull` | `submitLeaveApply(apply)` | startTime 为 null 时抛出 `PARAM_ERROR` |
| 6 | `submitShouldThrowWhenEndTimeIsNull` | `submitLeaveApply(apply)` | endTime 为 null 时抛出 `PARAM_ERROR` |
| 7 | `submitShouldThrowWhenEndTimeBeforeStartTime` | `submitLeaveApply(apply)` | endTime 早于 startTime 时抛出 `PARAM_ERROR` |
| 8 | `submitShouldThrowWhenLeaveDaysIsNull` | `submitLeaveApply(apply)` | leaveDays 为 null 时抛出 `PARAM_ERROR` |
| 9 | `submitShouldThrowWhenLeaveDaysIsZero` | `submitLeaveApply(apply)` | leaveDays 为 0 时抛出 `PARAM_ERROR` |
| 10 | `submitShouldThrowWhenLeaveDaysIsNegative` | `submitLeaveApply(apply)` | leaveDays 为负数时抛出 `PARAM_ERROR` |
| 11 | `submitShouldInsertWithGeneratedApplyNumberAndSubmittedStatus` | `submitLeaveApply(apply)` | 成功提交时生成 "LV" 前缀单号，状态为 "SUBMITTED" |
| 12 | `submitShouldClearIdAndProcessInstanceIdBeforeInsert` | `submitLeaveApply(apply)` | 插入前清空 id 和 processInstanceId（防止覆写） |
| 13 | `submitShouldAllowEndTimeEqualToStartTime` | `submitLeaveApply(apply)` | endTime 等于 startTime 时校验通过（边界条件） |

### 2.2 TravelApplyServiceImplTest

被测类：`TravelApplyServiceImpl` | Mock 依赖：`TravelApplyMapper`

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `submitShouldThrowWhenApplyIsNull` | `submitTravelApply(apply)` | 申请对象为 null 时抛出 `PARAM_ERROR` |
| 2 | `submitShouldThrowWhenApplicantIdIsNull` | `submitTravelApply(apply)` | applicantId 为 null 时抛出 `PARAM_ERROR` |
| 3 | `submitShouldThrowWhenDestinationIsNull` | `submitTravelApply(apply)` | destination 为 null 时抛出 `PARAM_ERROR` |
| 4 | `submitShouldThrowWhenDestinationIsBlank` | `submitTravelApply(apply)` | destination 为空白时抛出 `PARAM_ERROR` |
| 5 | `submitShouldThrowWhenStartDateIsNull` | `submitTravelApply(apply)` | startDate 为 null 时抛出 `PARAM_ERROR` |
| 6 | `submitShouldThrowWhenEndDateIsNull` | `submitTravelApply(apply)` | endDate 为 null 时抛出 `PARAM_ERROR` |
| 7 | `submitShouldThrowWhenEndDateBeforeStartDate` | `submitTravelApply(apply)` | endDate 早于 startDate 时抛出 `PARAM_ERROR` |
| 8 | `submitShouldThrowWhenTravelDaysIsNull` | `submitTravelApply(apply)` | travelDays 为 null 时抛出 `PARAM_ERROR` |
| 9 | `submitShouldThrowWhenTravelDaysIsZero` | `submitTravelApply(apply)` | travelDays 为 0 时抛出 `PARAM_ERROR` |
| 10 | `submitShouldThrowWhenTravelDaysIsNegative` | `submitTravelApply(apply)` | travelDays 为负数时抛出 `PARAM_ERROR` |
| 11 | `submitShouldInsertWithGeneratedApplyNumberAndSubmittedStatus` | `submitTravelApply(apply)` | 成功提交时生成 "TR" 前缀单号，状态为 "SUBMITTED" |
| 12 | `submitShouldClearIdAndProcessInstanceIdBeforeInsert` | `submitTravelApply(apply)` | 插入前清空 id 和 processInstanceId |
| 13 | `submitShouldAllowEndDateEqualToStartDate` | `submitTravelApply(apply)` | endDate 等于 startDate 时校验通过（边界条件） |

### 2.3 ExpenseApplyServiceImplTest

被测类：`ExpenseApplyServiceImpl` | Mock 依赖：`ExpenseApplyMapper`

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `submitShouldThrowWhenApplyIsNull` | `submitExpenseApply(apply)` | 申请对象为 null 时抛出 `PARAM_ERROR` |
| 2 | `submitShouldThrowWhenApplicantIdIsNull` | `submitExpenseApply(apply)` | applicantId 为 null 时抛出 `PARAM_ERROR` |
| 3 | `submitShouldThrowWhenExpenseTypeIsNull` | `submitExpenseApply(apply)` | expenseType 为 null 时抛出 `PARAM_ERROR` |
| 4 | `submitShouldThrowWhenExpenseTypeIsBlank` | `submitExpenseApply(apply)` | expenseType 为空白时抛出 `PARAM_ERROR` |
| 5 | `submitShouldThrowWhenTotalAmountIsNull` | `submitExpenseApply(apply)` | totalAmount 为 null 时抛出 `PARAM_ERROR` |
| 6 | `submitShouldThrowWhenTotalAmountIsZero` | `submitExpenseApply(apply)` | totalAmount 为 0 时抛出 `PARAM_ERROR` |
| 7 | `submitShouldThrowWhenTotalAmountIsNegative` | `submitExpenseApply(apply)` | totalAmount 为负数时抛出 `PARAM_ERROR` |
| 8 | `submitShouldInsertWithGeneratedApplyNumberAndSubmittedStatus` | `submitExpenseApply(apply)` | 成功提交时生成 "EX" 前缀单号，状态为 "SUBMITTED" |
| 9 | `submitShouldClearIdAndProcessInstanceIdBeforeInsert` | `submitExpenseApply(apply)` | 插入前清空 id 和 processInstanceId |
| 10 | `submitShouldAcceptSmallPositiveAmount` | `submitExpenseApply(apply)` | 小额正数如 0.01 校验通过 |

---

## 3. om-system — 系统管理模块

### 3.1 UserServiceImplTest

被测类：`UserServiceImpl` | Mock 依赖：`RoleUserMapper` | 特殊模式：spy + `BCryptPasswordEncoder` 真实实例

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `createUserShouldThrowWhenUsernameAlreadyExists` | `createUser(req)` | 用户名已存在时抛出 `PARAM_ERROR`，不保存 |
| 2 | `createUserShouldSaveNewUserWithDefaultStatus` | `createUser(req)` | 新建用户默认状态为 `STATUS_NORMAL` |
| 3 | `createUserShouldHashDefaultPassword` | `createUser(req)` | 默认密码经 BCrypt 哈希后存储 |
| 4 | `updateUserShouldThrowWhenUserNotFound` | `updateUser(id, req)` | 用户不存在时抛出 `USER_NOT_FOUND` |
| 5 | `updateUserShouldApplyRealNameChange` | `updateUser(id, req)` | 更新用户时应用 realName 变更 |
| 6 | `updateUserShouldReplaceRolesWhenRoleIdsProvided` | `updateUser(id, req)` | 提供 roleIds 时删除旧角色映射并插入新映射 |
| 7 | `resetPasswordShouldThrowWhenUserNotFound` | `resetPassword(id)` | 用户不存在时抛出 `USER_NOT_FOUND` |
| 8 | `resetPasswordShouldSetNewHashedPasswordForExistingUser` | `resetPassword(id)` | 重置密码为新哈希值并持久化 |
| 9 | `changeUserStatusShouldThrowWhenUserNotFound` | `changeUserStatus(id, status)` | 用户不存在时抛出 `USER_NOT_FOUND` |
| 10 | `changeUserStatusShouldUpdateStatusField` | `changeUserStatus(id, status)` | 更新用户状态字段并持久化 |
| 11 | `getUserByIdShouldThrowWhenUserNotFound` | `getUserById(id)` | 用户不存在时抛出 `USER_NOT_FOUND` |
| 12 | `deleteUserShouldCleanRoleMappingsBeforeRemoval` | `deleteUser(id)` | 删除用户前先清除角色-用户映射 |

### 3.2 RoleServiceImplTest

被测类：`RoleServiceImpl` | Mock 依赖：`RoleMenuMapper`、`RoleUserMapper` | 特殊模式：spy

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `createRoleShouldThrowWhenRoleCodeAlreadyExists` | `createRole(role)` | 角色编码已存在时抛出 `PARAM_ERROR`，不保存 |
| 2 | `createRoleShouldSetDefaultStatusWhenBlank` | `createRole(role)` | 状态为空时默认设为 `STATUS_NORMAL` |
| 3 | `createRoleShouldPreserveExplicitStatus` | `createRole(role)` | 显式设置的状态（如 `STATUS_DISABLED`）被保留 |
| 4 | `updateRoleShouldThrowWhenRoleNotFound` | `updateRole(id, role)` | 角色不存在时抛出 `ROLE_NOT_FOUND` |
| 5 | `updateRoleShouldApplyRoleNameChange` | `updateRole(id, role)` | 更新角色时应用 roleName 变更 |
| 6 | `getRoleByIdShouldThrowWhenRoleNotFound` | `getRoleById(id)` | 角色不存在时抛出 `ROLE_NOT_FOUND` |
| 7 | `getRoleByIdShouldReturnRoleWhenFound` | `getRoleById(id)` | 角色存在时正确返回 |
| 8 | `deleteRoleShouldCascadeDeleteMenuAndUserMappings` | `deleteRole(id)` | 删除角色前级联删除角色-菜单和角色-用户映射 |
| 9 | `assignRoleMenusShouldThrowWhenRoleNotFound` | `assignRoleMenus(roleId, menuIds)` | 角色不存在时抛出 `ROLE_NOT_FOUND` |
| 10 | `assignRoleMenusShouldReplaceAllExistingMenuMappings` | `assignRoleMenus(roleId, menuIds)` | 分配菜单时删除旧映射并插入新映射 |
| 11 | `assignRoleMenusShouldOnlyClearWhenMenuIdsEmpty` | `assignRoleMenus(roleId, menuIds)` | 菜单 ID 列表为空时仅清除现有映射 |

### 3.3 MenuServiceImplTest

被测类：`MenuServiceImpl` | Mock 依赖：`RoleMenuMapper` | 特殊模式：spy

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `createMenuShouldDefaultParentIdToRootWhenNull` | `createMenu(menu)` | parentId 为 null 时默认设为 `ROOT_PARENT_ID` |
| 2 | `createMenuShouldDefaultStatusToEnabledWhenBlank` | `createMenu(menu)` | status 为空时默认设为 `STATUS_NORMAL` |
| 3 | `createMenuShouldPreserveExplicitParentId` | `createMenu(menu)` | 显式设置的 parentId 被保留 |
| 4 | `updateMenuShouldThrowWhenMenuNotFound` | `updateMenu(id, menu)` | 菜单不存在时抛出 `MENU_NOT_FOUND` |
| 5 | `updateMenuShouldApplyNameChange` | `updateMenu(id, menu)` | 更新菜单时应用 menuName 变更 |
| 6 | `getMenuByIdShouldThrowWhenMenuNotFound` | `getMenuById(id)` | 菜单不存在时抛出 `MENU_NOT_FOUND` |
| 7 | `buildMenuTreeShouldReturnEmptyListWhenInputIsEmpty` | `buildMenuTree(menus)` | 空列表输入返回空列表 |
| 8 | `buildMenuTreeShouldReturnEmptyListWhenInputIsNull` | `buildMenuTree(menus)` | null 输入返回空列表 |
| 9 | `buildMenuTreeShouldNestChildrenUnderParent` | `buildMenuTree(menus)` | 子菜单正确嵌套在父菜单下 |
| 10 | `buildMenuTreeShouldPromoteOrphanedChildrenToRoot` | `buildMenuTree(menus)` | 父节点不在列表中的菜单提升为根节点 |
| 11 | `buildMenuTreeShouldHandleMultipleRootNodes` | `buildMenuTree(menus)` | 正确处理多个根节点 |
| 12 | `listMenusByRoleIdsShouldReturnEmptyWhenRoleIdsNull` | `listMenusByRoleIds(roleIds)` | roleIds 为 null 时返回空列表 |
| 13 | `listMenusByRoleIdsShouldReturnEmptyWhenRoleIdsEmpty` | `listMenusByRoleIds(roleIds)` | roleIds 为空列表时返回空列表 |

### 3.4 DictServiceImplTest

被测类：`DictServiceImpl` | Mock 依赖：`DictTypeMapper`、`DictItemMapper`

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `createDictTypeShouldSetDefaultStatusWhenBlank` | `createDictType(dictType)` | 状态为空时默认设为 `STATUS_NORMAL` |
| 2 | `createDictTypeShouldPreserveExplicitStatus` | `createDictType(dictType)` | 显式设置的状态被保留 |
| 3 | `updateDictTypeShouldThrowWhenTypeNotFound` | `updateDictType(id, dictType)` | 字典类型不存在时抛出 `NOT_FOUND` |
| 4 | `updateDictTypeShouldApplyNameChange` | `updateDictType(id, dictType)` | 更新字典类型时应用 dictName 变更 |
| 5 | `updateDictTypeShouldNotOverwriteWithNullFields` | `updateDictType(id, dictType)` | 更新请求中 null 字段不覆盖已有值 |
| 6 | `deleteDictTypeShouldCallDeleteById` | `deleteDictType(id)` | 委托 `dictTypeMapper.deleteById` 执行删除 |
| 7 | `createDictItemShouldSetDefaultStatusWhenBlank` | `createDictItem(item)` | 字典项状态为空时默认设为 `STATUS_NORMAL` |
| 8 | `updateDictItemShouldThrowWhenItemNotFound` | `updateDictItem(id, item)` | 字典项不存在时抛出 `NOT_FOUND` |
| 9 | `updateDictItemShouldApplyLabelAndValueChanges` | `updateDictItem(id, item)` | 更新字典项时应用 itemLabel 和 itemValue 变更 |
| 10 | `deleteDictItemShouldCallDeleteById` | `deleteDictItem(id)` | 委托 `dictItemMapper.deleteById` 执行删除 |
| 11 | `listDictItemsShouldQueryWithDictType` | `listDictItems(dictType, withDisabled)` | 按字典类型查询字典项列表 |

### 3.5 ConfigServiceImplTest

被测类：`ConfigServiceImpl` | 特殊模式：spy（无 Mock 依赖）

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `updateConfigShouldThrowWhenConfigNotFound` | `updateConfig(id, config)` | 配置不存在时抛出 `NOT_FOUND` |
| 2 | `updateConfigShouldApplyValueChange` | `updateConfig(id, config)` | 更新配置时应用 configValue 变更 |
| 3 | `updateConfigShouldApplyKeyChange` | `updateConfig(id, config)` | 更新配置时应用 configKey 变更 |
| 4 | `updateConfigShouldNotOverwriteWithNullFields` | `updateConfig(id, config)` | 更新请求中 null 字段不覆盖已有值 |
| 5 | `getConfigValueShouldReturnStoredValueWhenKeyExists` | `getConfigValue(key, default)` | key 存在时返回存储值 |
| 6 | `getConfigValueShouldReturnDefaultWhenKeyNotFound` | `getConfigValue(key, default)` | key 不存在时返回默认值 |
| 7 | `getConfigValueShouldReturnDefaultWhenValueIsBlank` | `getConfigValue(key, default)` | 存储值为空白时返回默认值 |
| 8 | `deleteConfigShouldCallRemoveById` | `deleteConfig(id)` | 委托 `removeById` 执行删除 |

---

## 4. om-hr — 人事管理模块

### 4.1 EmployeeServiceImplTest

被测类：`EmployeeServiceImpl` | 特殊模式：spy（无 Mock 依赖）

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `createShouldThrowWhenEmployeeIsNull` | `createEmployee(emp)` | 员工对象为 null 时抛出 `PARAM_ERROR` |
| 2 | `createShouldThrowWhenEmpNoIsNull` | `createEmployee(emp)` | empNo 为 null 时抛出 `PARAM_ERROR` |
| 3 | `createShouldThrowWhenEmpNoIsBlank` | `createEmployee(emp)` | empNo 为空白时抛出 `PARAM_ERROR` |
| 4 | `createShouldThrowWhenEmpNameIsNull` | `createEmployee(emp)` | empName 为 null 时抛出 `PARAM_ERROR` |
| 5 | `createShouldThrowWhenDeptIdIsNull` | `createEmployee(emp)` | deptId 为 null 时抛出 `PARAM_ERROR` |
| 6 | `createShouldThrowWhenHireDateIsNull` | `createEmployee(emp)` | hireDate 为 null 时抛出 `PARAM_ERROR` |
| 7 | `createShouldThrowWhenEmpNoAlreadyExists` | `createEmployee(emp)` | 工号已存在时抛出 `PARAM_ERROR`，不保存 |
| 8 | `createShouldSaveEmployeeWithNullId` | `createEmployee(emp)` | 插入前清空 id（防止覆写） |
| 9 | `updateShouldThrowWhenEmployeeIsNull` | `updateEmployee(id, emp)` | 更新对象为 null 时抛出 `PARAM_ERROR` |
| 10 | `updateShouldThrowWhenEmployeeNotFound` | `updateEmployee(id, emp)` | 员工不存在时抛出 `NOT_FOUND` |
| 11 | `updateShouldThrowWhenNewEmpNoAlreadyExistsOnAnotherEmployee` | `updateEmployee(id, emp)` | 新工号已被其他员工占用时抛出 `PARAM_ERROR` |
| 12 | `updateShouldAllowKeepingSameEmpNo` | `updateEmployee(id, emp)` | 保持原工号不变时不触发唯一性校验 |
| 13 | `updateShouldSetIdBeforeUpdate` | `updateEmployee(id, emp)` | 更新前将 id 设置到更新对象上 |
| 14 | `changeStatusShouldThrowWhenEmployeeNotFound` | `changeEmpStatus(id, status)` | 员工不存在时抛出 `NOT_FOUND` |
| 15 | `changeStatusShouldThrowWhenStatusIsBlank` | `changeEmpStatus(id, status)` | 状态为空白时抛出 `PARAM_ERROR` |
| 16 | `changeStatusShouldUpdateEmpStatusField` | `changeEmpStatus(id, status)` | 更新员工状态字段并持久化 |
| 17 | `deleteShouldThrowWhenEmployeeNotFound` | `deleteEmployee(id)` | 员工不存在时抛出 `NOT_FOUND`，不执行删除 |
| 18 | `deleteShouldCallRemoveByIdWhenEmployeeExists` | `deleteEmployee(id)` | 员工存在时调用 removeById 删除 |

---

## 5. om-asset — 资产管理模块

### 5.1 AssetServiceImplTest

被测类：`AssetServiceImpl` | 特殊模式：spy（无 Mock 依赖）

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `createShouldThrowWhenAssetIsNull` | `createAsset(asset)` | 资产对象为 null 时抛出 `PARAM_ERROR` |
| 2 | `createShouldThrowWhenAssetCodeIsNull` | `createAsset(asset)` | assetCode 为 null 时抛出 `PARAM_ERROR` |
| 3 | `createShouldThrowWhenAssetCodeIsBlank` | `createAsset(asset)` | assetCode 为空白时抛出 `PARAM_ERROR` |
| 4 | `createShouldThrowWhenAssetNameIsNull` | `createAsset(asset)` | assetName 为 null 时抛出 `PARAM_ERROR` |
| 5 | `createShouldThrowWhenCategoryIsNull` | `createAsset(asset)` | category 为 null 时抛出 `PARAM_ERROR` |
| 6 | `createShouldThrowWhenAssetCodeAlreadyExists` | `createAsset(asset)` | 资产编码已存在时抛出 `PARAM_ERROR`，不保存 |
| 7 | `createShouldThrowOnDuplicateKeyExceptionFromDatabase` | `createAsset(asset)` | 数据库 `DuplicateKeyException` 转换为 `BusinessException(PARAM_ERROR)`（并发安全兜底） |
| 8 | `createShouldSaveAssetWithNullId` | `createAsset(asset)` | 插入前清空 id（防止覆写） |
| 9 | `updateShouldThrowWhenAssetIsNull` | `updateAsset(id, asset)` | 更新对象为 null 时抛出 `PARAM_ERROR` |
| 10 | `updateShouldThrowWhenAssetNotFound` | `updateAsset(id, asset)` | 资产不存在时抛出 `NOT_FOUND` |
| 11 | `updateShouldThrowWhenNewAssetCodeAlreadyExistsOnAnotherAsset` | `updateAsset(id, asset)` | 新编码已被其他资产占用时抛出 `PARAM_ERROR` |
| 12 | `updateShouldAllowKeepingSameAssetCode` | `updateAsset(id, asset)` | 保持原编码不变时不触发唯一性校验 |
| 13 | `updateShouldSetIdBeforeUpdate` | `updateAsset(id, asset)` | 更新前将 id 设置到更新对象上 |
| 14 | `updateShouldThrowOnDuplicateKeyExceptionFromDatabase` | `updateAsset(id, asset)` | 更新时 `DuplicateKeyException` 转换为 `BusinessException` |
| 15 | `updateStatusShouldThrowWhenAssetNotFound` | `updateAssetStatus(id, status)` | 资产不存在时抛出 `NOT_FOUND` |
| 16 | `updateStatusShouldThrowWhenStatusIsBlank` | `updateAssetStatus(id, status)` | 状态为空白时抛出 `PARAM_ERROR` |
| 17 | `updateStatusShouldSetStatusAndUpdate` | `updateAssetStatus(id, status)` | 更新资产状态字段并持久化 |
| 18 | `deleteShouldThrowWhenAssetNotFound` | `deleteAsset(id)` | 资产不存在时抛出 `NOT_FOUND`，不执行删除 |
| 19 | `deleteShouldCallRemoveByIdWhenAssetExists` | `deleteAsset(id)` | 资产存在时调用 removeById 删除 |

---

## 6. om-org — 组织架构模块

### 6.1 DeptServiceImplTest

被测类：`DeptServiceImpl` | 特殊模式：spy（无 Mock 依赖）

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `updateDeptShouldThrowWhenDepartmentDoesNotExist` | `updateDept(id, dept)` | 部门不存在时抛出 `DEPT_NOT_FOUND`，不执行更新 |
| 2 | `updateDeptShouldAllowIdempotentUpdates` | `updateDept(id, dept)` | 设置 id 后委托 `updateById` 执行幂等更新 |
| 3 | `deleteDeptShouldThrowWhenDepartmentHasChildren` | `deleteDept(id)` | 部门有子部门时抛出 `PARAM_ERROR`，不执行删除 |
| 4 | `deleteDeptShouldRemoveLeafDepartment` | `deleteDept(id)` | 叶子部门（无子部门）可正常删除 |

### 6.2 PositionServiceImplTest

被测类：`PositionServiceImpl` | 特殊模式：spy（无 Mock 依赖）

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `updatePositionShouldThrowWhenPositionDoesNotExist` | `updatePosition(id, position)` | 岗位不存在时抛出 `NOT_FOUND`，不执行更新 |
| 2 | `updatePositionShouldAllowIdempotentUpdates` | `updatePosition(id, position)` | 设置 id 后委托 `updateById` 执行幂等更新 |

---

## 7. om-message — 消息中心模块

### 7.1 MessageServiceImplTest

被测类：`MessageServiceImpl` | 特殊模式：spy + `UserContext`（ThreadLocal）

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `sendShouldAllowSystemGeneratedMessages` | `send(receiverId, title, content, msgType, bizType, bizId)` | 无用户上下文时 senderId 为 null，消息正确保存 |
| 2 | `sendShouldUseCurrentUserAsSenderWhenPresent` | `send(receiverId, title, content, msgType)` | 用户上下文存在时使用当前用户 id 作为 senderId |
| 3 | `sendShouldRejectPartialBusinessLinkage` | `send(receiverId, title, content, msgType, bizType, bizId)` | bizType 非空但 bizId 为 null 时拒绝（业务关联不完整），不保存 |

### 7.2 TodoGenerateServiceImplTest

被测类：`TodoGenerateServiceImpl` | Mock 依赖：`MessageService`、`MessageMapper`

| # | 测试方法 | 被测方法 | 场景描述 |
|---|---------|---------|---------|
| 1 | `generateTodoShouldDelegateToMessageServiceWithTodoType` | `generateTodo(receiverId, title, content, bizType, bizId)` | 委托 `messageService.send`，msgType 为 TODO |
| 2 | `completeTodosShouldReturnZeroForEmptyReceivers` | `completeTodos(receiverIds, bizType, bizId)` | 接收人列表为空时返回 0，不调用 messageService 或 messageMapper |
| 3 | `completeTodoShouldReturnUpdatedCount` | `completeTodo(receiverId, bizType, bizId)` | 返回 `messageMapper.update` 更新的行数 |
| 4 | `generateTodoShouldRejectMissingBusinessLink` | `generateTodo(receiverId, title, content, bizType, bizId)` | bizType 为空时拒绝，不发送消息 |
| 5 | `generateTodoShouldRejectNullBusinessId` | `generateTodo(receiverId, title, content, bizType, bizId)` | bizId 为 null 时拒绝，不发送消息 |

---

## 附录：测试统计

| 模块 | 测试类数 | 测试用例数 |
|------|---------|-----------|
| om-auth | 3 | 25 |
| om-oa | 3 | 36 |
| om-system | 5 | 56 |
| om-hr | 1 | 18 |
| om-asset | 1 | 19 |
| om-org | 2 | 6 |
| om-message | 2 | 8 |
| **合计** | **17** | **168** |
