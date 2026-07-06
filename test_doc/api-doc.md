# 员工工时管理系统后端 API 接口文档

本文档依据当前代码重新整理，主要参考：

- `backend/src/main/java/com/worktime/controller/`
- `backend/src/main/java/com/worktime/service/impl/`
- `backend/src/main/java/com/worktime/dto/`
- `backend/src/main/java/com/worktime/vo/`
- `database/schema.sql`
- `database/init-data.sql`

旧版 `员工工时管理系统-后端API接口文档.md` 不作为本文档依据。

## 1. 通用说明

### 1.1 基础地址

```text
http://localhost:18080/api
```

### 1.2 统一返回格式

所有接口统一返回：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

字段说明：

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| code | number | 业务状态码，`200` 表示成功 |
| message | string | 提示信息 |
| data | object / array / null | 真实返回数据 |

常见失败示例：

```json
{
  "code": 403,
  "message": "只有管理员可以执行该操作",
  "data": null
}
```

### 1.3 登录认证

除登录和健康检查接口外，其他接口都需要登录后访问。

请求头格式：

```text
Authorization: Bearer <登录后返回的token>
```

### 1.4 角色枚举

| userRole | 角色 | 说明 |
| --- | --- | --- |
| `0` | 管理员 | 维护基础数据，查看公司统计和全部日志 |
| `1` | 部门经理 | 管理本部门授权，审批本部门员工工时 |
| `2` | 员工 | 填报、修改、提交和查询本人工时 |

### 1.5 业务枚举

项目状态 `projectStatus`：

| 值 | 含义 |
| --- | --- |
| 0 | 禁用 |
| 1 | 启用 |

授权状态 `authStatus`：

| 值 | 含义 |
| --- | --- |
| 0 | 取消授权 |
| 1 | 有效授权 |

工时状态 `status`：

| 值 | 含义 |
| --- | --- |
| 0 | 草稿 |
| 1 | 待审批 |
| 2 | 审批通过 |
| 3 | 已驳回 |

工时日志操作类型 `operationType`：

| 值 | 含义 |
| --- | --- |
| 0 | 新建 |
| 1 | 修改 |
| 2 | 提交 |
| 3 | 审批通过 |
| 4 | 驳回 |

删除标记 `isDeleted`：

| 值 | 含义 |
| --- | --- |
| 0 | 未删除 |
| 1 | 已删除 |

## 2. 返回对象字段

### 2.1 UserVO

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| userId | number | 用户编号 |
| userName | string | 用户姓名 |
| phone | string | 手机号 |
| email | string/null | 邮箱 |
| userRole | string | 用户角色：0/1/2 |
| deptId | number | 所属部门编号 |
| deptName | string | 所属部门名称 |

### 2.2 DepartmentVO

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| deptId | number | 部门编号 |
| deptName | string | 部门名称 |

### 2.3 ProjectVO

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| projectId | number | 项目编号 |
| projectName | string | 项目名称 |
| projectStatus | number | 项目状态：0禁用，1启用 |
| deptId | number | 所属部门编号 |
| deptName | string | 所属部门名称 |

### 2.4 UserProjectVO

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| authId | number | 授权编号 |
| userId | number | 员工编号 |
| userName | string | 员工姓名 |
| userDeptId | number | 员工部门编号 |
| userDeptName | string | 员工部门名称 |
| projectId | number | 项目编号 |
| projectName | string | 项目名称 |
| projectStatus | number | 项目状态 |
| projectDeptId | number | 项目部门编号 |
| projectDeptName | string | 项目部门名称 |
| authTime | string | 授权时间 |
| authStatus | number | 授权状态 |

### 2.5 WorkTimeApplyVO

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| workId | number | 工时编号 |
| userId | number | 填报人编号 |
| userName | string | 填报人姓名 |
| userDeptId | number | 填报人部门编号 |
| userDeptName | string | 填报人部门名称 |
| projectId | number | 项目编号 |
| projectName | string | 项目名称 |
| projectStatus | number | 项目状态 |
| projectDeptId | number | 项目部门编号 |
| projectDeptName | string | 项目部门名称 |
| workDate | string | 工作日期，格式 `YYYY-MM-DD` |
| workHours | number | 工时数 |
| workDesc | string/null | 工作描述 |
| status | number | 工时状态 |
| isDeleted | number | 删除标记 |

