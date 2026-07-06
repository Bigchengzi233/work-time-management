# 员工工时管理系统 Apifox 接口测试记录

本文档用于记录员工工时管理系统在 Apifox 中进行的后端接口测试过程。接口字段和业务规则以当前项目代码、数据库脚本和 `test_doc/api-doc.md` 为准。

## 1. 测试环境

| 项目 | 内容 |
| --- | --- |
| 测试工具 | Apifox |
| 后端地址 | `http://localhost:18080/api` |
| 前端地址 | `http://localhost:5173` |
| 数据库 | MySQL，数据库名 `work_time_management` |
| 后端服务 | Spring Boot，端口 `18080` |
| 测试方式 | 手动接口测试 |
| 认证方式 | 登录后在请求头携带 `Authorization: Bearer <token>` |

## 2. Apifox 测试准备

### 2.1 环境变量

建议在 Apifox 中新建本地测试环境，并配置变量：

| 变量名 | 变量值 | 说明 |
| --- | --- | --- |
| `baseUrl` | `http://localhost:18080/api` | 后端接口统一前缀 |
| `token` | 登录接口返回的 token | 用于访问需要登录的接口 |

### 2.2 通用请求头

除登录接口和健康检查接口外，其余接口都需要添加：

```text
Authorization: Bearer {{token}}
Content-Type: application/json
```

### 2.3 测试账号

| 角色 | 手机号 | 用途 |
| --- | --- | --- |
| 管理员 | `13800000000` | 测试部门、用户、项目、公司统计、全部日志 |
| 部门经理 | `13800000001` | 测试本部门授权、审批、部门统计 |
| 员工 | `13800000003`、`13800000004` | 测试本人工时填报、修改、删除、提交、个人统计 |

说明：测试密码以数据库初始化脚本或 README 中的初始化测试账号说明为准，本文档不重复记录真实密码。

## 3. 总体测试流程

1. 启动 MySQL，并确认 `work_time_management` 数据库存在。
2. 启动后端 `BackendApplication`，确认端口为 `18080`。
3. 在 Apifox 中先调用健康检查接口，确认后端服务可访问。
4. 调用登录接口，获取 token。
5. 将 token 保存到 Apifox 环境变量 `token`。
6. 按照“基础数据 -> 项目授权 -> 工时填报 -> 审批 -> 日志 -> 统计 -> 个人中心”的顺序测试。
7. 每次测试新增、修改、删除后，到列表接口或数据库中确认数据变化。
8. 使用不同角色账号分别测试权限范围，确认不能越权访问。

## 4. 测试结论总览

| 模块 | 测试内容 | 结论 |
| --- | --- | --- |
| 健康检查 | 后端启动检查、异常响应格式 | 通过 |
| 登录认证 | 登录成功、密码错误、token 访问当前用户 | 通过 |
| 部门管理 | 管理员查询、新增、修改、删除部门 | 通过 |
| 用户管理 | 管理员查询、新增、修改、删除用户 | 通过 |
| 项目管理 | 管理员查询、新增、修改、删除项目 | 通过 |
| 员工项目授权 | 部门经理授权、修改状态、取消授权 | 通过 |
| 工时管理 | 员工新增、修改、删除、提交工时 | 通过 |
| 工时审批 | 部门经理查询待审批、通过、驳回 | 通过 |
| 操作日志 | 管理员查看全部日志，按工时查看日志 | 通过 |
| 统计分析 | 个人、部门、公司工时统计 | 通过 |
| 个人中心 | 查询资料、修改资料、修改密码 | 通过 |
| 权限控制 | 管理员、经理、员工访问范围校验 | 通过 |

## 5. 详细接口测试记录

### 5.1 健康检查

| 编号 | 方法 | 完整测试 URL | 请求重点 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- | --- |
| H-01 | GET | `http://localhost:18080/api/health/ping` | 不需要 token | 返回 `code=200`，data 中提示后端正在运行 | 通过 |
| H-02 | GET | `http://localhost:18080/api/health/fail` | 验证业务失败响应 | 返回统一 JSON 错误格式 | 通过 |
| H-03 | GET | `http://localhost:18080/api/health/exception` | 验证系统异常响应 | 返回统一 JSON 错误格式，不暴露详细堆栈 | 通过 |

### 5.2 登录认证

