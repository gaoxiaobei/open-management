ALTER TABLE sys_login_log
    MODIFY COLUMN browser varchar(500) COMMENT '浏览器',
    MODIFY COLUMN os      varchar(500) COMMENT '操作系统';