### 2.6 WorkTimeLogVO

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| logId | number | 日志编号 |
| workId | number | 工时编号 |
| operationType | number | 操作类型 |
| operationTime | string | 操作时间 |
| operatorId | number | 操作人编号 |
| operatorName | string | 操作人姓名 |
| workUserId | number/null | 工时所属员工编号 |
| workUserName | string/null | 工时所属员工姓名 |
| userDeptName | string/null | 员工部门名称 |
| projectName | string/null | 项目名称 |
| workDate | string/null | 工作日期 |
| operationDesc | string/null | 操作说明 |

### 2.7 WorkTimeStatisticsVO

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| statisticsType | string | 统计类型：personal / department / company |
| scopeId | number | 统计对象编号 |
| scopeName | string | 统计对象名称 |
| startDate | string | 开始日期 |
| endDate | string | 结束日期 |
| totalHours | number | 总工时 |
| details | array | 工时明细列表 |

`details` 明细字段：

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| workId | number | 工时编号 |
| userId | number | 员工编号 |
| userName | string | 员工姓名 |
| deptId | number | 部门编号 |
| deptName | string | 部门名称 |
| projectId | number | 项目编号 |
| projectName | string | 项目名称 |
| workDate | string | 工作日期 |
| workHours | number | 工时数 |
| workDesc | string/null | 工作描述 |

## 3. 健康检查接口

### 3.1 后端启动检查

```text
GET http://localhost:18080/api/health/ping
```

权限：无需登录。

含义：检查后端服务是否正常启动。

成功示例：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": "员工工时管理系统后端启动成功"
}
```

### 3.2 失败响应测试

```text
GET http://localhost:18080/api/health/fail
```

权限：无需登录。

含义：返回一个模拟失败响应，用于学习统一响应格式。

### 3.3 业务异常测试

```text
GET http://localhost:18080/api/health/exception
```

权限：无需登录。

含义：抛出业务异常，用于学习全局异常处理。

## 4. 登录认证接口

### 4.1 登录

```text
POST http://localhost:18080/api/auth/login
```

权限：无需登录。

Body：

```json
{
  "phone": "13800000000",
  "password": "admin123456"
}
```

参数说明：

| 字段 | 必填 | 规则 | 含义 |
| --- | --- | --- | --- |
| phone | 是 | 11位手机号格式 | 登录手机号 |
| password | 是 | 6到50字符 | 登录密码 |

成功返回 `LoginVO`：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "xxx",
    "userId": 1,
    "userName": "系统管理员",
    "phone": "13800000000",
    "userRole": "0",
    "deptId": 4,
    "deptName": "综合管理部"
  }
}
```

业务规则：

- 手机号不存在时返回 `手机号或密码错误`。
- 密码错误时返回 `手机号或密码错误`。
- 初始化脚本中的明文密码登录成功后，会自动升级为 BCrypt 哈希密码。

### 4.2 查询当前登录用户

```text
GET http://localhost:18080/api/auth/me
```

权限：已登录用户。

请求头：

```text
Authorization: Bearer <token>
```

含义：根据 token 查询当前登录用户信息。

返回：`UserVO`。

## 5. 个人中心接口

### 5.1 查询个人资料

```text
GET http://localhost:18080/api/profile
```

权限：已登录用户。

含义：查询当前登录用户个人资料。

返回：`UserVO`。

### 5.2 修改个人资料

```text
PUT http://localhost:18080/api/profile
```

权限：已登录用户。

Body：

```json
{
  "userName": "张三",
  "email": "zhangsan_new@example.com"
}
```

参数说明：

| 字段 | 必填 | 规则 | 含义 |
| --- | --- | --- | --- |
| userName | 是 | 最长50字符 | 用户姓名 |
| email | 否 | 邮箱格式，最长100字符 | 邮箱 |

返回：修改后的 `UserVO`。

业务规则：

- 只能修改当前登录用户自己的姓名和邮箱。
- 手机号、角色、部门仍由管理员在用户管理中维护。
- 邮箱为空字符串时，后端会入库为 `null`。

