# Open Management — 开发进度追踪

> 最后更新：2026-04-20  
> 当前阶段：**第一期 · 平台底座开发**（路线图 Sprint 1–7）

---

## 图例

| 标记 | 含义 |
|------|------|
| ✅ | 已完成，代码已提交 |
| 🔨 | 骨架/接口已建立，Service 实现仍为 TODO 占位 |
| ⬜ | 尚未开始 |

---

## 第一期 · 平台底座（Sprint 1–7）

### Sprint 1：工程基础设施

| 层次 | 交付项 | 状态 |
|------|--------|------|
| 后端 `om-common` | `ErrorCode` 枚举、`CommonConstants`、`UserContext`、`IpUtils`、`@OperateLog`、`@DataPermission` 注解 | ✅ |
| 后端 `om-common` | `R<T>` 统一响应、`PageResult<T>`、`PageQuery`、`BaseEntity`、`BusinessException`、`GlobalExceptionHandler`（含 Sa-Token 异常） | ✅ |
| 后端 `om-app` | Spring Boot 主配置（数据源、Redis、MinIO、RabbitMQ、Sa-Token、Flyway） | ✅ |
| 后端 `om-app` | `MybatisPlusConfig`（分页插件 + MetaObjectHandler 自动填充） | ✅ |
| 后端 `om-app` | `SaTokenConfig`（路由鉴权拦截器 + UserContext HandlerInterceptor） | ✅ |
| 后端 `om-app` | `MinioConfig`（MinioClient Bean） | ✅ |
| 后端 `om-app` | `CorsConfig`（跨域过滤器） | ✅ |
| 后端 `om-app` | `application.yml` 分环境配置（dev / prod） | ✅ |
| 数据库 | `V1.0.0__platform_ddl.sql` — sys_user、sys_role、sys_menu、sys_dept、sys_position、sys_dict_type、sys_dict_item、sys_config、sys_file、sys_message、sys_login_log、sys_operate_log | ✅ |
| 数据库 | `V1.0.5__init_data.sql` — 初始字典、角色、菜单、超管账号 | ✅ |
| 前端 | `request.ts` Axios 拦截器、Token 携带、统一错误处理骨架 | ✅ |
| 前端 | 路由守卫（登录态检查、权限拦截）骨架 | ✅ |
| 前端 | 全局 Layout（侧边菜单、顶部导航）骨架 | ✅ |
| 前端 | Pinia user / permission store 骨架 | ✅ |

---

### Sprint 2：认证授权模块

| 层次 | 交付项 | 状态 |
|------|--------|------|
| 后端 `om-auth` | `CaptchaService` — AWT 数学验证码生成，答案存 Redis（TTL 5 分钟） | ✅ |
| 后端 `om-auth` | `PasswordPolicyService` — BCrypt 编码/校验、密码强度检查 | ✅ |
| 后端 `om-auth` | `LoginServiceImpl` — 验证码校验 → 失败计数锁定（5 次/30 分钟）→ 用户查询 → 密码校验 → Sa-Token 登录 → 返回 Token + UserInfo + 菜单树 | ✅ |
| 后端 `om-auth` | `AuthController` — `/login`、`/logout`、`/captcha`、`/refresh-token` | ✅ |
| 后端 `om-system` | 菜单权限树查询（基于角色动态返回）—— 供登录响应组装 | ✅ |
| 后端 `om-system` | 按钮权限标识（permission code 列表）—— 供登录响应组装 | ✅ |
| 数据权限 | `@DataPermission` 注解定义（AOP 切面实现待 Sprint 3） | ✅（注解）/ ⬜（切面） |
| 前端 | `/login` 页（账号、密码、验证码表单、校验）骨架 | ✅ |
| 前端 | 登录后权限菜单动态加载（Pinia permission store）骨架 | ✅ |

---

### Sprint 3：系统管理模块

| 层次 | 交付项 | 状态 |
|------|--------|------|
| 后端 `om-system` | `UserService/Impl` — 分页查询（用户名/姓名/部门/状态过滤）、创建、更新、重置密码、启停、删除 | ✅ |
| 后端 `om-system` | `RoleService/Impl` — 分页查询、CRUD、菜单权限分配（`sys_role_menu`）、按用户查角色 | ✅ |
| 后端 `om-system` | `MenuService/Impl` — 全量查询、树形构建、CRUD、按角色列表查菜单 | ✅ |
| 后端 `om-system` | `DictService/Impl` — 字典类型分页、字典项 CRUD、按类型查项 | ✅ |
| 后端 `om-system` | `ConfigService/Impl` — 系统参数分页、CRUD、`getConfigValue(key, default)` | ✅ |
| 后端 `om-system` | 实体：`SysRoleUser`、`SysRoleMenu`、`SysConfig` | ✅ |
| 后端 `om-system` | Mapper：`RoleUserMapper`、`RoleMenuMapper`、`DictTypeMapper`、`DictItemMapper`、`ConfigMapper` | ✅ |
| 前端 | 用户管理页骨架（搜索、分页列表、新增/编辑弹窗） | ✅ |
| 前端 | 角色管理页骨架 | ✅ |
| 前端 `views/system` | 菜单管理页、字典管理页、参数配置页 | ⬜ |