| 编号 | 方法 | 完整测试 URL | 请求重点 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- | --- |
| A-01 | POST | `http://localhost:18080/api/auth/login` | 管理员手机号和正确密码 | 返回 `code=200`，data 中包含 token 和用户信息 | 通过 |
| A-02 | POST | `http://localhost:18080/api/auth/login` | 密码错误 | 返回登录失败提示 | 通过 |
| A-03 | GET | `http://localhost:18080/api/auth/me` | 请求头携带 token | 返回当前登录用户信息 | 通过 |
| A-04 | GET | `http://localhost:18080/api/auth/me` | 不携带 token | 返回未登录或认证失败提示 | 通过 |

登录请求示例：

```json
{
  "phone": "13800000000",
  "password": "<管理员测试密码>"
}
```

### 5.3 部门管理

| 编号 | 方法 | 完整测试 URL | 角色 | 请求重点 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- | --- | --- |
| D-01 | GET | `http://localhost:18080/api/departments` | 管理员 | 查询部门列表 | 返回全部部门 | 通过 |
| D-02 | GET | `http://localhost:18080/api/departments/1` | 管理员 | 查询单个部门 | 返回 `deptId=1` 的部门信息 | 通过 |
| D-03 | POST | `http://localhost:18080/api/departments` | 管理员 | 新增部门名称 | 新增成功，列表可查到 | 通过 |
| D-04 | PUT | `http://localhost:18080/api/departments/{deptId}` | 管理员 | 修改部门名称 | 修改成功，列表显示新名称 | 通过 |
| D-05 | DELETE | `http://localhost:18080/api/departments/{deptId}` | 管理员 | 删除无关联部门 | 删除成功，列表不再显示 | 通过 |
| D-06 | DELETE | `http://localhost:18080/api/departments/1` | 管理员 | 删除有关联用户或项目的部门 | 返回友好错误，不允许删除 | 通过 |
| D-07 | POST | `http://localhost:18080/api/departments` | 员工 | 员工尝试新增部门 | 返回无权限提示 | 通过 |

新增部门请求示例：

```json
{
  "deptName": "测试部门"
}
```

### 5.4 用户管理

| 编号 | 方法 | 完整测试 URL | 角色 | 请求重点 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- | --- | --- |
| U-01 | GET | `http://localhost:18080/api/users` | 管理员 | 查询用户列表 | 返回全部用户 | 通过 |
| U-02 | GET | `http://localhost:18080/api/users/1` | 管理员 | 查询单个用户 | 返回指定用户信息 | 通过 |
| U-03 | POST | `http://localhost:18080/api/users` | 管理员 | 新增用户 | 新增成功，列表可查到 | 通过 |
| U-04 | PUT | `http://localhost:18080/api/users/{userId}` | 管理员 | 修改用户姓名、手机号、邮箱、角色、部门 | 修改成功 | 通过 |
| U-05 | PUT | `http://localhost:18080/api/users/{userId}` | 管理员 | 密码字段为空字符串 | 不修改原密码，其他字段正常更新 | 通过 |
| U-06 | POST | `http://localhost:18080/api/users` | 管理员 | 手机号重复 | 返回手机号重复提示 | 通过 |
| U-07 | DELETE | `http://localhost:18080/api/users/{userId}` | 管理员 | 删除无工时关联用户 | 删除成功 | 通过 |
| U-08 | DELETE | `http://localhost:18080/api/users/{userId}` | 管理员 | 删除已有工时记录用户 | 返回不允许删除提示 | 通过 |
| U-09 | GET | `http://localhost:18080/api/users` | 部门经理 | 查询用户列表 | 只能看到本部门相关用户 | 通过 |

新增用户请求示例：

```json
{
  "userName": "测试员工",
  "phone": "13900000001",
  "password": "test123456",
  "email": "test@example.com",
  "userRole": "2",
  "deptId": 1
}
```

### 5.5 项目管理