### 5.3 修改密码

```text
PUT http://localhost:18080/api/profile/password
```

权限：已登录用户。

Body：

```json
{
  "oldPassword": "admin123456",
  "newPassword": "new123456"
}
```

参数说明：

| 字段 | 必填 | 规则 | 含义 |
| --- | --- | --- | --- |
| oldPassword | 是 | 6到50字符 | 原密码 |
| newPassword | 是 | 6到50字符 | 新密码 |

成功返回：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

业务规则：

- 原密码错误时返回 `原密码错误`。
- 新密码不能和原密码相同。
- 新密码会使用 BCrypt 哈希后保存。

## 6. 部门管理接口

### 6.1 查询部门列表

```text
GET http://localhost:18080/api/departments
```

权限：管理员。

含义：查询所有部门。

返回：`DepartmentVO[]`。

### 6.2 查询单个部门

```text
GET http://localhost:18080/api/departments/{deptId}
```

权限：管理员。

路径参数：

| 参数 | 含义 |
| --- | --- |
| deptId | 部门编号 |

返回：`DepartmentVO`。

### 6.3 新增部门

```text
POST http://localhost:18080/api/departments
```

权限：管理员。

Body：

```json
{
  "deptName": "测试部门"
}
```

参数说明：

| 字段 | 必填 | 规则 | 含义 |
| --- | --- | --- | --- |
| deptName | 是 | 最长50字符 | 部门名称 |

业务规则：

- 部门名称不能为空。
- 部门名称不能重复。

### 6.4 修改部门

```text
PUT http://localhost:18080/api/departments/{deptId}
```

权限：管理员。

Body：

```json
{
  "deptName": "测试部门修改后"
}
```

业务规则：

- 部门必须存在。
- 新部门名称不能和其他部门重复。

### 6.5 删除部门

```text
DELETE http://localhost:18080/api/departments/{deptId}
```

权限：管理员。

业务规则：

- 部门必须存在。
- 部门下存在用户时不能删除。
- 部门下存在项目时不能删除。

## 7. 用户管理接口

### 7.1 查询用户列表

```text
GET http://localhost:18080/api/users
```

权限：管理员、部门经理。

返回：`UserVO[]`。

业务规则：

- 管理员可查看全部用户。
- 部门经理只能查看本部门员工，不能查看管理员和其他部门用户。

### 7.2 查询单个用户

```text
GET http://localhost:18080/api/users/{userId}
```

权限：管理员。

返回：`UserVO`。

### 7.3 新增用户

```text
POST http://localhost:18080/api/users
```

权限：管理员。

Body：

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

参数说明：

| 字段 | 必填 | 规则 | 含义 |
| --- | --- | --- | --- |
| userName | 是 | 最长50字符 | 用户姓名 |
| phone | 是 | 11位手机号 | 登录手机号 |
| password | 是 | 6到50字符 | 初始密码 |
| email | 否 | 邮箱格式，最长100字符 | 邮箱 |
| userRole | 是 | 0/1/2 | 用户角色 |
| deptId | 是 | 已存在部门编号 | 所属部门 |

业务规则：

- 手机号不能重复。
- 所属部门必须存在。
- 密码会使用 BCrypt 哈希保存。

### 7.4 修改用户

```text
PUT http://localhost:18080/api/users/{userId}
```

权限：管理员。

Body：

```json
{
  "userName": "测试员工修改后",
  "phone": "13900000001",
  "password": "",
  "email": "test_update@example.com",
  "userRole": "2",
  "deptId": 1
}
```

业务规则：

- 用户必须存在。
- 手机号不能被其他用户占用。
- 所属部门必须存在。
- `password` 为空字符串时表示不修改密码。
- `password` 非空时长度必须为 6 到 50 字符，并会重新哈希保存。

### 7.5 删除用户

```text
DELETE http://localhost:18080/api/users/{userId}
```

权限：管理员。

业务规则：

- 用户必须存在。
- 用户存在工时申报数据时不能删除。
- 用户作为操作人存在工时日志时不能删除。
- 删除用户前会删除其项目授权记录。

## 8. 项目管理接口

### 8.1 查询项目列表

