-- 员工工时管理系统管理员部门清理脚本
-- 使用方式：在 Navicat 中连接 MySQL 后，选择 work_time_management 数据库并执行本文件。
-- 业务目标：
-- 1. 全公司只保留 1 个管理员账号。
-- 2. 管理员所在的“综合管理部”只保留管理员，不保留其他经理或员工。
-- 3. 删除综合管理部其他人员时，先删除他们关联的工时日志、工时申报、项目授权，避免外键约束报错。
-- 4. 保留 department 表中的“综合管理部”这个部门，不删除部门本身。
--
-- 执行前建议：
-- 1. 先备份数据库。
-- 2. 确认当前库名是 work_time_management。
-- 3. 如果你刚执行过随机测试数据脚本，本脚本可以继续执行。

USE `work_time_management`;

START TRANSACTION;

-- 1. 找到“综合管理部”的部门编号。
SET @admin_dept_id = (
    SELECT `dept_id`
    FROM `department`
    WHERE `dept_name` = '综合管理部'
    ORDER BY `dept_id`
    LIMIT 1
);

-- 2. 确定唯一保留的管理员。
-- 优先保留综合管理部中的管理员；如果存在多个管理员，则保留 user_id 最小的那个。
SET @keeper_admin_id = (
    SELECT `user_id`
    FROM `user`
    WHERE `user_role` = '0'
      AND `dept_id` = @admin_dept_id
    ORDER BY `user_id`
    LIMIT 1
);

-- 如果综合管理部里没有管理员，则退一步保留系统中 user_id 最小的管理员。
SET @keeper_admin_id = IFNULL(
    @keeper_admin_id,
    (
        SELECT `user_id`
        FROM `user`
        WHERE `user_role` = '0'
        ORDER BY `user_id`
        LIMIT 1
    )
);

-- 3. 临时表：记录本次要删除的用户。
-- 删除范围：
-- 1）综合管理部中除了保留管理员之外的所有用户。
-- 2）系统中除了保留管理员之外的其他管理员，保证全公司只有一个管理员。
DROP TEMPORARY TABLE IF EXISTS `tmp_users_to_delete`;
CREATE TEMPORARY TABLE `tmp_users_to_delete` (
    `user_id` INT NOT NULL PRIMARY KEY
) ENGINE = MEMORY;

INSERT IGNORE INTO `tmp_users_to_delete` (`user_id`)
SELECT `user_id`
FROM `user`
WHERE `dept_id` = @admin_dept_id
  AND `user_id` <> @keeper_admin_id;

INSERT IGNORE INTO `tmp_users_to_delete` (`user_id`)
SELECT `user_id`
FROM `user`
WHERE `user_role` = '0'
  AND `user_id` <> @keeper_admin_id;

-- 4. 临时表：记录综合管理部下的项目。
-- 管理员部门不再保留业务项目，所以综合管理部下的项目也一并清理。
DROP TEMPORARY TABLE IF EXISTS `tmp_projects_to_delete`;
CREATE TEMPORARY TABLE `tmp_projects_to_delete` (
    `project_id` INT NOT NULL PRIMARY KEY
) ENGINE = MEMORY;

INSERT IGNORE INTO `tmp_projects_to_delete` (`project_id`)
SELECT `project_id`
FROM `project`
WHERE `dept_id` = @admin_dept_id;

-- 5. 临时表：记录本次会被删除的工时申报单。
-- 只要工时属于要删除的用户，或者属于综合管理部要删除的项目，就需要清理。
DROP TEMPORARY TABLE IF EXISTS `tmp_work_times_to_delete`;
CREATE TEMPORARY TABLE `tmp_work_times_to_delete` (
    `work_id` INT NOT NULL PRIMARY KEY
) ENGINE = MEMORY;

INSERT IGNORE INTO `tmp_work_times_to_delete` (`work_id`)
SELECT `work_id`
FROM `work_time_apply`
WHERE `user_id` IN (SELECT `user_id` FROM `tmp_users_to_delete`)
   OR `project_id` IN (SELECT `project_id` FROM `tmp_projects_to_delete`);

-- 6. 先删除工时操作日志。
-- 日志表同时关联 work_id 和 operator_id，所以要先清理日志，后面才能删除工时和用户。
DELETE FROM `work_time_log`
WHERE `work_id` IN (SELECT `work_id` FROM `tmp_work_times_to_delete`)
   OR `operator_id` IN (SELECT `user_id` FROM `tmp_users_to_delete`);

-- 7. 删除工时申报单。
DELETE FROM `work_time_apply`
WHERE `work_id` IN (SELECT `work_id` FROM `tmp_work_times_to_delete`);

-- 8. 删除项目授权关系。
DELETE FROM `user_project`
WHERE `user_id` IN (SELECT `user_id` FROM `tmp_users_to_delete`)
   OR `project_id` IN (SELECT `project_id` FROM `tmp_projects_to_delete`);

-- 9. 删除综合管理部下的项目。
DELETE FROM `project`
WHERE `project_id` IN (SELECT `project_id` FROM `tmp_projects_to_delete`);

-- 10. 删除综合管理部其他用户，以及多余管理员。
DELETE FROM `user`
WHERE `user_id` IN (SELECT `user_id` FROM `tmp_users_to_delete`);

COMMIT;

-- 11. 执行后检查结果。
-- 结果 1：全公司管理员数量应该为 1。
SELECT COUNT(*) AS admin_count
FROM `user`
WHERE `user_role` = '0';

-- 结果 2：综合管理部应该只剩管理员。
SELECT u.`user_id`,
       u.`user_name`,
       u.`phone`,
       u.`user_role`,
       d.`dept_name`
FROM `user` u
JOIN `department` d ON d.`dept_id` = u.`dept_id`
WHERE d.`dept_name` = '综合管理部'
ORDER BY u.`user_id`;

-- 结果 3：综合管理部下应该不再有项目。
SELECT COUNT(*) AS admin_dept_project_count
FROM `project` p
JOIN `department` d ON d.`dept_id` = p.`dept_id`
WHERE d.`dept_name` = '综合管理部';