| 编号 | 方法 | 完整测试 URL | 角色 | 请求重点 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- | --- | --- |
| P-01 | GET | `http://localhost:18080/api/projects` | 管理员 | 查询项目列表 | 返回全部项目 | 通过 |
| P-02 | GET | `http://localhost:18080/api/projects/{projectId}` | 管理员 | 查询单个项目 | 返回项目详情 | 通过 |
| P-03 | POST | `http://localhost:18080/api/projects` | 管理员 | 新增项目 | 新增成功 | 通过 |
| P-04 | PUT | `http://localhost:18080/api/projects/{projectId}` | 管理员 | 修改项目名称、部门、状态 | 修改成功 | 通过 |
| P-05 | DELETE | `http://localhost:18080/api/projects/{projectId}` | 管理员 | 删除无关联项目 | 删除成功 | 通过 |
| P-06 | DELETE | `http://localhost:18080/api/projects/{projectId}` | 管理员 | 删除已有授权或工时记录项目 | 返回不允许删除提示 | 通过 |
| P-07 | GET | `http://localhost:18080/api/projects` | 部门经理 | 查询项目列表 | 只能看到本部门项目 | 通过 |

新增项目请求示例：

```json
{
  "projectName": "测试项目",
  "deptId": 1,
  "projectStatus": 1
}
```

### 5.6 员工项目授权

| 编号 | 方法 | 完整测试 URL | 角色 | 请求重点 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- | --- | --- |
| UP-01 | GET | `http://localhost:18080/api/user-projects` | 部门经理 | 查询授权列表 | 返回本部门授权数据 | 通过 |
| UP-02 | GET | `http://localhost:18080/api/user-projects/{authId}` | 部门经理 | 查询单条授权 | 返回指定授权详情 | 通过 |
| UP-03 | GET | `http://localhost:18080/api/user-projects/users/{userId}` | 部门经理 | 查询某员工授权项目 | 返回该员工已授权项目 | 通过 |
| UP-04 | POST | `http://localhost:18080/api/user-projects` | 部门经理 | 给本部门员工授权本部门项目 | 授权成功 | 通过 |
| UP-05 | PUT | `http://localhost:18080/api/user-projects/{authId}` | 部门经理 | 修改授权状态 | 状态修改成功 | 通过 |
| UP-06 | DELETE | `http://localhost:18080/api/user-projects/{authId}` | 部门经理 | 取消授权 | `authStatus` 改为 `0`，返回成功 | 通过 |
| UP-07 | POST | `http://localhost:18080/api/user-projects` | 部门经理 | 重复授权同一用户同一项目 | 返回重复授权提示 | 通过 |
| UP-08 | POST | `http://localhost:18080/api/user-projects` | 部门经理 | 授权跨部门用户或项目 | 返回无权限或不允许跨部门提示 | 通过 |

新增授权请求示例：

```json
{
  "userId": 5,
  "projectId": 2,
  "authStatus": 1
}
```

### 5.7 工时管理

| 编号 | 方法 | 完整测试 URL | 角色 | 请求重点 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- | --- | --- |
| W-01 | GET | `http://localhost:18080/api/work-times` | 员工 | 查询工时列表 | 员工只能看到本人未删除工时 | 通过 |
| W-02 | GET | `http://localhost:18080/api/work-times/{workId}` | 员工 | 查询本人单条工时 | 返回工时详情 | 通过 |
| W-03 | GET | `http://localhost:18080/api/work-times/users/{userId}` | 员工 | 查询本人指定用户工时 | 返回本人数据 | 通过 |
| W-04 | POST | `http://localhost:18080/api/work-times` | 员工 | 新增草稿工时 | 新增成功，状态为草稿 | 通过 |
| W-05 | PUT | `http://localhost:18080/api/work-times/{workId}` | 员工 | 修改草稿或驳回工时 | 修改成功 | 通过 |
| W-06 | PUT | `http://localhost:18080/api/work-times/{workId}/submit` | 员工 | 提交草稿工时 | 状态变为待审批 | 通过 |
| W-07 | DELETE | `http://localhost:18080/api/work-times/{workId}?userId={userId}` | 员工 | 删除本人草稿或驳回工时 | `isDeleted` 改为 `1`，日志保留 | 通过 |
| W-08 | POST | `http://localhost:18080/api/work-times` | 员工 | 填报未来日期 | 返回日期不能晚于当前日期提示 | 通过 |
| W-09 | POST | `http://localhost:18080/api/work-times` | 员工 | 工时不是 0.5 粒度 | 返回工时格式错误提示 | 通过 |
| W-10 | POST | `http://localhost:18080/api/work-times` | 员工 | 同一用户、同一项目、同一天重复填报 | 返回重复填报提示 | 通过 |
| W-11 | POST | `http://localhost:18080/api/work-times` | 员工 | 填报未授权项目 | 返回没有项目授权提示 | 通过 |

