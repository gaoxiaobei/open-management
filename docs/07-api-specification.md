# 接口设计说明书

| 项目 | 内容 |
| --- | --- |
| 项目名称 | Open Management |
| 文档编号 | OM-DES-API-001 |
| 文档版本 | V1.0 |
| 文档状态 | 评审版 |
| 密级 | 内部 |
| 编制日期 | 2026-04-20 |
| 适用阶段 | 设计评审、前后端联调、接口实现 |
| 责任角色 | 架构师、后端负责人、前端负责人 |

## 1. 修订记录

| 版本 | 日期 | 修订人 | 修订说明 |
| --- | --- | --- | --- |
| V1.0 | 2026-04-20 | Codex | 将接口草案整理为正式评审版接口设计说明书 |

## 2. 文档目的

本文档用于统一接口风格、鉴权方式、错误模型、分页排序规范和关键业务接口定义，作为前后端并行开发与联调的契约基线。

## 3. 总体规范

- 协议：HTTPS
- 风格：RESTful
- 数据格式：JSON
- 编码：UTF-8
- 接口前缀：`/api`

### 3.1 请求头

| Header | 说明 |
| --- | --- |
| Authorization | `Bearer Token` |
| Content-Type | `application/json` |
| X-Trace-Id | 链路追踪 ID |

### 3.2 通用响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "TRACE202604200001"
}
```

### 3.3 通用错误码

| code | 说明 |
| --- | --- |
| 0 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录或登录失效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 409 | 数据冲突 |
| 500 | 系统异常 |

### 3.4 分页与排序约定

- 分页参数统一为 `pageNum`、`pageSize`
- 排序参数建议统一为 `sortField`、`sortOrder`
- 列表响应统一返回 `total` 和 `records`

## 4. 鉴权与安全要求

- 所有受保护接口必须校验登录态
- 业务接口必须校验菜单、按钮和数据权限
- 文件下载、预览接口不得绕开权限校验
- 审批接口必须校验当前任务处理权限

## 5. 接口目录

| 模块 | 接口示例 | 说明 |
| --- | --- | --- |
| 认证 | `/api/auth/login` | 登录、登出、会话维护 |
| 系统管理 | `/api/system/users` | 用户、角色、菜单、字典、参数 |
| 组织架构 | `/api/org/depts/tree` | 部门树、岗位 |
| 文件中心 | `/api/files/upload` | 文件上传、下载、预览 |
| 工作流 | `/api/workflow/process/start` | 发起流程、任务处理 |
| 人事 | `/api/hr/employees` | 员工档案、人员异动 |
| OA | `/api/oa/leave-applications` | 请假、出差、报销 |
| 资产 | `/api/assets` | 台账、领用、归还、维修、报废 |
| 消息中心 | `/api/messages/my` | 消息查询、已读处理 |

## 6. 关键接口定义

### 6.1 登录

`POST /api/auth/login`

请求体：

```json
{
  "username": "zhangsan",
  "password": "123456",
  "captcha": "8K2P",
  "captchaKey": "cpt_001"
}
```

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9",
    "userInfo": {
      "id": 1001,
      "username": "zhangsan",
      "realName": "张三",
      "deptId": 10
    },
    "menus": []
  }
}
```

### 6.2 退出登录

`POST /api/auth/logout`

### 6.3 分页查询用户

`GET /api/system/users`

查询参数：

- `pageNum`
- `pageSize`
- `username`
- `realName`
- `deptId`
- `status`

### 6.4 新增用户

`POST /api/system/users`

请求体：

```json
{
  "username": "lisi",
  "realName": "李四",
  "mobile": "13800000000",
  "email": "lisi@example.com",
  "deptId": 20,
  "positionId": 5,
  "roleIds": [2, 3]
}
```

### 6.5 修改用户

`PUT /api/system/users/{id}`

### 6.6 重置密码

`POST /api/system/users/{id}/reset-password`

### 6.7 查询部门树

`GET /api/org/depts/tree`

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "deptName": "集团总部",
      "children": [
        {
          "id": 10,
          "deptName": "综合管理部"
        }
      ]
    }
  ]
}
```

### 6.8 上传文件

`POST /api/files/upload`

请求方式：

- `multipart/form-data`

字段：

- `file`
- `bizType`
- `bizId`

### 6.9 发起流程

`POST /api/workflow/process/start`

请求体：

```json
{
  "processKey": "leave_apply_process",
  "businessKey": "LEAVE_20260001",
  "businessType": "OA_LEAVE",
  "formData": {
    "applyId": 3001
  }
}
```

### 6.10 审批任务处理

`POST /api/workflow/tasks/{taskId}/complete`

请求体：

```json
{
  "action": "APPROVE",
  "comment": "同意",
  "nextAssigneeIds": [1003]
}
```

### 6.11 新增员工档案

`POST /api/hr/employees`

请求体：

```json
{
  "empNo": "E10001",
  "empName": "王五",
  "gender": "M",
  "idNo": "110101199001010011",
  "deptId": 10,
  "positionId": 3,
  "hireDate": "2025-01-01",
  "mobile": "13900000000"
}
```

### 6.12 分页查询员工档案

`GET /api/hr/employees`

### 6.13 提交请假申请

`POST /api/oa/leave-applications`

请求体：

```json
{
  "leaveType": "ANNUAL",
  "startTime": "2025-02-10 09:00:00",
  "endTime": "2025-02-12 18:00:00",
  "reason": "返乡探亲",
  "fileIds": [9001, 9002]
}
```

### 6.14 查询我的请假申请

`GET /api/oa/leave-applications/my`

### 6.15 查询资产台账

`GET /api/assets`

查询参数：

- `assetCode`
- `assetName`
- `category`
- `assetStatus`

### 6.16 提交资产领用申请

`POST /api/assets/receive-applications`

请求体：

```json
{
  "assetId": 5001,
  "reason": "新员工办公配发"
}
```

### 6.17 查询我的消息

`GET /api/messages/my`

### 6.18 标记消息已读

`POST /api/messages/{id}/read`

## 7. 兼容性与变更控制

- 非破坏性变更优先通过新增字段实现
- 破坏性变更必须升级接口版本或明确迁移窗口
- 错误码、字段名、状态枚举应纳入统一治理

## 8. 后续补充项

- 输出 OpenAPI 3.0 完整 YAML
- 补充导入导出、批量操作、幂等等接口规范
- 明确公共请求头、签名和防重放策略
