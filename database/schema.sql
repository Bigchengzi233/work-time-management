-- 员工工时管理系统数据库建表脚本
-- 使用方式：在 Navicat 中连接 MySQL 后，打开并执行本文件。
-- 注意：本脚本会先删除已有同名表，适合开发初期反复重建数据库使用。

-- 创建项目数据库；utf8mb4 可以较好支持中文、英文和常见符号。
CREATE DATABASE IF NOT EXISTS `work_time_management`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 进入项目数据库，后面的建表语句都会在这个数据库中执行。
USE `work_time_management`;

-- 为了方便开发阶段重复执行脚本，先按外键依赖关系从子表到主表删除旧表。
DROP TABLE IF EXISTS `work_time_log`;
DROP TABLE IF EXISTS `work_time_apply`;
DROP TABLE IF EXISTS `user_project`;
DROP TABLE IF EXISTS `project`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `department`;

-- 部门表：保存企业部门基础信息。
CREATE TABLE `department` (
    `dept_id` INT NOT NULL AUTO_INCREMENT COMMENT '部门编号，主键，自增',
    `dept_name` VARCHAR(50) NOT NULL COMMENT '部门名称',
    PRIMARY KEY (`dept_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '部门表';

-- 用户表：保存管理员、部门经理和普通员工的账号信息。
CREATE TABLE `user` (
    `user_id` INT NOT NULL AUTO_INCREMENT COMMENT '用户编号，主键，自增',
    `user_name` VARCHAR(50) NOT NULL COMMENT '用户姓名',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号，用于登录，必须唯一',
    `psw` VARCHAR(100) NOT NULL COMMENT '密码，后端实际保存时应保存哈希值，不保存明文',
    `email` VARCHAR(100) NULL COMMENT '邮箱',
    `user_role` VARCHAR(20) NOT NULL COMMENT '用户角色：0管理员，1部门经理，2员工',
    `dept_id` INT NOT NULL COMMENT '所属部门编号，关联 department.dept_id',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_user_phone` (`phone`),
    KEY `idx_user_dept_id` (`dept_id`),
    CONSTRAINT `fk_user_department`
        FOREIGN KEY (`dept_id`)
        REFERENCES `department` (`dept_id`)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '用户表';

-- 项目表：保存项目基础信息，并记录项目所属部门。
CREATE TABLE `project` (
    `project_id` INT NOT NULL AUTO_INCREMENT COMMENT '项目编号，主键，自增',
    `project_name` VARCHAR(100) NOT NULL COMMENT '项目名称',
    `project_status` INT NOT NULL COMMENT '项目状态：0禁用，1启用',
    `dept_id` INT NOT NULL COMMENT '所属部门编号，关联 department.dept_id',
    PRIMARY KEY (`project_id`),
    KEY `idx_project_dept_id` (`dept_id`),
    CONSTRAINT `fk_project_department`
        FOREIGN KEY (`dept_id`)
        REFERENCES `department` (`dept_id`)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT `ck_project_status`
        CHECK (`project_status` IN (0, 1))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '项目表';

-- 用户项目参与表：保存员工被授权参与哪些项目。
CREATE TABLE `user_project` (
    `auth_id` INT NOT NULL AUTO_INCREMENT COMMENT '授权编号，主键，自增',
    `user_id` INT NOT NULL COMMENT '用户编号，关联 user.user_id',
    `project_id` INT NOT NULL COMMENT '项目编号，关联 project.project_id',
    `auth_time` DATETIME NOT NULL COMMENT '授权时间',
    `auth_status` INT NOT NULL COMMENT '授权状态：0取消授权，1有效授权',
    PRIMARY KEY (`auth_id`),
    UNIQUE KEY `uk_user_project` (`user_id`, `project_id`),
    KEY `idx_user_project_project_id` (`project_id`),
    CONSTRAINT `fk_user_project_user`
        FOREIGN KEY (`user_id`)
        REFERENCES `user` (`user_id`)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT `fk_user_project_project`
        FOREIGN KEY (`project_id`)
        REFERENCES `project` (`project_id`)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT `ck_auth_status`
        CHECK (`auth_status` IN (0, 1))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '用户项目参与表';

-- 工时申报单表：保存员工每天在项目上的工时申报数据。
CREATE TABLE `work_time_apply` (
    `work_id` INT NOT NULL AUTO_INCREMENT COMMENT '工时编号，主键，自增',
    `user_id` INT NOT NULL COMMENT '用户编号，关联 user.user_id',
    `project_id` INT NOT NULL COMMENT '项目编号，关联 project.project_id',
    `work_date` DATE NOT NULL COMMENT '工作日期，不能晚于当前日期',
    `work_hours` DECIMAL(4, 1) NOT NULL COMMENT '工时数，范围 0.5 到 24，按 0.5 小时粒度填写',
    `work_desc` VARCHAR(500) NULL COMMENT '工作描述',
    `status` INT NOT NULL COMMENT '工时状态：0草稿，1待审批，2审批通过，3已驳回',
    `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
    PRIMARY KEY (`work_id`),
    KEY `idx_work_time_user_project_date` (`user_id`, `project_id`, `work_date`),
    KEY `idx_work_time_project_id` (`project_id`),
    KEY `idx_work_time_status` (`status`),
    CONSTRAINT `fk_work_time_user`
        FOREIGN KEY (`user_id`)
        REFERENCES `user` (`user_id`)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT `fk_work_time_project`
        FOREIGN KEY (`project_id`)
        REFERENCES `project` (`project_id`)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT `ck_work_time_status`
        CHECK (`status` IN (0, 1, 2, 3)),
    CONSTRAINT `ck_work_time_is_deleted`
        CHECK (`is_deleted` IN (0, 1)),
    CONSTRAINT `ck_work_hours_range`
        CHECK (`work_hours` >= 0.5 AND `work_hours` <= 24.0),
    CONSTRAINT `ck_work_hours_step`
        CHECK (MOD(`work_hours` * 10, 5) = 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '工时申报单表';

-- 工时操作日志表：记录工时申报单的新建、修改、提交、审批通过和驳回。
CREATE TABLE `work_time_log` (
    `log_id` INT NOT NULL AUTO_INCREMENT COMMENT '日志编号，主键，自增',
    `work_id` INT NOT NULL COMMENT '工时编号，关联 work_time_apply.work_id',
    `operation_type` INT NOT NULL COMMENT '操作类型：0新建，1修改，2提交，3审批通过，4驳回',
    `operation_time` DATETIME NOT NULL COMMENT '操作时间',
    `operator_id` INT NOT NULL COMMENT '操作人编号，关联 user.user_id',
    `operation_desc` VARCHAR(500) NULL COMMENT '操作说明，例如驳回原因或修改说明',
    PRIMARY KEY (`log_id`),
    KEY `idx_work_time_log_work_id` (`work_id`),
    KEY `idx_work_time_log_operator_id` (`operator_id`),
    CONSTRAINT `fk_work_time_log_apply`
        FOREIGN KEY (`work_id`)
        REFERENCES `work_time_apply` (`work_id`)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT `fk_work_time_log_operator`
        FOREIGN KEY (`operator_id`)
        REFERENCES `user` (`user_id`)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT `ck_operation_type`
        CHECK (`operation_type` IN (0, 1, 2, 3, 4))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '工时操作日志表';