新增工时请求示例：

```json
{
  "userId": 5,
  "projectId": 1,
  "workDate": "2026-07-04",
  "workHours": 7.5,
  "workDesc": "完成页面联调和接口测试"
}
```

删除工时完整 URL 示例：

```text
DELETE http://localhost:18080/api/work-times/5?userId=5
```

说明：删除工时不是物理删除，而是把 `work_time_apply.is_deleted` 改为 `1`，这样历史日志仍然可以追踪。

### 5.8 工时审批

| 编号 | 方法 | 完整测试 URL | 角色 | 请求重点 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- | --- | --- |
| R-01 | GET | `http://localhost:18080/api/work-times/pending` | 部门经理 | 查询待审批工时 | 返回本部门待审批工时 | 通过 |
| R-02 | PUT | `http://localhost:18080/api/work-times/{workId}/approve` | 部门经理 | 审批通过本部门待审批工时 | 状态变为审批通过 | 通过 |
| R-03 | PUT | `http://localhost:18080/api/work-times/{workId}/reject` | 部门经理 | 驳回本部门待审批工时并填写原因 | 状态变为已驳回 | 通过 |
| R-04 | PUT | `http://localhost:18080/api/work-times/{workId}/approve` | 部门经理 | 审批非本部门工时 | 返回无权限提示 | 通过 |
| R-05 | PUT | `http://localhost:18080/api/work-times/{workId}/reject` | 部门经理 | 驳回原因为空 | 返回驳回原因不能为空提示 | 通过 |

审批通过请求示例：

```json
{
  "operatorId": 2,
  "operationDesc": "工时填写合理，审批通过"
}
```

驳回请求示例：

```json
{
  "operatorId": 2,
  "operationDesc": "请补充当天具体工作内容"
}
```

### 5.9 工时操作日志

| 编号 | 方法 | 完整测试 URL | 角色 | 请求重点 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- | --- | --- |
| L-01 | GET | `http://localhost:18080/api/work-time-logs` | 管理员 | 查询全部工时操作日志 | 返回所有可见日志 | 通过 |
| L-02 | GET | `http://localhost:18080/api/work-time-logs` | 部门经理 | 查询日志 | 返回本部门相关日志 | 通过 |
| L-03 | GET | `http://localhost:18080/api/work-time-logs` | 员工 | 查询日志 | 返回本人相关日志 | 通过 |
| L-04 | GET | `http://localhost:18080/api/work-time-logs/work-times/{workId}` | 管理员 | 按工时编号查询日志 | 返回该工时的操作记录 | 通过 |

日志字段说明：

| 字段 | 含义 |
| --- | --- |
| `logId` | 日志编号 |
| `workId` | 关联的工时申报单编号 |
| `operationType` | 操作类型：0 新建、1 修改、2 提交、3 审批通过、4 驳回 |
| `operationTime` | 操作时间 |
| `operatorId` | 操作人编号 |
| `operatorName` | 操作人姓名 |
| `operationDesc` | 操作说明 |

### 5.10 统计分析

| 编号 | 方法 | 完整测试 URL | 角色 | 请求重点 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- | --- | --- |
| S-01 | GET | `http://localhost:18080/api/statistics/personal?userId={userId}&startDate=2026-07-01&endDate=2026-07-31` | 员工 | 查询个人统计 | 返回本人审批通过且未删除的工时统计 | 通过 |
| S-02 | GET | `http://localhost:18080/api/statistics/department?deptId={deptId}&startDate=2026-07-01&endDate=2026-07-31` | 部门经理 | 查询本部门统计 | 返回本部门审批通过且未删除的工时统计 | 通过 |
| S-03 | GET | `http://localhost:18080/api/statistics/admin/department?deptId={deptId}&startDate=2026-07-01&endDate=2026-07-31` | 管理员 | 管理员按部门查询统计 | 返回指定部门统计 | 通过 |
| S-04 | GET | `http://localhost:18080/api/statistics/company?startDate=2026-07-01&endDate=2026-07-31` | 管理员 | 查询公司统计 | 返回公司级工时统计 | 通过 |
| S-05 | GET | `http://localhost:18080/api/statistics/company?startDate=2026-07-31&endDate=2026-07-01` | 管理员 | 开始日期晚于结束日期 | 返回日期范围错误提示 | 通过 |

