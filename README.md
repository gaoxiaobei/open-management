# Open Management

国有企业综合管理系统平台。

本仓库包含 `open-management` 项目的需求/设计文档、数据库脚本、后端多模块工程（Spring Boot）、前端管理台（Vue 3）和 Windows 桌面客户端（Electron）。

## 1. 项目定位

`open-management` 面向国有企业场景，采用"企业级 Web 平台 + Windows 客户端封装"的总体方案，先建设统一平台底座，再逐步扩展 OA、人事、资产、合同、采购、报表等业务模块。

## 2. 建设目标

- 建立统一认证、统一权限、统一组织、统一流程、统一文件、统一消息、统一日志的平台底座
- 支撑千级用户规模，并为后续横向扩展和服务拆分预留空间
- 满足国企场景对安全性、审计性、可维护性和可扩展性的要求
- 采用稳定成熟、长期维护成本可控的技术栈
- 交付为 Windows 软件，同时保持集中部署和统一运维能力

## 3. 推荐技术路线

### 3.1 前端

- Vue 3 + TypeScript
- Element Plus
- Pinia
- Vue Router
- ECharts

### 3.2 Windows 客户端

- Electron
- electron-builder

### 3.3 后端

- Java 17
- Spring Boot 3
- Sa-Token（认证授权）
- MyBatis-Plus
- Flowable（工作流）
- Redis
- RabbitMQ
- MinIO

### 3.4 数据库

- MySQL 8（当前）
- PostgreSQL（可选）

## 4. 当前开发状态

> 第一期平台底座正在建设中。详细进度见 **[TODO.md](TODO.md)**。

| 模块 | 状态 |
|------|------|
| om-common（公共基础） | ✅ 已完成 |
| om-app（Spring Boot 主配置） | ✅ 已完成 |
| om-auth（认证授权） | ✅ 已完成 |
| om-system（用户/角色/菜单/字典/参数） | ✅ 已完成 |
| om-org（组织架构） | ✅ 后端与前端管理页完成 |
| om-audit（日志审计） | ✅ 后端服务、AOP 与前端页面完成 |
| om-file（文件中心） | ✅ MinIO 集成与文件服务完成，联调待补充 |
| om-message（消息中心） | ✅ MessageService / TodoGenerateService / 前端消息中心完成 |
| om-workflow（工作流） | ✅ Flowable 集成、待办页、流程详情页、流程管理页完成 |
| om-hr / om-oa / om-asset | 🔨 骨架完成，Service 待实现 |
| 前端（Vue 3） | 🔨 平台底座页面完成，业务模块页面持续补全 |
| 桌面端（Electron） | 🔨 骨架完成，打包配置待完善 |
| 数据库 DDL（Flyway，6 个脚本） | ✅ 全部已就位 |

## 5. 仓库结构

```text
.
├─ README.md
├─ TODO.md                    # 开发进度追踪
├─ backend/                   # Spring Boot 多模块后端
│  ├─ om-common/              # 公共工具、统一响应、基类
│  ├─ om-auth/                # 认证授权（Sa-Token + 验证码）
│  ├─ om-system/              # 系统管理（用户/角色/菜单/字典/参数）
│  ├─ om-org/                 # 组织架构（部门/岗位）
│  ├─ om-audit/               # 日志审计（登录日志/操作日志）
│  ├─ om-file/                # 文件中心（MinIO）
│  ├─ om-message/             # 消息中心（待办/通知）
│  ├─ om-workflow/            # 工作流（Flowable）
│  ├─ om-hr/                  # 人事管理
│  ├─ om-oa/                  # OA 审批（请假/出差/报销）
│  ├─ om-asset/               # 资产管理
│  └─ om-app/                 # Spring Boot 启动模块
├─ frontend/                  # Vue 3 + TypeScript 管理台
├─ desktop/                   # Electron Windows 客户端
├─ sql/                       # Flyway 数据库迁移脚本
└─ docs/                      # 需求、架构、设计文档
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
   ├─ 10-roadmap.md
   └─ 11-test-catalog.md
```

## 6. 快速开始（本地开发）

### 前置依赖

| 工具 | 版本 |
|------|------|
| JDK | 17+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 6+ |
| MinIO | 最新版 |
| RabbitMQ | 3.x |

### 后端启动

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE open_management CHARACTER SET utf8mb4;"

# 2. 按需修改数据源配置
#    backend/om-app/src/main/resources/application-dev.yml

# 3. 编译并启动（Flyway 会自动执行 sql/ 下的迁移脚本）
cd backend
mvn clean package -DskipTests
java -jar om-app/target/om-app-1.0.0-SNAPSHOT.jar
```

启动后 API 文档：http://localhost:8080/doc.html

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

默认访问：http://localhost:3000

### Dev Container（VS Code / Dev Containers）

仓库已包含 `.devcontainer/` 配置，可直接在 VS Code 中执行 `Dev Containers: Reopen in Container`。

开发容器会提供以下环境：

- Java 17 + Maven
- Node.js 20 + npm
- MySQL 8
- Redis 7
- RabbitMQ 3（管理台端口 `15672`）
- MinIO（API `9000`，控制台 `9001`，默认桶 `open-management`）

基础设施端口在宿主机侧仅绑定到 `127.0.0.1`，避免开发容器把 MySQL、Redis、RabbitMQ、MinIO 直接暴露到外部网络。

> 若你在不同版本的 `.devcontainer` 凭据之间切换过，已有命名卷中的账号口令不会自动迁移；此时请删除对应容器/volume 后再重建开发容器。

容器创建完成后会自动安装 `frontend/` 与 `desktop/` 的 Node 依赖。

> ⚠ 若 Electron 下载超时，容器会跳过 desktop 安装（不影响 Web 端开发）。如需桌面端，可在容器内手动执行：
> ```bash
> ELECTRON_MIRROR=https://npmmirror.com/mirrors/electron/ npm install --prefix desktop
> ```

启动命令：

```bash
# 后端（先编译再启动）
cd backend && mvn clean package -DskipTests && java -jar om-app/target/om-app-1.0.0-SNAPSHOT.jar

# 前端（另一个终端）
cd frontend && npm run dev -- --host 0.0.0.0
```

## 7. 文档入口

- [开发进度追踪](TODO.md)
- [文档目录](docs/00-index.md)
- [用户故事](docs/01-user-stories.md)
- [功能规格说明书](docs/02-functional-specification.md)
- [软件需求规格说明书](docs/03-software-requirements-specification.md)
- [架构设计说明书](docs/04-architecture.md)
- [数据库设计说明书](docs/05-database-design.md)
- [模块设计说明书](docs/06-module-design.md)
- [接口设计说明书](docs/07-api-specification.md)
- [项目实施计划](docs/08-implementation-plan.md)
- [开发路线图](docs/10-roadmap.md)
- [测试用例目录](docs/11-test-catalog.md)
