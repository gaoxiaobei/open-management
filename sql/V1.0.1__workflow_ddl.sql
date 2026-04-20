-- ============================================================
-- V1.0.1 工作流表结构 DDL
-- ============================================================

-- ----------------------------
-- 1. 流程定义表
-- ----------------------------
CREATE TABLE IF NOT EXISTS wf_process_definition (
    id                  bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    process_key         varchar(100) NOT NULL                COMMENT '流程标识',
    process_name        varchar(200) NOT NULL                COMMENT '流程名称',
    form_key            varchar(100)                         COMMENT '关联表单标识',
    status              varchar(20)  NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
    remark              varchar(500)                         COMMENT '备注',
    created_by          bigint                               COMMENT '创建人',
    created_at          datetime                             COMMENT '创建时间',
    updated_by          bigint                               COMMENT '更新人',
    updated_at          datetime                             COMMENT '更新时间',
    deleted_flag        tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no          int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_wf_pd_process_key (process_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程定义表';

-- ----------------------------
-- 2. 流程实例表
-- ----------------------------
CREATE TABLE IF NOT EXISTS wf_process_instance (
    id                    bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    process_key           varchar(100) NOT NULL                COMMENT '流程标识',
    process_name          varchar(200)                         COMMENT '流程名称',
    business_key          varchar(100)                         COMMENT '业务单据号',
    business_type         varchar(50)                          COMMENT '业务类型',
    starter_id            bigint       NOT NULL                COMMENT '发起人ID',
    start_time            datetime     NOT NULL                COMMENT '发起时间',
    end_time              datetime                             COMMENT '结束时间',
    status                varchar(20)  NOT NULL DEFAULT 'RUNNING' COMMENT '流程状态',
    flowable_instance_id  varchar(100)                         COMMENT 'Flowable流程实例ID',
    created_by            bigint                               COMMENT '创建人',
    created_at            datetime                             COMMENT '创建时间',
    updated_by            bigint                               COMMENT '更新人',
    updated_at            datetime                             COMMENT '更新时间',
    deleted_flag          tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no            int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_wf_pi_business_key (business_key),
    KEY idx_wf_pi_starter_id (starter_id),
    KEY idx_wf_pi_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程实例表';

-- ----------------------------
-- 3. 审批任务表
-- ----------------------------
CREATE TABLE IF NOT EXISTS wf_task (
    id                   bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    process_instance_id  bigint       NOT NULL                COMMENT '流程实例ID',
    flowable_task_id     varchar(100)                         COMMENT 'Flowable任务ID',
    task_name            varchar(200)                         COMMENT '任务节点名称',
    assignee_id          bigint       NOT NULL                COMMENT '审批人ID',
    claim_time           datetime                             COMMENT '签收时间',
    complete_time        datetime                             COMMENT '完成时间',
    action               varchar(20)                          COMMENT '审批动作: APPROVE/REJECT/TRANSFER',
    comment              text                                 COMMENT '审批意见',
    status               varchar(20)  NOT NULL DEFAULT 'PENDING' COMMENT '任务状态',
    created_by           bigint                               COMMENT '创建人',
    created_at           datetime                             COMMENT '创建时间',
    updated_by           bigint                               COMMENT '更新人',
    updated_at           datetime                             COMMENT '更新时间',
    deleted_flag         tinyint      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no           int          NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    KEY idx_wf_task_instance_id (process_instance_id),
    KEY idx_wf_task_assignee_id (assignee_id),
    KEY idx_wf_task_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批任务表';