说明：统计接口只统计 `status=2` 审批通过且 `is_deleted=0` 未删除的工时数据。

### 5.11 个人中心

| 编号 | 方法 | 完整测试 URL | 角色 | 请求重点 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- | --- | --- |
| PR-01 | GET | `http://localhost:18080/api/profile` | 已登录用户 | 查询个人资料 | 返回当前登录用户资料 | 通过 |
| PR-02 | PUT | `http://localhost:18080/api/profile` | 已登录用户 | 修改姓名和邮箱 | 修改成功，重新查询显示新资料 | 通过 |
| PR-03 | PUT | `http://localhost:18080/api/profile/password` | 已登录用户 | 正确原密码、新密码和确认密码一致 | 密码修改成功 | 通过 |
| PR-04 | PUT | `http://localhost:18080/api/profile/password` | 已登录用户 | 原密码错误 | 返回原密码错误提示 | 通过 |
| PR-05 | PUT | `http://localhost:18080/api/profile/password` | 已登录用户 | 新密码和确认密码不一致 | 返回两次密码不一致提示 | 通过 |

修改个人资料请求示例：

```json
{
  "userName": "系统管理员",
  "email": "admin@example.com"
}
```

修改密码请求示例：

```json
{
  "oldPassword": "<当前密码>",
  "newPassword": "<新密码>",
  "confirmPassword": "<确认新密码>"
}
```

## 6. 权限测试记录

| 编号 | 测试场景 | 操作账号 | 预期结果 | 测试结论 |
| --- | --- | --- | --- | --- |
| AUTH-01 | 员工访问部门新增接口 | 员工 | 返回无权限提示 | 通过 |
| AUTH-02 | 员工访问用户管理接口 | 员工 | 返回无权限提示或前端菜单不可见 | 通过 |
| AUTH-03 | 部门经理审批其他部门工时 | 部门经理 | 返回无权限提示 | 通过 |
| AUTH-04 | 部门经理给其他部门员工授权 | 部门经理 | 返回无权限或跨部门限制提示 | 通过 |
| AUTH-05 | 管理员查询公司统计 | 管理员 | 查询成功 | 通过 |
| AUTH-06 | 未登录访问受保护接口 | 未登录 | 返回未登录或 token 无效提示 | 通过 |

## 7. 常见问题记录

### 7.1 Apifox 浏览器限制

如果使用 Apifox 网页版访问本地服务，可能会出现浏览器跨域或云端代理无法解析 `localhost` 的问题。

处理方式：

- 优先使用 Apifox 桌面版。
- 或安装 Apifox 浏览器扩展后再访问本地接口。
- 本项目本地开发推荐使用桌面版测试。

### 7.2 token 未设置

表现：

- 登录接口成功，但访问其他接口返回未登录。

处理方式：

- 确认 Apifox 环境变量 `token` 已保存。
- 确认请求头为 `Authorization: Bearer {{token}}`。
- 注意 `Bearer` 和 token 中间有一个空格。

### 7.3 删除接口返回成功但列表不显示变化

工时删除是软删除，接口成功后数据库中记录不会消失，而是 `is_deleted` 字段变为 `1`。列表接口默认只查询未删除数据，所以页面和接口列表中不会再显示该记录。

### 7.4 统计结果和工时列表数量不一致

统计接口只统计审批通过的有效工时，也就是：

- `status=2`
- `is_deleted=0`

草稿、待审批、已驳回、已删除的工时不会进入统计。

## 8. 本次测试范围说明

本次测试重点覆盖系统核心业务流程：

- 登录认证是否可用。
- 不同角色权限是否正确。
- 基础数据增删改查是否可用。
- 部门经理项目授权是否可用。
- 员工工时填报、修改、删除、提交是否可用。
- 部门经理审批通过和驳回是否可用。
- 工时操作日志是否能追踪关键操作。
- 个人、部门、公司统计是否符合业务规则。
- 个人中心资料和密码修改是否可用。

本次未覆盖内容：

- 自动化接口测试脚本。
- 高并发压力测试。
- 生产环境安全扫描。
- 浏览器兼容性专项测试。

这些内容可以作为后续版本迭代和项目完善方向。
