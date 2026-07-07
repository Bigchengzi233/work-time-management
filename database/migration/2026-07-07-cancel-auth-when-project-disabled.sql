-- 禁用项目自动取消授权的历史数据修复脚本
-- 使用方式：在 Navicat 中连接 MySQL 后，选择 work_time_management 数据库并执行本文件。
-- 业务规则：
-- 项目状态为禁用时，该项目不应该继续存在“有效授权”。
-- 因此把所有禁用项目下 auth_status = 1 的授权记录统一改为 auth_status = 0。

USE `work_time_management`;

START TRANSACTION;

UPDATE `user_project` up
INNER JOIN `project` p ON p.`project_id` = up.`project_id`
SET up.`auth_status` = 0
WHERE p.`project_status` = 0
  AND up.`auth_status` = 1;

COMMIT;

-- 执行后检查：结果应该为 0。
SELECT COUNT(*) AS enabled_auth_on_disabled_project_count
FROM `user_project` up
INNER JOIN `project` p ON p.`project_id` = up.`project_id`
WHERE p.`project_status` = 0
  AND up.`auth_status` = 1;
