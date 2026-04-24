-- ============================================================
-- V1.0.7 初始化菜单和岗位种子数据
-- ============================================================

-- ----------------------------
-- 1. 菜单数据
-- ----------------------------

-- 工作台目录
INSERT IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, permission_code, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES (1, 0, '工作台', 'DIR', '/dashboard', NULL, 'Odometer', NULL, 1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

INSERT IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, permission_code, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    (101, 1, '首页总览',   'MENU', '/dashboard',       'views/dashboard/DashboardView.vue',              'Odometer',       'dashboard:view',       1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (102, 1, '消息中心',   'MENU', '/messages',         'views/message/MessageCenterView.vue',            'Bell',           'message:view',         2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (103, 1, '我的待办',   'MENU', '/workflow/todo',    'views/workflow/todo/TodoListView.vue',           'Connection',     'workflow:todo:view',    3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (104, 1, '流程管理',   'MENU', '/workflow/manage',  'views/workflow/manage/WorkflowManageView.vue',   'List',           'workflow:manage:view',  4, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- 系统管理目录
INSERT IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, permission_code, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES (2, 0, '系统管理', 'DIR', '/system', NULL, 'Setting', NULL, 2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

INSERT IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, permission_code, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    (201, 2, '用户管理', 'MENU', '/system/users',        'views/system/user/UserListView.vue',             'User',            'system:user:query',    1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (202, 2, '角色管理', 'MENU', '/system/roles',        'views/system/role/RoleListView.vue',             'Setting',         'system:role:query',    2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (203, 2, '菜单管理', 'MENU', '/system/menus',        'views/system/menu/MenuListView.vue',             'Document',        'system:menu:query',    3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (204, 2, '字典管理', 'MENU', '/system/dicts',        'views/system/dict/DictListView.vue',             'List',            'system:dict:query',    4, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (205, 2, '参数配置', 'MENU', '/system/configs',      'views/system/config/ConfigListView.vue',         'Setting',         'system:config:query',  5, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (206, 2, '部门管理', 'MENU', '/org/dept',            'views/org/dept/DeptTreeView.vue',                'OfficeBuilding',  'org:dept:query',       6, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (207, 2, '岗位管理', 'MENU', '/org/positions',       'views/org/position/PositionListView.vue',        'User',            'org:position:query',   7, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (208, 2, '登录日志', 'MENU', '/audit/login-logs',    'views/audit/login/LoginLogListView.vue',         'DocumentChecked', 'audit:login:query',    8, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (209, 2, '操作日志', 'MENU', '/audit/operate-logs',  'views/audit/operate/OperateLogListView.vue',     'DocumentChecked', 'audit:operate:query',  9, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (210, 2, '文件中心', 'MENU', '/files',               'views/file/FileCenterView.vue',                  'FolderOpened',    'file:view',           10, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- 业务管理目录
INSERT IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, permission_code, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES (3, 0, '业务管理', 'DIR', '/business', NULL, 'Box', NULL, 3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

INSERT IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, permission_code, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    (301, 3, '员工档案', 'MENU', '/hr/employees', 'views/hr/employee/EmployeeListView.vue', 'User',     'hr:employee:query', 1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (302, 3, '请假申请', 'MENU', '/oa/leave',      'views/oa/leave/LeaveListView.vue',       'Document', 'oa:leave:query',    2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (303, 3, '资产台账', 'MENU', '/asset',          'views/asset/AssetListView.vue',          'Box',      'asset:query',       3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- 为超级管理员角色（id=1）分配所有菜单权限
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE deleted_flag = 0;

-- ----------------------------
-- 2. 岗位种子数据（关联到集团总部，dept_id=1）
-- ----------------------------
INSERT IGNORE INTO sys_position (id, position_code, position_name, dept_id, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    (1, 'CEO',    '首席执行官', 1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (2, 'CTO',    '首席技术官', 1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (3, 'CFO',    '首席财务官', 1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (4, 'HR_MGR', '人力资源经理', 1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (5, 'DEV',    '研发工程师', 1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    (6, 'OPS',    '运维工程师', 1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);
