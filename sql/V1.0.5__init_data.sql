-- ============================================================
-- V1.0.5 初始化基础数据
-- ============================================================

-- ----------------------------
-- 1. 字典类型
-- ----------------------------
INSERT IGNORE INTO sys_dict_type (dict_type, dict_name, status, remark, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    ('USER_STATUS',      '用户状态',     'ENABLED', NULL, 1, NOW(), 1, NOW(), 0, 0),
    ('DEPT_TYPE',        '部门类型',     'ENABLED', NULL, 1, NOW(), 1, NOW(), 0, 0),
    ('ROLE_DATA_SCOPE',  '数据权限范围', 'ENABLED', NULL, 1, NOW(), 1, NOW(), 0, 0),
    ('LEAVE_TYPE',       '请假类型',     'ENABLED', NULL, 1, NOW(), 1, NOW(), 0, 0),
    ('EMP_STATUS',       '员工状态',     'ENABLED', NULL, 1, NOW(), 1, NOW(), 0, 0),
    ('ASSET_STATUS',     '资产状态',     'ENABLED', NULL, 1, NOW(), 1, NOW(), 0, 0),
    ('MSG_TYPE',         '消息类型',     'ENABLED', NULL, 1, NOW(), 1, NOW(), 0, 0),
    ('WORKFLOW_STATUS',  '流程状态',     'ENABLED', NULL, 1, NOW(), 1, NOW(), 0, 0),
    ('APPLY_STATUS',     '申请单状态',   'ENABLED', NULL, 1, NOW(), 1, NOW(), 0, 0),
    ('GENDER',           '性别',         'ENABLED', NULL, 1, NOW(), 1, NOW(), 0, 0);

-- ----------------------------
-- 2. 字典项
-- ----------------------------

-- USER_STATUS 用户状态
INSERT IGNORE INTO sys_dict_item (dict_type, item_label, item_value, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    ('USER_STATUS', '启用', 'ENABLED',  1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('USER_STATUS', '禁用', 'DISABLED', 2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('USER_STATUS', '锁定', 'LOCKED',   3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- DEPT_TYPE 部门类型
INSERT IGNORE INTO sys_dict_item (dict_type, item_label, item_value, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    ('DEPT_TYPE', '公司', 'COMPANY', 1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('DEPT_TYPE', '部门', 'DEPT',    2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('DEPT_TYPE', '小组', 'GROUP',   3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- ROLE_DATA_SCOPE 数据权限范围
INSERT IGNORE INTO sys_dict_item (dict_type, item_label, item_value, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    ('ROLE_DATA_SCOPE', '全部数据', 'ALL',  1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('ROLE_DATA_SCOPE', '本部门',   'DEPT', 2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('ROLE_DATA_SCOPE', '仅本人',   'SELF', 3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- LEAVE_TYPE 请假类型
INSERT IGNORE INTO sys_dict_item (dict_type, item_label, item_value, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    ('LEAVE_TYPE', '年假', 'ANNUAL',   1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('LEAVE_TYPE', '事假', 'PERSONAL', 2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('LEAVE_TYPE', '病假', 'SICK',     3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- EMP_STATUS 员工状态
INSERT IGNORE INTO sys_dict_item (dict_type, item_label, item_value, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    ('EMP_STATUS', '在职', 'ACTIVE',    1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('EMP_STATUS', '离职', 'RESIGNED',  2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('EMP_STATUS', '停职', 'SUSPENDED', 3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- ASSET_STATUS 资产状态
INSERT IGNORE INTO sys_dict_item (dict_type, item_label, item_value, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    ('ASSET_STATUS', '闲置',   'IDLE',     1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('ASSET_STATUS', '使用中', 'IN_USE',   2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('ASSET_STATUS', '维修中', 'REPAIR',   3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('ASSET_STATUS', '已报废', 'SCRAPPED', 4, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- MSG_TYPE 消息类型
INSERT IGNORE INTO sys_dict_item (dict_type, item_label, item_value, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    ('MSG_TYPE', '待办', 'TODO',   1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('MSG_TYPE', '通知', 'NOTICE', 2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('MSG_TYPE', '结果', 'RESULT', 3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- WORKFLOW_STATUS 流程状态
INSERT IGNORE INTO sys_dict_item (dict_type, item_label, item_value, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    ('WORKFLOW_STATUS', '进行中', 'RUNNING',    1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('WORKFLOW_STATUS', '已完成', 'COMPLETED',  2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('WORKFLOW_STATUS', '已终止', 'TERMINATED', 3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- APPLY_STATUS 申请单状态
INSERT IGNORE INTO sys_dict_item (dict_type, item_label, item_value, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    ('APPLY_STATUS', '草稿',   'DRAFT',     1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('APPLY_STATUS', '已提交', 'SUBMITTED', 2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('APPLY_STATUS', '审批中', 'APPROVING', 3, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('APPLY_STATUS', '已通过', 'APPROVED',  4, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('APPLY_STATUS', '已拒绝', 'REJECTED',  5, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('APPLY_STATUS', '已撤销', 'CANCELLED', 6, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- GENDER 性别
INSERT IGNORE INTO sys_dict_item (dict_type, item_label, item_value, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    ('GENDER', '男', 'M', 1, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0),
    ('GENDER', '女', 'F', 2, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- ----------------------------
-- 3. 根部门
-- ----------------------------
INSERT IGNORE INTO sys_dept (id, dept_code, dept_name, parent_id, dept_type, leader_user_id, sort_order, status, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES (1, 'ROOT', '集团总部', 0, 'COMPANY', NULL, 0, 'ENABLED', 1, NOW(), 1, NOW(), 0, 0);

-- ----------------------------
-- 4. 内置角色
-- ----------------------------
INSERT IGNORE INTO sys_role (id, role_code, role_name, data_scope, status, remark, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES
    (1, 'SUPER_ADMIN', '超级管理员', 'ALL',  'ENABLED', '系统内置超级管理员，拥有全部权限', 1, NOW(), 1, NOW(), 0, 0),
    (2, 'ADMIN',       '管理员',     'DEPT', 'ENABLED', '系统内置管理员',                   1, NOW(), 1, NOW(), 0, 0);

-- ----------------------------
-- 5. 初始管理员用户
--    password_hash = bcrypt('Admin@123')，首次登录后请立即修改密码
-- ----------------------------
INSERT IGNORE INTO sys_user (id, username, password_hash, real_name, mobile, email, dept_id, position_id, status, last_login_time, created_by, created_at, updated_by, updated_at, deleted_flag, version_no)
VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAtu', '系统管理员', NULL, NULL, 1, NULL, 'ENABLED', NULL, 1, NOW(), 1, NOW(), 0, 0);

-- ----------------------------
-- 6. 用户-角色关联：admin -> SUPER_ADMIN
-- ----------------------------
INSERT IGNORE INTO sys_user_role (user_id, role_id)
VALUES (1, 1);
