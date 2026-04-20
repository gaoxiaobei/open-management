-- ============================================================
-- V1.0.0 平台底座表结构 DDL
-- ============================================================

-- ----------------------------
-- 1. 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_user (
    id                bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username          varchar(50)  NOT NULL                COMMENT '登录账号',
    password_hash     varchar(255) NOT NULL                COMMENT '密码哈希',
    real_name         varchar(50)                          COMMENT '姓名',
    mobile            varchar(20)                          COMMENT '手机号',
    email             varchar(100)                         COMMENT '邮箱',
    dept_id           bigint                               COMMENT '部门ID',
    position_id       bigint                               COMMENT '岗位ID',
    status            varchar(20)  NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
    last_login_time   datetime                             COMMENT '最近登录时间',
    created_by        bigint                               COMMENT '创建人',
    created_at        datetime                             COMMENT '创建时间',
    updated_by        bigint                               COMMENT '更新人',
    updated_at        datetime                             COMMENT '更新时间',
    deleted_flag      tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no        int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_username (username),
    KEY idx_sys_user_dept_id (dept_id),
    KEY idx_sys_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 部门表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_dept (
    id               bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    dept_code        varchar(50)  NOT NULL                COMMENT '部门编码',
    dept_name        varchar(100) NOT NULL                COMMENT '部门名称',
    parent_id        bigint       NOT NULL DEFAULT 0      COMMENT '上级部门ID',
    dept_type        varchar(20)                          COMMENT '部门类型',
    leader_user_id   bigint                               COMMENT '负责人ID',
    sort_order       int          NOT NULL DEFAULT 0      COMMENT '排序',
    status           varchar(20)  NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
    created_by       bigint                               COMMENT '创建人',
    created_at       datetime                             COMMENT '创建时间',
    updated_by       bigint                               COMMENT '更新人',
    updated_at       datetime                             COMMENT '更新时间',
    deleted_flag     tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no       int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_dept_code (dept_code),
    KEY idx_sys_dept_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 3. 岗位表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_position (
    id              bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    position_code   varchar(50)  NOT NULL                COMMENT '岗位编码',
    position_name   varchar(100) NOT NULL                COMMENT '岗位名称',
    dept_id         bigint                               COMMENT '部门ID',
    status          varchar(20)  NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
    created_by      bigint                               COMMENT '创建人',
    created_at      datetime                             COMMENT '创建时间',
    updated_by      bigint                               COMMENT '更新人',
    updated_at      datetime                             COMMENT '更新时间',
    deleted_flag    tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no      int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_position_code (position_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- ----------------------------
-- 4. 角色表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_role (
    id           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_code    varchar(50)  NOT NULL                COMMENT '角色编码',
    role_name    varchar(100) NOT NULL                COMMENT '角色名称',
    data_scope   varchar(20)  NOT NULL DEFAULT 'SELF' COMMENT '数据权限范围',
    status       varchar(20)  NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
    remark       varchar(500)                         COMMENT '备注',
    created_by   bigint                               COMMENT '创建人',
    created_at   datetime                             COMMENT '创建时间',
    updated_by   bigint                               COMMENT '更新人',
    updated_at   datetime                             COMMENT '更新时间',
    deleted_flag tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no   int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 5. 用户-角色关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_user_role (
    id      bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id bigint NOT NULL                COMMENT '用户ID',
    role_id bigint NOT NULL                COMMENT '角色ID',
    PRIMARY KEY (id),
    KEY idx_sys_user_role_user_id (user_id),
    KEY idx_sys_user_role_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 6. 菜单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_menu (
    id               bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    parent_id        bigint       NOT NULL DEFAULT 0      COMMENT '上级菜单ID',
    menu_name        varchar(100) NOT NULL                COMMENT '菜单名称',
    menu_type        varchar(20)  NOT NULL                COMMENT '菜单类型: 目录/菜单/按钮',
    path             varchar(200)                         COMMENT '路由路径',
    component        varchar(200)                         COMMENT '前端组件路径',
    icon             varchar(100)                         COMMENT '图标',
    permission_code  varchar(100)                         COMMENT '权限标识',
    sort_order       int          NOT NULL DEFAULT 0      COMMENT '排序',
    status           varchar(20)  NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
    created_by       bigint                               COMMENT '创建人',
    created_at       datetime                             COMMENT '创建时间',
    updated_by       bigint                               COMMENT '更新人',
    updated_at       datetime                             COMMENT '更新时间',
    deleted_flag     tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no       int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    KEY idx_sys_menu_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------
-- 7. 角色-菜单关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id      bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_id bigint NOT NULL                COMMENT '角色ID',
    menu_id bigint NOT NULL                COMMENT '菜单ID',
    PRIMARY KEY (id),
    KEY idx_sys_role_menu_role_id (role_id),
    KEY idx_sys_role_menu_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ----------------------------
-- 8. 字典类型表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    dict_type    varchar(100) NOT NULL                COMMENT '字典类型编码',
    dict_name    varchar(100) NOT NULL                COMMENT '字典类型名称',
    status       varchar(20)  NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
    remark       varchar(500)                         COMMENT '备注',
    created_by   bigint                               COMMENT '创建人',
    created_at   datetime                             COMMENT '创建时间',
    updated_by   bigint                               COMMENT '更新人',
    updated_at   datetime                             COMMENT '更新时间',
    deleted_flag tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no   int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ----------------------------
-- 9. 字典项表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_dict_item (
    id           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    dict_type    varchar(100) NOT NULL                COMMENT '字典类型编码',
    item_label   varchar(100) NOT NULL                COMMENT '字典项标签',
    item_value   varchar(100) NOT NULL                COMMENT '字典项值',
    sort_order   int          NOT NULL DEFAULT 0      COMMENT '排序',
    status       varchar(20)  NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
    created_by   bigint                               COMMENT '创建人',
    created_at   datetime                             COMMENT '创建时间',
    updated_by   bigint                               COMMENT '更新人',
    updated_at   datetime                             COMMENT '更新时间',
    deleted_flag tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no   int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    KEY idx_sys_dict_item_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典项表';

-- ----------------------------
-- 10. 系统配置表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_config (
    id           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    config_key   varchar(100) NOT NULL                COMMENT '配置键',
    config_value varchar(500) NOT NULL                COMMENT '配置值',
    config_name  varchar(100)                         COMMENT '配置名称',
    remark       varchar(500)                         COMMENT '备注',
    created_by   bigint                               COMMENT '创建人',
    created_at   datetime                             COMMENT '创建时间',
    updated_by   bigint                               COMMENT '更新人',
    updated_at   datetime                             COMMENT '更新时间',
    deleted_flag tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no   int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ----------------------------
-- 11. 文件表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_file (
    id            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    file_name     varchar(255) NOT NULL                COMMENT '存储文件名',
    original_name varchar(255) NOT NULL                COMMENT '原始文件名',
    file_path     varchar(500) NOT NULL                COMMENT '文件路径',
    file_size     bigint                               COMMENT '文件大小(字节)',
    mime_type     varchar(100)                         COMMENT 'MIME 类型',
    biz_type      varchar(50)                          COMMENT '业务类型',
    biz_id        bigint                               COMMENT '业务ID',
    created_by    bigint                               COMMENT '创建人',
    created_at    datetime                             COMMENT '创建时间',
    updated_by    bigint                               COMMENT '更新人',
    updated_at    datetime                             COMMENT '更新时间',
    deleted_flag  tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no    int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    KEY idx_sys_file_biz (biz_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- ----------------------------
-- 12. 消息表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_message (
    id           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    title        varchar(200) NOT NULL                COMMENT '消息标题',
    content      text                                 COMMENT '消息内容',
    msg_type     varchar(20)  NOT NULL                COMMENT '消息类型',
    receiver_id  bigint       NOT NULL                COMMENT '接收人ID',
    sender_id    bigint                               COMMENT '发送人ID',
    is_read      tinyint      NOT NULL DEFAULT 0      COMMENT '是否已读: 0=未读, 1=已读',
    read_time    datetime                             COMMENT '阅读时间',
    biz_type     varchar(50)                          COMMENT '业务类型',
    biz_id       bigint                               COMMENT '业务ID',
    created_by   bigint                               COMMENT '创建人',
    created_at   datetime                             COMMENT '创建时间',
    updated_by   bigint                               COMMENT '更新人',
    updated_at   datetime                             COMMENT '更新时间',
    deleted_flag tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no   int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    KEY idx_sys_message_receiver_id (receiver_id),
    KEY idx_sys_message_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- ----------------------------
-- 13. 登录日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_login_log (
    id           bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    username     varchar(50)                         COMMENT '登录账号',
    user_id      bigint                              COMMENT '用户ID',
    ip_addr      varchar(50)                         COMMENT 'IP地址',
    browser      varchar(100)                        COMMENT '浏览器',
    os           varchar(100)                        COMMENT '操作系统',
    login_status varchar(20) NOT NULL                COMMENT '登录状态',
    login_time   datetime    NOT NULL                COMMENT '登录时间',
    msg          varchar(255)                        COMMENT '提示消息',
    PRIMARY KEY (id),
    KEY idx_sys_login_log_user_id (user_id),
    KEY idx_sys_login_log_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ----------------------------
-- 14. 操作日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_operate_log (
    id               bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    module_name      varchar(50)                          COMMENT '模块名称',
    operation_name   varchar(100)                         COMMENT '操作名称',
    request_method   varchar(10)                          COMMENT '请求方法',
    request_url      varchar(500)                         COMMENT '请求URL',
    request_params   text                                 COMMENT '请求参数',
    response_result  text                                 COMMENT '响应结果',
    user_id          bigint                               COMMENT '操作用户ID',
    username         varchar(50)                          COMMENT '操作用户账号',
    ip_addr          varchar(50)                          COMMENT 'IP地址',
    operate_time     datetime     NOT NULL                COMMENT '操作时间',
    cost_time        bigint                               COMMENT '耗时(毫秒)',
    PRIMARY KEY (id),
    KEY idx_sys_operate_log_user_id (user_id),
    KEY idx_sys_operate_log_time (operate_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ----------------------------
-- 15. 异常日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_exception_log (
    id                bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    request_url       varchar(500)                         COMMENT '请求URL',
    request_method    varchar(10)                          COMMENT '请求方法',
    request_params    text                                 COMMENT '请求参数',
    exception_name    varchar(200)                         COMMENT '异常类名',
    exception_message text                                 COMMENT '异常信息',
    user_id           bigint                               COMMENT '用户ID',
    username          varchar(50)                          COMMENT '用户账号',
    ip_addr           varchar(50)                          COMMENT 'IP地址',
    operate_time      datetime     NOT NULL                COMMENT '发生时间',
    PRIMARY KEY (id),
    KEY idx_sys_exception_log_time (operate_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常日志表';