```text
GET http://localhost:18080/api/projects
```

权限：管理员、部门经理。

返回：`ProjectVO[]`。

业务规则：

- 管理员可查看全部项目。
- 部门经理只能查看本部门项目。

### 8.2 查询单个项目

```text
GET http://localhost:18080/api/projects/{projectId}
```

权限：管理员。

返回：`ProjectVO`。

### 8.3 新增项目

```text
POST http://localhost:18080/api/projects
```

权限：管理员。

Body：

```json
{
  "projectName": "测试项目",
  "projectStatus": 1,
  "deptId": 1
}
```

业务规则：

- 项目名称不能为空，最长100字符。
- 项目状态只能是 `0` 或 `1`。
- 所属部门必须存在。

### 8.4 修改项目

```text
PUT http://localhost:18080/api/projects/{projectId}
```

权限：管理员。

Body：

```json
{
  "projectName": "测试项目修改后",
  "projectStatus": 1,
  "deptId": 1
}
```

业务规则：

- 项目必须存在。
- 新所属部门必须存在。

### 8.5 删除项目

```text
DELETE http://localhost:18080/api/projects/{projectId}
```

权限：管理员。

业务规则：

- 项目必须存在。
- 项目存在工时申报数据时不能删除。
- 项目存在员工授权记录时不能删除。

## 9. 员工项目授权接口

### 9.1 查询授权列表

```text
GET http://localhost:18080/api/user-projects
```

权限：部门经理。

返回：`UserProjectVO[]`。

业务规则：

- 部门经理只能查看本部门员工与本部门项目的授权记录。

### 9.2 查询单条授权

```text
GET http://localhost:18080/api/user-projects/{authId}
```

权限：部门经理。

业务规则：

- 授权记录必须存在。
- 部门经理只能查看本部门授权记录。

### 9.3 按用户查询授权项目

```text
GET http://localhost:18080/api/user-projects/users/{userId}
```

权限：员工、部门经理。

返回：`UserProjectVO[]`。

业务规则：

- 员工只能查询自己的授权项目。
- 部门经理只能查询本部门员工的授权项目。
- 管理员当前无权使用此接口。

### 9.4 新增授权

```text
POST http://localhost:18080/api/user-projects
```

权限：部门经理。

Body：

```json
{
  "userId": 5,
  "projectId": 2,
  "authStatus": 1
}
```

业务规则：

- 只能给普通员工分配项目。
- 员工和项目必须属于同一部门。
- 部门经理只能管理本部门员工和本部门项目。
- 禁用项目不能设置为有效授权。
- 同一员工和同一项目只能存在一条授权记录。

### 9.5 修改授权状态

```text
PUT http://localhost:18080/api/user-projects/{authId}
```

权限：部门经理。

Body：

```json
{
  "authStatus": 0
}
```

业务规则：

- 授权记录必须存在。
- `authStatus` 只能是 `0` 或 `1`。
- 禁用项目不能被设置为有效授权。
- 部门经理只能修改本部门授权记录。

### 9.6 取消授权

```text
DELETE http://localhost:18080/api/user-projects/{authId}
```

权限：部门经理。

业务规则：

- 不是物理删除，而是把 `auth_status` 改为 `0`。
- 部门经理只能取消本部门授权记录。

成功返回：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 10. 工时管理接口

### 10.1 查询全部工时

```text
GET http://localhost:18080/api/work-times
```

权限：管理员。

返回：`WorkTimeApplyVO[]`。

业务规则：

- 只查询 `is_deleted = 0` 的工时。

### 10.2 查询单条工时

```text
GET http://localhost:18080/api/work-times/{workId}
```

权限：管理员、部门经理、员工。

业务规则：

- 管理员可查看任意未删除工时。
- 部门经理可查看本部门员工未删除工时。
- 员工只能查看本人未删除工时。

### 10.3 按用户查询工时

```text
GET http://localhost:18080/api/work-times/users/{userId}
```

权限：管理员、员工。

业务规则：

- 员工只能查询本人。
- 管理员可以查询任意用户。
- 部门经理不能使用此接口，应使用待审批接口。

### 10.4 查询部门经理待审批工时

