-- 员工工时管理系统初始化数据脚本
-- 使用方式：先执行 database/schema.sql，再执行本文件。
-- 说明：这里的密码暂用演示字符串，后端登录功能完成后应改为哈希后的密码。

-- 进入项目数据库。
USE `work_time_management`;

-- 初始化部门数据。
INSERT INTO `department` (`dept_id`, `dept_name`) VALUES
    (1, 'Web系统研发部'),
    (2, '移动端研发部'),
    (3, 'Windows应用研发部'),
    (4, '综合管理部');

-- 初始化用户数据。
-- user_role：0管理员，1部门经理，2员工。
INSERT INTO `user` (`user_id`, `user_name`, `phone`, `psw`, `email`, `user_role`, `dept_id`) VALUES
    (1, '系统管理员', '13800000000', 'admin123456', 'admin@example.com', '0', 4),
    (2, '王经理', '13800000001', 'manager123456', 'wang.manager@example.com', '1', 1),
    (3, '李经理', '13800000002', 'manager123456', 'li.manager@example.com', '1', 2),
    (4, '张三', '13800000003', 'employee123456', 'zhangsan@example.com', '2', 1),
    (5, '李四', '13800000004', 'employee123456', 'lisi@example.com', '2', 1),
    (6, '赵六', '13800000005', 'employee123456', 'zhaoliu@example.com', '2', 2);

-- 初始化项目数据。
-- project_status：0禁用，1启用。
INSERT INTO `project` (`project_id`, `project_name`, `project_status`, `dept_id`) VALUES
    (1, '工时管理系统', 1, 1),
    (2, '客户关系管理系统', 1, 1),
    (3, '移动审批App', 1, 2),
    (4, '旧版报表系统维护', 0, 1);

-- 初始化员工项目授权数据。
-- auth_status：0取消授权，1有效授权。
INSERT INTO `user_project` (`auth_id`, `user_id`, `project_id`, `auth_time`, `auth_status`) VALUES
    (1, 4, 1, '2026-07-01 09:00:00', 1),
    (2, 4, 2, '2026-07-01 09:10:00', 1),
    (3, 5, 1, '2026-07-01 09:20:00', 1),
    (4, 6, 3, '2026-07-01 09:30:00', 1);

-- 初始化工时申报单数据。
-- status：0草稿，1待审批，2审批通过，3已驳回。
INSERT INTO `work_time_apply` (`work_id`, `user_id`, `project_id`, `work_date`, `work_hours`, `work_desc`, `status`) VALUES
    (1, 4, 1, '2026-07-01', 8.0, '完成登录页面和接口字段整理', 2),
    (2, 4, 2, '2026-07-02', 4.0, '整理客户管理模块需求', 1),
    (3, 5, 1, '2026-07-01', 7.5, '编写部门管理页面原型', 0),
    (4, 6, 3, '2026-07-01', 6.0, '调研移动审批流程', 2);

-- 初始化工时操作日志数据。
-- operation_type：0新建，1修改，2提交，3审批通过，4驳回。
INSERT INTO `work_time_log` (`log_id`, `work_id`, `operation_type`, `operation_time`, `operator_id`, `operation_desc`) VALUES
    (1, 1, 0, '2026-07-01 18:00:00', 4, '员工新建工时申报单'),
    (2, 1, 2, '2026-07-01 18:05:00', 4, '员工提交审批'),
    (3, 1, 3, '2026-07-02 09:00:00', 2, '部门经理审批通过'),
    (4, 2, 0, '2026-07-02 18:00:00', 4, '员工新建工时申报单'),
    (5, 2, 2, '2026-07-02 18:10:00', 4, '员工提交审批'),
    (6, 3, 0, '2026-07-01 18:20:00', 5, '员工新建草稿'),
    (7, 4, 0, '2026-07-01 18:30:00', 6, '员工新建工时申报单'),
    (8, 4, 2, '2026-07-01 18:35:00', 6, '员工提交审批'),
    (9, 4, 3, '2026-07-02 09:20:00', 3, '部门经理审批通过');
