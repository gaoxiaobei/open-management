-- ============================================================
-- V1.0.4 资产域表结构 DDL
-- ============================================================

-- ----------------------------
-- 1. 资产信息表
-- ----------------------------
CREATE TABLE IF NOT EXISTS asset_info (
    id               bigint         NOT NULL AUTO_INCREMENT COMMENT '主键',
    asset_code       varchar(50)    NOT NULL                COMMENT '资产编号',
    asset_name       varchar(100)   NOT NULL                COMMENT '资产名称',
    category         varchar(50)    NOT NULL                COMMENT '资产类别',
    brand            varchar(100)                           COMMENT '品牌',
    model            varchar(100)                           COMMENT '型号',
    purchase_date    date                                   COMMENT '购置日期',
    purchase_price   decimal(12,2)                          COMMENT '购置金额',
    asset_status     varchar(20)    NOT NULL DEFAULT 'IDLE' COMMENT '资产状态',
    location         varchar(200)                           COMMENT '存放位置',
    current_user_id  bigint                                 COMMENT '当前使用人ID',
    dept_id          bigint                                 COMMENT '所属部门ID',
    remark           varchar(500)                           COMMENT '备注',
    created_by       bigint                                 COMMENT '创建人',
    created_at       datetime                               COMMENT '创建时间',
    updated_by       bigint                                 COMMENT '更新人',
    updated_at       datetime                               COMMENT '更新时间',
    deleted_flag     tinyint        NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no       int            NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_asset_info_code (asset_code),
    KEY idx_asset_info_status (asset_status),
    KEY idx_asset_info_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产信息表';

-- ----------------------------
-- 2. 资产领用申请表
-- ----------------------------
CREATE TABLE IF NOT EXISTS asset_receive_apply (
    id                   bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    apply_no             varchar(50) NOT NULL                COMMENT '申请单号',
    asset_id             bigint      NOT NULL                COMMENT '资产ID',
    applicant_id         bigint      NOT NULL                COMMENT '申请人ID',
    reason               varchar(500)                        COMMENT '申请原因',
    process_instance_id  bigint                              COMMENT '流程实例ID',
    apply_status         varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '申请状态',
    created_by           bigint                              COMMENT '创建人',
    created_at           datetime                            COMMENT '创建时间',
    updated_by           bigint                              COMMENT '更新人',
    updated_at           datetime                            COMMENT '更新时间',
    deleted_flag         tinyint     NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no           int         NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_asset_receive_apply_no (apply_no),
    KEY idx_asset_receive_asset_id (asset_id),
    KEY idx_asset_receive_applicant_id (applicant_id),
    KEY idx_asset_receive_status (apply_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产领用申请表';

-- ----------------------------
-- 3. 资产归还记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS asset_return_record (
    id           bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    record_no    varchar(50) NOT NULL                COMMENT '归还单号',
    asset_id     bigint      NOT NULL                COMMENT '资产ID',
    returner_id  bigint      NOT NULL                COMMENT '归还人ID',
    return_date  date        NOT NULL                COMMENT '归还日期',
    remark       varchar(500)                        COMMENT '备注',
    created_by   bigint                              COMMENT '创建人',
    created_at   datetime                            COMMENT '创建时间',
    updated_by   bigint                              COMMENT '更新人',
    updated_at   datetime                            COMMENT '更新时间',
    deleted_flag tinyint     NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no   int         NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_asset_return_record_no (record_no),
    KEY idx_asset_return_asset_id (asset_id),
    KEY idx_asset_return_returner_id (returner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产归还记录表';

-- ----------------------------
-- 4. 资产报修申请表
-- ----------------------------
CREATE TABLE IF NOT EXISTS asset_repair_apply (
    id                   bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    apply_no             varchar(50) NOT NULL                COMMENT '申请单号',
    asset_id             bigint      NOT NULL                COMMENT '资产ID',
    applicant_id         bigint      NOT NULL                COMMENT '申请人ID',
    fault_desc           varchar(500)                        COMMENT '故障描述',
    process_instance_id  bigint                              COMMENT '流程实例ID',
    apply_status         varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '申请状态',
    created_by           bigint                              COMMENT '创建人',
    created_at           datetime                            COMMENT '创建时间',
    updated_by           bigint                              COMMENT '更新人',
    updated_at           datetime                            COMMENT '更新时间',
    deleted_flag         tinyint     NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no           int         NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_asset_repair_apply_no (apply_no),
    KEY idx_asset_repair_asset_id (asset_id),
    KEY idx_asset_repair_applicant_id (applicant_id),
    KEY idx_asset_repair_status (apply_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产报修申请表';

-- ----------------------------
-- 5. 资产报废申请表
-- ----------------------------
CREATE TABLE IF NOT EXISTS asset_scrap_apply (
    id                   bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    apply_no             varchar(50) NOT NULL                COMMENT '申请单号',
    asset_id             bigint      NOT NULL                COMMENT '资产ID',
    applicant_id         bigint      NOT NULL                COMMENT '申请人ID',
    scrap_reason         varchar(500)                        COMMENT '报废原因',
    process_instance_id  bigint                              COMMENT '流程实例ID',
    apply_status         varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '申请状态',
    created_by           bigint                              COMMENT '创建人',
    created_at           datetime                            COMMENT '创建时间',
    updated_by           bigint                              COMMENT '更新人',
    updated_at           datetime                            COMMENT '更新时间',
    deleted_flag         tinyint     NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no           int         NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_asset_scrap_apply_no (apply_no),
    KEY idx_asset_scrap_asset_id (asset_id),
    KEY idx_asset_scrap_applicant_id (applicant_id),
    KEY idx_asset_scrap_status (apply_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产报废申请表';
