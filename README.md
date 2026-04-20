# Open Management

国有企业综合管理系统平台文档仓库。

本仓库用于沉淀 `open-management` 项目的正式需求、设计与实施文档，作为项目立项、需求评审、设计评审、开发实施和交付验收的统一基线。

## 1. 项目定位

`open-management` 面向国有企业场景，采用“企业级 Web 平台 + Windows 客户端封装”的总体方案，先建设统一平台底座，再逐步扩展 OA、人事、资产、合同、采购、报表等业务模块。

## 2. 建设目标

- 建立统一认证、统一权限、统一组织、统一流程、统一文件、统一消息、统一日志的平台底座
- 支撑千级用户规模，并为后续横向扩展和服务拆分预留空间
- 满足国企场景对安全性、审计性、可维护性和可扩展性的要求
- 采用稳定成熟、长期维护成本可控的技术栈
- 交付为 Windows 软件，同时保持集中部署和统一运维能力

## 3. 推荐技术路线

### 3.1 前端

- Vue 3
- TypeScript
- Element Plus
- Pinia
- Vue Router
- ECharts

### 3.2 Windows 客户端

- Electron
- electron-builder

### 3.3 后端

- Java 17
- Spring Boot
- Spring Security 或 Sa-Token
- MyBatis-Plus
- Flowable
- Redis
- RabbitMQ
- MinIO

### 3.4 数据库

- PostgreSQL
- MySQL 8

## 4. 当前文档基线

当前仓库已整理为正式评审版文档包，版本基线为 `V1.0`，日期为 `2026-04-20`。文档默认状态为“评审版”，后续如进入开发基线，应在修订记录中显式升级版本。

## 5. 文档目录

```text
.
├─ README.md
└─ docs/
   ├─ 00-index.md
   ├─ 01-user-stories.md
   ├─ 02-functional-specification.md
   ├─ 03-software-requirements-specification.md
   ├─ 04-architecture.md
   ├─ 05-database-design.md
   ├─ 06-module-design.md
   ├─ 07-api-specification.md
   ├─ 08-implementation-plan.md
   ├─ 09-document-control.md
   └─ 10-roadmap.md
```

## 6. 文档入口

- [文档目录](docs/00-index.md)
- [用户故事](docs/01-user-stories.md)
- [功能规格说明书](docs/02-functional-specification.md)
- [软件需求规格说明书](docs/03-software-requirements-specification.md)
- [架构设计说明书](docs/04-architecture.md)
- [数据库设计说明书](docs/05-database-design.md)
- [模块设计说明书](docs/06-module-design.md)
- [接口设计说明书](docs/07-api-specification.md)
- [项目实施计划](docs/08-implementation-plan.md)
- [文档控制说明](docs/09-document-control.md)
- [开发路线图](docs/10-roadmap.md)

## 7. 文档使用建议

- 立项和需求评审阶段：优先阅读用户故事、FSD、SRS
- 设计评审阶段：优先阅读架构、数据库、模块、接口文档
- 项目管理和交付阶段：优先阅读实施计划和文档控制说明

## 8. 后续建议

- 增补数据库初始化脚本和菜单权限初始化脚本
- 建立后端多模块工程骨架
- 建立前端管理台与 Electron 客户端骨架
- 输出 OpenAPI 完整 YAML 和数据库 DDL 脚本
