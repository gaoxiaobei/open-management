# SQL 初始化脚本

本目录存放 Open Management 系统数据库初始化脚本，按 Flyway 版本命名规范组织。

## 脚本清单

| 文件 | 说明 |
| --- | --- |
| V1.0.0__platform_ddl.sql | 平台底座表结构 DDL |
| V1.0.1__workflow_ddl.sql | 工作流表结构 DDL |
| V1.0.2__hr_ddl.sql | 人事域表结构 DDL |
| V1.0.3__oa_ddl.sql | OA 域表结构 DDL |
| V1.0.4__asset_ddl.sql | 资产域表结构 DDL |
| V1.0.5__init_data.sql | 初始化基础数据 |

## 使用说明

- 数据库：MySQL 8 或 PostgreSQL（推荐 PostgreSQL）
- 字符集：utf8mb4
- 执行顺序：按版本号顺序依次执行
- 初始管理员账号：admin，初始密码执行后需立即修改