---

### Sprint 4：组织架构模块

| 层次 | 交付项 | 状态 |
|------|--------|------|
| 后端 `om-org` | 实体 `SysDept`、`SysPosition`；Mapper `DeptMapper`、`PositionMapper` | ✅ |
| 后端 `om-org` | `DeptService/Impl` — 部门树查询、CRUD | ✅ |
| 后端 `om-org` | `PositionService/Impl` — 岗位分页查询、CRUD | ✅ |
| 后端 `om-org` | `DeptController` — `/api/org/depts/tree` 等接口 | ✅（Controller 骨架） |
| 后端 `om-org` | `PositionController` — `/api/org/positions` 等接口 | ✅（Controller 骨架） |
| 前端 | 部门树管理页骨架 | ✅ |
| 前端 | 岗位管理页 | ⬜ |

---

### Sprint 5：日志审计模块

| 层次 | 交付项 | 状态 |
|------|--------|------|
| 数据库 | `sys_login_log`、`sys_operate_log` 表（含于 V1.0.0） | ✅ |
| 后端 `om-audit` | 实体 `SysLoginLog`、`SysOperateLog`；Mapper | ✅ |
| 后端 `om-audit` | `LoginLogService/Impl` — 保存登录日志、分页查询 | ✅ |
| 后端 `om-audit` | `OperateLogService/Impl` — 保存操作日志、分页查询 | ✅ |
| 后端 `om-audit` | `@OperateLog` AOP 切面（`OperateLogAspect`） | ✅ |
| 后端 `om-audit` | `AuditQueryController` — 登录日志列表、操作日志列表接口 | ✅（Controller 骨架） |
| 前端 | 登录日志列表页、操作日志列表页 | ⬜ |

---

### Sprint 6：文件中心 & 消息中心

| 层次 | 交付项 | 状态 |
|------|--------|------|
| 数据库 | `sys_file`、`sys_message` 表（含于 V1.0.0） | ✅ |
| 后端 `om-file` | 实体 `SysFile`；`FileMapper` | ✅ |
| 后端 `om-file` | `FileStorageService/Impl` — MinIO 上传/下载/预签名 URL、DB 记录 | 🔨 |
| 后端 `om-file` | `FileController` — `/api/files/upload`、`/api/files/{id}/url`、`DELETE /api/files/{id}` | ✅（Controller 骨架） |
| 后端 `om-message` | 实体 `SysMessage`；`MessageMapper` | ✅ |
| 后端 `om-message` | `MessageService/Impl` — 发送、已读/未读、分页查询 | 🔨 |
| 后端 `om-message` | `TodoGenerateService` — 待办生成 & 完成 | ⬜ |
| 后端 `om-message` | `MessageController` — 消息列表、标记已读、未读数量 | ✅（Controller 骨架） |
| 前端 | 顶部消息中心入口、首页工作台统计图 | ⬜ |

---

### Sprint 7：工作流底座

| 层次 | 交付项 | 状态 |
|------|--------|------|
| 数据库 | `V1.0.1__workflow_ddl.sql` — `wf_process_definition`、`wf_process_instance`、`wf_task` | ✅ |
| 后端 `om-workflow` | 实体 `WfProcessInstance`、`WfTask`；Mapper | ✅ |
| 后端 `om-workflow` | `ProcessInstanceService/Impl` — Flowable 发起流程、查询实例 | 🔨 |
| 后端 `om-workflow` | `TaskService/Impl` — 待办查询、审批/退回/撤回/转办 | 🔨 |
| 后端 `om-workflow` | Flowable 集成配置 | ⬜ |
| 后端 `om-workflow` | `WorkflowController` — 启动流程、完成任务接口骨架 | ✅（Controller 骨架） |
| 前端 | 我的待办页、流程详情页、流程管理页 | ⬜ |

---

## 第二期 · 业务模块（Sprint 8–10）

### Sprint 8：人事管理模块

| 层次 | 交付项 | 状态 |
|------|--------|------|
| 数据库 | `V1.0.2__hr_ddl.sql` — `hr_employee`、`hr_employee_change`、`hr_employee_salary`、`hr_attendance` | ✅ |
| 后端 `om-hr` | 实体 `HrEmployee`；`EmployeeMapper` | ✅ |
| 后端 `om-hr` | `EmployeeService/Impl` — 档案分页查询、新增、状态管理 | 🔨 |
| 后端 `om-hr` | `EmployeeController` — `/api/hr/employees` 骨架 | ✅（Controller 骨架） |
| 后端 `om-hr` | `EmployeeChangeService`（入转调离记录） | ⬜ |
| 前端 | 员工档案列表页骨架 | ✅ |
| 前端 | 员工档案详情/编辑页、入转调离记录页 | ⬜ |

