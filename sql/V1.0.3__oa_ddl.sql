-- ============================================================
-- V1.0.3 OA 域表结构 DDL
-- ============================================================

-- ----------------------------
-- 1. 请假申请表
-- ----------------------------
CREATE TABLE IF NOT EXISTS oa_leave_apply (
    id                   bigint         NOT NULL AUTO_INCREMENT COMMENT '主键',
    apply_no             varchar(50)    NOT NULL                COMMENT '申请单号',
    applicant_id         bigint         NOT NULL                COMMENT '申请人ID',
    leave_type           varchar(20)    NOT NULL                COMMENT '请假类型',
    start_time           datetime       NOT NULL                COMMENT '开始时间',
    end_time             datetime       NOT NULL                COMMENT '结束时间',
    leave_days           decimal(6,2)   NOT NULL                COMMENT '请假天数',
    reason               varchar(500)                           COMMENT '请假事由',
    process_instance_id  bigint                                 COMMENT '流程实例ID',
    apply_status         varchar(20)    NOT NULL DEFAULT 'DRAFT' COMMENT '申请状态',
    created_by           bigint                                 COMMENT '创建人',
    created_at           datetime                               COMMENT '创建时间',
    updated_by           bigint                                 COMMENT '更新人',
    updated_at           datetime                               COMMENT '更新时间',
    deleted_flag         tinyint        NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no           int            NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_oa_leave_apply_no (apply_no),
    KEY idx_oa_leave_applicant_id (applicant_id),
    KEY idx_oa_leave_status (apply_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';

-- ----------------------------
-- 2. 出差申请表
-- ----------------------------
CREATE TABLE IF NOT EXISTS oa_travel_apply (
    id                   bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    apply_no             varchar(50)  NOT NULL                COMMENT '申请单号',
    applicant_id         bigint       NOT NULL                COMMENT '申请人ID',
    destination          varchar(200) NOT NULL                COMMENT '出差目的地',
    start_date           date         NOT NULL                COMMENT '出发日期',
    end_date             date         NOT NULL                COMMENT '返回日期',
    travel_days          int          NOT NULL                COMMENT '出差天数',
    purpose              varchar(500)                         COMMENT '出差目的',
    process_instance_id  bigint                               COMMENT '流程实例ID',
    apply_status         varchar(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '申请状态',
    created_by           bigint                               COMMENT '创建人',
    created_at           datetime                             COMMENT '创建时间',
    updated_by           bigint                               COMMENT '更新人',
    updated_at           datetime                             COMMENT '更新时间',
    deleted_flag         tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no           int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_oa_travel_apply_no (apply_no),
    KEY idx_oa_travel_applicant_id (applicant_id),
    KEY idx_oa_travel_status (apply_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出差申请表';

-- ----------------------------
-- 3. 费用报销申请表
-- ----------------------------
CREATE TABLE IF NOT EXISTS oa_expense_apply (
    id                   bigint         NOT NULL AUTO_INCREMENT COMMENT '主键',
    apply_no             varchar(50)    NOT NULL                COMMENT '申请单号',
    applicant_id         bigint         NOT NULL                COMMENT '申请人ID',
    expense_type         varchar(50)    NOT NULL                COMMENT '费用类型',
    total_amount         decimal(12,2)  NOT NULL                COMMENT '报销总金额',
    reason               varchar(500)                           COMMENT '报销事由',
    process_instance_id  bigint                                 COMMENT '流程实例ID',
    apply_status         varchar(20)    NOT NULL DEFAULT 'DRAFT' COMMENT '申请状态',
    created_by           bigint                                 COMMENT '创建人',
    created_at           datetime                               COMMENT '创建时间',
    updated_by           bigint                                 COMMENT '更新人',
    updated_at           datetime                               COMMENT '更新时间',
    deleted_flag         tinyint        NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no           int            NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_oa_expense_apply_no (apply_no),
    KEY idx_oa_expense_applicant_id (applicant_id),
    KEY idx_oa_expense_status (apply_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用报销申请表';