```text
GET http://localhost:18080/api/work-times/pending/managers/{managerId}
```

权限：部门经理。

业务规则：

- 只能使用当前登录经理自己的 `managerId`。
- 只查询本部门 `status = 1` 且 `is_deleted = 0` 的工时。

### 10.5 新增工时草稿

```text
POST http://localhost:18080/api/work-times
```

权限：员工。

Body：

```json
{
  "userId": 5,
  "projectId": 1,
  "workDate": "2026-07-04",
  "workHours": 7.5,
  "workDesc": "完成测试用例整理"
}
```

业务规则：

- 员工只能为自己新增工时。
- 用户和项目必须存在。
- 员工必须拥有该项目的有效授权。
- 项目必须处于启用状态。
- 工作日期不能晚于当前日期。
- 工时范围为 0.5 到 24。
- 工时必须按 0.5 小时粒度填写。
- 同一员工、同一项目、同一天只能存在一条未删除工时。
- 新增后状态为草稿 `0`。
- 新增成功后写入操作日志，类型为 `0`。

### 10.6 修改工时

```text
PUT http://localhost:18080/api/work-times/{workId}
```

权限：员工。

Body：

```json
{
  "projectId": 1,
  "workDate": "2026-07-04",
  "workHours": 8.0,
  "workDesc": "修改后的工作描述"
}
```

业务规则：

- 只能修改本人填报的工时。
- 只有草稿 `0` 或已驳回 `3` 的工时可以修改。
- 已驳回工时修改后会回到草稿 `0`。
- 仍需满足项目授权、工作日期、工时数、重复填报等规则。
- 修改成功后写入操作日志，类型为 `1`。

### 10.7 提交审批

```text
POST http://localhost:18080/api/work-times/{workId}/submit
```

权限：员工。

业务规则：

- 只能提交本人填报的工时。
- 只有草稿 `0` 可以提交。
- 提交后状态变为待审批 `1`。
- 提交成功后写入操作日志，类型为 `2`。

### 10.8 删除工时

```text
DELETE http://localhost:18080/api/work-times/{workId}?userId={userId}
```

权限：员工。

示例：

```text
DELETE http://localhost:18080/api/work-times/5?userId=5
```

业务规则：

- 只能删除本人填报的工时。
- 只有草稿 `0` 或已驳回 `3` 的工时可以删除。
- 删除采用软删除，把 `is_deleted` 改为 `1`。
- 删除前会写入一条操作日志，当前日志类型为 `1`。

### 10.9 审批通过工时

```text
POST http://localhost:18080/api/work-times/{workId}/approve
```

权限：部门经理。

Body：

```json
{
  "managerId": 2,
  "operationDesc": "审批通过"
}
```

业务规则：

- 只能使用当前登录经理自己的 `managerId`。
- 审批人必须是部门经理。
- 部门经理只能审批本部门员工工时。
- 只有待审批 `1` 的工时可以审批通过。
- 通过后状态变为审批通过 `2`。
- 成功后写入操作日志，类型为 `3`。
- `operationDesc` 可为空，空时默认写入 `部门经理审批通过`。

### 10.10 驳回工时

```text
POST http://localhost:18080/api/work-times/{workId}/reject
```

权限：部门经理。

Body：

```json
{
  "managerId": 2,
  "rejectReason": "工作描述不够清楚，请补充说明"
}
```

业务规则：

- 只能使用当前登录经理自己的 `managerId`。
- 部门经理只能驳回本部门员工工时。
- 只有待审批 `1` 的工时可以驳回。
- 驳回后状态变为已驳回 `3`。
- 驳回原因不能为空，最多500字符。
- 成功后写入操作日志，类型为 `4`。

## 11. 工时操作日志接口

### 11.1 查询可见日志列表

```text
GET http://localhost:18080/api/work-time-logs
```

权限：管理员、部门经理、员工。

可选 Query 参数：

| 参数 | 必填 | 含义 |
| --- | --- | --- |
| workId | 否 | 按工时编号筛选 |
| operationType | 否 | 按操作类型筛选 |

示例：