---

### Sprint 9：OA 审批模块

| 层次 | 交付项 | 状态 |
|------|--------|------|
| 数据库 | `V1.0.3__oa_ddl.sql` — `oa_leave_apply`、`oa_travel_apply`、`oa_expense_apply`、`oa_approval_task` | ✅ |
| 后端 `om-oa` | 实体 `OaLeaveApply`、`OaTravelApply`、`OaExpenseApply`；Mapper | ✅ |
| 后端 `om-oa` | `LeaveApplyService/Impl` — 申请 CRUD、工作流集成 | 🔨 |
| 后端 `om-oa` | `TravelApplyService/Impl` — 申请 CRUD、工作流集成 | 🔨 |
| 后端 `om-oa` | `ExpenseApplyService/Impl` — 申请 CRUD、工作流集成 | 🔨 |
| 后端 `om-oa` | Controller 骨架（Leave / Travel / Expense） | ✅（Controller 骨架） |
| 前端 | 请假/出差/报销申请页骨架 | ✅（请假） |
| 前端 | 出差申请页、报销申请页、待办审批处理页 | ⬜ |

---

### Sprint 10：资产管理模块

| 层次 | 交付项 | 状态 |
|------|--------|------|
| 数据库 | `V1.0.4__asset_ddl.sql` — `asset_info`、`asset_receive_apply`、`asset_transfer_record` | ✅ |
| 后端 `om-asset` | 实体 `AssetInfo`、`AssetReceiveApply`；Mapper | ✅ |
| 后端 `om-asset` | `AssetService/Impl` — 台账分页查询、CRUD、状态管理 | 🔨 |
| 后端 `om-asset` | `AssetReceiveService/Impl` — 领用申请、审批、状态回写 | 🔨 |
| 后端 `om-asset` | Controller 骨架（Asset / AssetReceive） | ✅（Controller 骨架） |
| 前端 | 资产台账列表页骨架 | ✅ |
| 前端 | 领用/归还/维修/报废申请页 | ⬜ |

---

## 第三期 · 联调测试与上线（Sprint 11–14）

| 阶段 | 状态 |
|------|------|
| Sprint 11：功能测试与缺陷修复 | ⬜ |
| Sprint 12：性能 & 安全测试 | ⬜ |
| Sprint 13：桌面端打包 & Docker 部署 | ⬜ |
| Sprint 14：验收上线 | ⬜ |

---

## 近期待办（Next Actions）

优先级按交付风险排序：

1. **数据权限** — 实现 `@DataPermission` AOP 切面（SELF / DEPT / DEPT_AND_CHILD / ALL 四种策略）
2. **`om-file`** — 实现 `FileStorageServiceImpl`（MinIO 上传/删除/预签名 URL）
3. **`om-message`** — 实现 `MessageServiceImpl` 全部方法，新建 `TodoGenerateService`
4. **`om-workflow`** — 完成 Flowable 配置，实现 `ProcessInstanceServiceImpl` 和 `TaskServiceImpl`
5. **`om-hr` / `om-oa` / `om-asset`** — 补全各 ServiceImpl 的数据库操作
6. **前端** — 补全菜单管理页、字典管理页、岗位管理页；完善消息中心、待办页；接入真实后端接口
7. **测试完善** — 为已完成的组织架构与日志审计服务补充单元测试和联调用例

---

## 统计快照（2026-04-20）

| 模块 | 状态 | 完成度 |
|------|------|--------|
| om-common | ✅ 已完成 | 100% |
| om-app | ✅ 已完成 | 100% |
| om-auth | ✅ 已完成 | 100% |
| om-system | ✅ 已完成 | 100% |
| om-org | ✅ 服务实现完成，前端岗位页待补全 | 70% |
| om-audit | ✅ 服务与 AOP 实现完成，前端页面待补全 | 70% |
| om-file | 🔨 骨架完成，MinIO 集成待实现 | 30% |
| om-message | 🔨 骨架完成，Service 待实现 | 30% |
| om-workflow | 🔨 骨架完成，Flowable 集成待实现 | 20% |
| om-hr | 🔨 骨架完成，Service 待实现 | 30% |
| om-oa | 🔨 骨架完成，Service 待实现 | 25% |
| om-asset | 🔨 骨架完成，Service 待实现 | 25% |
| 前端 | 🔨 骨架完成，部分页面待补全 | 40% |
| 桌面端 | 🔨 骨架完成，打包配置待完善 | 20% |
| 数据库 DDL | ✅ 全部脚本已就位 | 100% |
