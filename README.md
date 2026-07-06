# 员工工时管理系统

员工工时管理系统是一个基于 Spring Boot、Vue 3 和 MySQL 的前后端分离项目，用于解决企业日常工时填报、审批、授权和统计分散的问题。

系统支持管理员维护基础数据，部门经理维护本部门员工项目授权并审批工时，员工填报和提交本人工时，最终按个人、部门、公司维度进行统计分析，并保留关键操作日志。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 后端 | Java、Spring Boot、MyBatis、Maven |
| 前端 | Vue 3、Vite、Vue Router、Pinia、Axios、Element Plus |
| 数据库 | MySQL |
| 开发工具 | IntelliJ IDEA、Navicat |

## 核心功能

- 登录认证：手机号和密码登录，登录后通过 token 访问接口。
- 基础数据管理：管理员维护部门、用户和项目。
- 项目授权管理：部门经理为本部门员工分配项目。
- 工时管理：员工新增、修改、删除、提交工时申报单。
- 工时审批：部门经理审批通过或驳回本部门员工工时。
- 统计分析：支持个人、部门、公司维度的工时统计。
- 操作日志：记录工时新建、修改、提交、审批通过和驳回。
- 个人中心：查看个人信息，修改姓名、邮箱和登录密码。

## 角色说明

| 角色值 | 角色 | 权限说明 |
| --- | --- | --- |
| 0 | 管理员 | 管理部门、用户、项目，查看公司级统计和全部工时日志 |
| 1 | 部门经理 | 管理本部门项目授权，审批本部门员工工时，查看部门统计和本部门日志 |
| 2 | 员工 | 填报、修改、删除、提交本人工时，查看个人统计和个人日志 |

## 目录结构

```text
work-time-management/
├── backend/       # Spring Boot 后端项目
├── frontend/      # Vue 前端项目
├── database/      # MySQL 建表脚本、初始化数据和迁移脚本
├── work_doc/      # 需求分析和数据库设计文档
├── DESIGN.md      # 前端 UI 风格规范
├── AGENTS.md      # 项目开发规则
└── README.md      # 项目运行说明
```

## 环境要求

- JDK 17 或更高版本
- Maven Wrapper，项目已自带 `mvnw.cmd`
- Node.js 18 或更高版本
- MySQL 8.x
- Navicat 或其他 MySQL 客户端

## 数据库初始化

1. 使用 Navicat 连接本机 MySQL。
2. 执行建表脚本：

```sql
database/schema.sql
```

3. 执行初始化数据脚本：

```sql
database/init-data.sql
```

默认数据库名：

```text
work_time_management
```

## 后端启动

进入后端目录：

```bash
cd backend
```

设置数据库密码环境变量。PowerShell 示例：

```powershell
$env:DB_PASSWORD="你的MySQL密码"
```

启动后端：

```bash
.\mvnw.cmd spring-boot:run
```

后端默认地址：

```text
http://localhost:18080
```

健康检查：

```text
GET http://localhost:18080/api/health/ping
```

## 前端启动

进入前端目录：

```bash
cd frontend
```

安装依赖：

```bash
npm install
```

启动开发服务：

```bash
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

## 测试账号

初始化数据中提供了以下演示账号。

| 角色 | 手机号 | 密码 |
| --- | --- | --- |
| 管理员 | 13800000000 | admin123456 |
| 部门经理 | 13800000001 | manager123456 |
| 部门经理 | 13800000002 | manager123456 |
| 员工 | 13800000003 | employee123456 |
| 员工 | 13800000004 | employee123456 |
| 员工 | 13800000005 | employee123456 |

说明：初始化脚本中的密码是演示数据。项目启动并成功登录后，后端会兼容旧明文密码并逐步更新为 BCrypt 哈希密码。

## 常用接口入口

接口基础地址：

```text
http://localhost:18080/api
```

| 模块 | 接口入口 | 说明 |
| --- | --- | --- |
| 登录 | `/auth/login` | 用户登录 |
| 当前用户 | `/auth/me` | 获取当前登录用户 |
| 个人中心 | `/profile` | 查询和修改个人资料 |
| 部门 | `/departments` | 部门增删改查 |
| 用户 | `/users` | 用户增删改查 |
| 项目 | `/projects` | 项目增删改查 |
| 项目授权 | `/user-projects` | 员工项目授权管理 |
| 工时 | `/work-times` | 工时填报、提交、审批 |
| 工时日志 | `/work-time-logs` | 工时操作日志查询 |
| 统计 | `/statistics` | 个人、部门、公司工时统计 |

除登录和健康检查外，其他接口需要在请求头中携带：

```text
Authorization: Bearer <token>
```

## 构建命令

后端编译：

```bash
cd backend
.\mvnw.cmd -q -DskipTests compile
```

前端构建：

```bash
cd frontend
npm run build
```

## 注意事项

- 数据库密码不要写死在代码中，开发环境通过 `DB_PASSWORD` 环境变量传入。
- 正式部署时应设置 `AUTH_TOKEN_SECRET`，不要使用开发默认值。
- 工时删除采用软删除，历史记录和操作日志会保留。
- 统计分析只统计审批通过且未删除的工时数据。
- 后端会再次校验权限，不能只依赖前端隐藏菜单。

