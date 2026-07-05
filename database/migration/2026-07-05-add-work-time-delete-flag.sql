-- 为工时申报单增加软删除标记
-- 使用方式：在 Navicat 中选择 work_time_management 数据库后执行本文件。
-- 目的：删除工时时不物理删除记录，避免工时操作日志外键断开。

USE `work_time_management`;

-- 1. 增加删除标记：0 未删除，1 已删除。
ALTER TABLE `work_time_apply`
    ADD COLUMN `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除' AFTER `status`;

-- 2. 先给 user_id 单独增加索引。
-- 原唯一索引 uk_work_time_user_project_date 的第一列是 user_id，当前也被 user_id 外键依赖。
-- 所以必须先补一个 user_id 普通索引，再删除原唯一索引。
ALTER TABLE `work_time_apply`
    ADD INDEX `idx_work_time_user_id` (`user_id`);

-- 3. 删除原来的唯一索引。
-- 原唯一索引会阻止“软删除后重新填报同一天同项目工时”，所以改为普通索引，唯一性由后端业务校验保证。
ALTER TABLE `work_time_apply`
    DROP INDEX `uk_work_time_user_project_date`;

-- 4. 增加普通索引，继续提升按用户、项目、日期查询的效率。
ALTER TABLE `work_time_apply`
    ADD INDEX `idx_work_time_user_project_date` (`user_id`, `project_id`, `work_date`);

-- 5. 增加删除标记取值约束。
ALTER TABLE `work_time_apply`
    ADD CONSTRAINT `ck_work_time_is_deleted`
        CHECK (`is_deleted` IN (0, 1));
