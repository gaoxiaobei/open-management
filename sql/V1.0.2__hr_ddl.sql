-- ============================================================
-- V1.0.2 人事域表结构 DDL
-- ============================================================

-- ----------------------------
-- 1. 员工档案表
-- ----------------------------
CREATE TABLE IF NOT EXISTS hr_employee (
    id           bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    emp_no       varchar(50) NOT NULL                COMMENT '工号',
    emp_name     varchar(50) NOT NULL                COMMENT '姓名',
    gender       varchar(10)                         COMMENT '性别',
    id_no        varchar(18)                         COMMENT '身份证号',
    dept_id      bigint      NOT NULL                COMMENT '部门ID',
    position_id  bigint                              COMMENT '岗位ID',
    hire_date    date        NOT NULL                COMMENT '入职日期',
    emp_status   varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '在职状态',
    mobile       varchar(20)                         COMMENT '手机号',
    email        varchar(100)                        COMMENT '邮箱',
    created_by   bigint                              COMMENT '创建人',
    created_at   datetime                            COMMENT '创建时间',
    updated_by   bigint                              COMMENT '更新人',
    updated_at   datetime                            COMMENT '更新时间',
    deleted_flag tinyint     NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no   int         NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_hr_employee_emp_no (emp_no),
    KEY idx_hr_employee_dept_id (dept_id),
    KEY idx_hr_employee_status (emp_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工档案表';

-- ----------------------------
-- 2. 员工异动记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS hr_employee_change (
    id                   bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    employee_id          bigint      NOT NULL                COMMENT '员工ID',
    change_type          varchar(50) NOT NULL                COMMENT '调动类型',
    before_dept_id       bigint                              COMMENT '调动前部门ID',
    after_dept_id        bigint                              COMMENT '调动后部门ID',
    before_position_id   bigint                              COMMENT '调动前岗位ID',
    after_position_id    bigint                              COMMENT '调动后岗位ID',
    change_date          date        NOT NULL                COMMENT '生效日期',
    remark               varchar(500)                        COMMENT '备注',
    created_by           bigint                              COMMENT '创建人',
    created_at           datetime                            COMMENT '创建时间',
    updated_by           bigint                              COMMENT '更新人',
    updated_at           datetime                            COMMENT '更新时间',
    deleted_flag         tinyint     NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    version_no           int         NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    KEY idx_hr_emp_change_employee_id (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工异动记录表';