```text
GET http://localhost:18080/api/work-time-logs
GET http://localhost:18080/api/work-time-logs?workId=2
GET http://localhost:18080/api/work-time-logs?operationType=3
GET http://localhost:18080/api/work-time-logs?workId=2&operationType=2
```

业务规则：

- 管理员可查看全部工时日志。
- 部门经理可查看本部门员工工时日志。
- 员工只能查看本人工时日志。

返回：`WorkTimeLogVO[]`。

### 11.2 根据工时编号查询日志

```text
GET http://localhost:18080/api/work-time-logs/work-times/{workId}
```

权限：管理员、部门经理、员工。

业务规则：

- 工时编号必须存在，包含已软删除工时。
- 管理员可查看任意工时日志。
- 部门经理可查看本部门员工工时日志。
- 员工只能查看本人工时日志。

返回：`WorkTimeLogVO[]`。

## 12. 工时统计接口

统计接口只统计：

```text
status = 2 且 is_deleted = 0
```

也就是只统计审批通过且未删除的工时。

### 12.1 查询个人工时统计

```text
GET http://localhost:18080/api/statistics/personal?userId={userId}&startDate={startDate}&endDate={endDate}
```

示例：

```text
GET http://localhost:18080/api/statistics/personal?userId=5&startDate=2026-07-01&endDate=2026-07-31
```

权限：管理员、员工。

业务规则：

- 员工只能查询本人。
- 管理员可查询任意员工。
- 部门经理当前无权使用此个人统计接口。
- 开始日期不能晚于结束日期。

返回：`WorkTimeStatisticsVO`。

### 12.2 部门经理查询本部门统计

```text
GET http://localhost:18080/api/statistics/department?managerId={managerId}&startDate={startDate}&endDate={endDate}
```

示例：

```text
GET http://localhost:18080/api/statistics/department?managerId=2&startDate=2026-07-01&endDate=2026-07-31
```

权限：部门经理。

业务规则：

- 只能使用当前登录经理自己的 `managerId`。
- 查询人必须是部门经理。
- 只统计该经理所在部门员工的审批通过工时。

### 12.3 管理员按部门查询统计

```text
GET http://localhost:18080/api/statistics/department-by-dept?adminId={adminId}&deptId={deptId}&startDate={startDate}&endDate={endDate}
```

示例：

```text
GET http://localhost:18080/api/statistics/department-by-dept?adminId=1&deptId=1&startDate=2026-07-01&endDate=2026-07-31
```

权限：管理员。

业务规则：

- 只能使用当前登录管理员自己的 `adminId`。
- 查询人必须是管理员。
- 部门必须存在。

### 12.4 管理员查询公司统计

```text
GET http://localhost:18080/api/statistics/company?adminId={adminId}&startDate={startDate}&endDate={endDate}
```

示例：

```text
GET http://localhost:18080/api/statistics/company?adminId=1&startDate=2026-07-01&endDate=2026-07-31
```

权限：管理员。

业务规则：

- 只能使用当前登录管理员自己的 `adminId`。
- 查询人必须是管理员。
- 统计公司全部审批通过且未删除的工时。

## 13. Apifox 测试建议顺序

建议按下面顺序测试，比较容易排查问题：

1. `GET /api/health/ping`：确认后端启动。
2. `POST /api/auth/login`：管理员登录，保存 token。
3. `GET /api/auth/me`：确认 token 生效。
4. 管理员测试部门、用户、项目管理接口。
5. 部门经理登录，测试用户列表、项目列表、项目授权。
6. 员工登录，测试授权项目查询、工时新增、修改、提交、删除。
7. 部门经理登录，测试待审批列表、审批通过、驳回。
8. 管理员、经理、员工分别测试统计接口。
9. 管理员、经理、员工分别测试工时操作日志接口。

## 14. 常见错误说明

| code | 常见原因 |
| --- | --- |
| 400 | 参数错误、业务规则不满足，例如重复手机号、重复工时、状态不允许流转 |
| 401 | 未登录、token 无效、登录手机号或密码错误 |
| 403 | 权限不足，例如员工访问管理员接口、经理跨部门审批 |
| 404 | 数据不存在，例如用户、部门、项目、工时不存在 |
| 500 | 未预料到的系统异常，需要查看 IDEA 控制台日志 |

