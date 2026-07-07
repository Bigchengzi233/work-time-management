-- 员工工时管理系统随机测试数据脚本
-- 使用方式：在 Navicat 中连接 MySQL 后，选择 work_time_management 数据库并执行本文件。
-- 说明：本脚本用于补充更接近真实业务的测试数据，方便前端页面、接口和统计功能展示。
-- 数据内容：新增 4 个部门、16 个项目、50 个随机用户、项目授权、工时申报单和工时操作日志。
-- 注意：50 个新增用户的初始密码统一为 123456。

USE `work_time_management`;

START TRANSACTION;

-- 1. 补充部门数据。
-- 已存在同名部门时不会重复插入，避免多次执行脚本导致部门重复。
INSERT INTO `department` (`dept_name`)
SELECT 'Web系统研发部'
WHERE NOT EXISTS (SELECT 1 FROM `department` WHERE `dept_name` = 'Web系统研发部');

INSERT INTO `department` (`dept_name`)
SELECT '移动端研发部'
WHERE NOT EXISTS (SELECT 1 FROM `department` WHERE `dept_name` = '移动端研发部');

INSERT INTO `department` (`dept_name`)
SELECT 'Windows应用研发部'
WHERE NOT EXISTS (SELECT 1 FROM `department` WHERE `dept_name` = 'Windows应用研发部');

INSERT INTO `department` (`dept_name`)
SELECT '综合管理部'
WHERE NOT EXISTS (SELECT 1 FROM `department` WHERE `dept_name` = '综合管理部');

INSERT INTO `department` (`dept_name`)
SELECT '产品体验部'
WHERE NOT EXISTS (SELECT 1 FROM `department` WHERE `dept_name` = '产品体验部');

INSERT INTO `department` (`dept_name`)
SELECT '数据平台部'
WHERE NOT EXISTS (SELECT 1 FROM `department` WHERE `dept_name` = '数据平台部');

INSERT INTO `department` (`dept_name`)
SELECT '测试质量部'
WHERE NOT EXISTS (SELECT 1 FROM `department` WHERE `dept_name` = '测试质量部');

INSERT INTO `department` (`dept_name`)
SELECT '客户成功部'
WHERE NOT EXISTS (SELECT 1 FROM `department` WHERE `dept_name` = '客户成功部');

-- 2. 保存部门编号到变量，后续新增用户和项目时直接引用。
SET @dept_web = (SELECT `dept_id` FROM `department` WHERE `dept_name` = 'Web系统研发部' ORDER BY `dept_id` LIMIT 1);
SET @dept_mobile = (SELECT `dept_id` FROM `department` WHERE `dept_name` = '移动端研发部' ORDER BY `dept_id` LIMIT 1);
SET @dept_windows = (SELECT `dept_id` FROM `department` WHERE `dept_name` = 'Windows应用研发部' ORDER BY `dept_id` LIMIT 1);
SET @dept_admin = (SELECT `dept_id` FROM `department` WHERE `dept_name` = '综合管理部' ORDER BY `dept_id` LIMIT 1);
SET @dept_product = (SELECT `dept_id` FROM `department` WHERE `dept_name` = '产品体验部' ORDER BY `dept_id` LIMIT 1);
SET @dept_data = (SELECT `dept_id` FROM `department` WHERE `dept_name` = '数据平台部' ORDER BY `dept_id` LIMIT 1);
SET @dept_quality = (SELECT `dept_id` FROM `department` WHERE `dept_name` = '测试质量部' ORDER BY `dept_id` LIMIT 1);
SET @dept_success = (SELECT `dept_id` FROM `department` WHERE `dept_name` = '客户成功部' ORDER BY `dept_id` LIMIT 1);

-- 3. 新增 50 个随机测试用户。
-- user_role：1 表示部门经理，2 表示员工。
-- phone 字段有唯一索引，所以这里使用 INSERT IGNORE，重复执行时不会重复插入同手机号用户。
INSERT IGNORE INTO `user` (`user_name`, `phone`, `psw`, `email`, `user_role`, `dept_id`) VALUES
    ('邵景川', '17638492051', '123456', 'jingchuan.shao@worktime.demo', '1', @dept_web),
    ('方予辰', '15294738106', '123456', 'yuchen.fang@worktime.demo', '2', @dept_web),
    ('许清越', '18956302741', '123456', 'qingyue.xu@worktime.demo', '2', @dept_web),
    ('唐沐阳', '17042865983', '123456', 'muyang.tang@worktime.demo', '2', @dept_web),
    ('韩以宁', '19970631452', '123456', 'yining.han@worktime.demo', '2', @dept_web),
    ('郭书禾', '16659284017', '123456', 'shuhe.guo@worktime.demo', '2', @dept_web),
    ('叶嘉树', '13580724691', '123456', 'jiashu.ye@worktime.demo', '2', @dept_web),
    ('林若溪', '18163495072', '123456', 'ruoxi.lin@worktime.demo', '1', @dept_mobile),
    ('苏念安', '15729368405', '123456', 'nianan.su@worktime.demo', '2', @dept_mobile),
    ('周云澈', '19274051836', '123456', 'yunche.zhou@worktime.demo', '2', @dept_mobile),
    ('白若晨', '17365829041', '123456', 'ruochen.bai@worktime.demo', '2', @dept_mobile),
    ('乔安禾', '14790632518', '123456', 'anhe.qiao@worktime.demo', '2', @dept_mobile),
    ('罗景行', '18642075963', '123456', 'jingxing.luo@worktime.demo', '2', @dept_mobile),
    ('沈知远', '15978304126', '123456', 'zhiyuan.shen@worktime.demo', '1', @dept_windows),
    ('蒋南星', '19825460731', '123456', 'nanxing.jiang@worktime.demo', '2', @dept_windows),
    ('陆修远', '15069283704', '123456', 'xiuyuan.lu@worktime.demo', '2', @dept_windows),
    ('江映雪', '17730584692', '123456', 'yingxue.jiang@worktime.demo', '2', @dept_windows),
    ('余子墨', '13691827450', '123456', 'zimo.yu@worktime.demo', '2', @dept_windows),
    ('丁亦航', '19162740583', '123456', 'yihang.ding@worktime.demo', '2', @dept_windows),
    ('何星澜', '16573908214', '123456', 'xinglan.he@worktime.demo', '1', @dept_admin),
    ('孟梓涵', '18024691357', '123456', 'zihan.meng@worktime.demo', '2', @dept_admin),
    ('魏清舒', '15860739241', '123456', 'qingshu.wei@worktime.demo', '2', @dept_admin),
    ('薛明哲', '19358472016', '123456', 'mingzhe.xue@worktime.demo', '2', @dept_admin),
    ('田语桐', '17182936504', '123456', 'yutong.tian@worktime.demo', '2', @dept_admin),
    ('邱梓航', '18875301962', '123456', 'zihang.qiu@worktime.demo', '1', @dept_product),
    ('梁思源', '15542690837', '123456', 'siyuan.liang@worktime.demo', '2', @dept_product),
    ('宋知夏', '19630758421', '123456', 'zhixia.song@worktime.demo', '2', @dept_product),
    ('姚安然', '14867523019', '123456', 'anran.yao@worktime.demo', '2', @dept_product),
    ('康书言', '18250947631', '123456', 'shuyan.kang@worktime.demo', '2', @dept_product),
    ('段景宁', '13786420591', '123456', 'jingning.duan@worktime.demo', '2', @dept_product),
    ('施予墨', '19031572864', '123456', 'yumo.shi@worktime.demo', '2', @dept_product),
    ('程予安', '16780249315', '123456', 'yuan.cheng@worktime.demo', '1', @dept_data),
    ('秦沐白', '18569472038', '123456', 'mubai.qin@worktime.demo', '2', @dept_data),
    ('贺星野', '15623894701', '123456', 'xingye.he@worktime.demo', '2', @dept_data),
    ('尹舒然', '19745068123', '123456', 'shuran.yin@worktime.demo', '2', @dept_data),
    ('高云舟', '15180736492', '123456', 'yunzhou.gao@worktime.demo', '2', @dept_data),
    ('安若琳', '17932680541', '123456', 'ruolin.an@worktime.demo', '2', @dept_data),
    ('毛景曜', '13495726018', '123456', 'jingyao.mao@worktime.demo', '2', @dept_data),
    ('袁景和', '19468157320', '123456', 'jinghe.yuan@worktime.demo', '1', @dept_quality),
    ('钟启明', '17250863941', '123456', 'qiming.zhong@worktime.demo', '2', @dept_quality),
    ('夏芷晴', '18376049215', '123456', 'zhiqing.xia@worktime.demo', '2', @dept_quality),
    ('赖言蹊', '16029485736', '123456', 'yanxi.lai@worktime.demo', '2', @dept_quality),
    ('傅一诺', '19583716042', '123456', 'yinuo.fu@worktime.demo', '2', @dept_quality),
    ('石景辰', '15360928471', '123456', 'jingchen.shi@worktime.demo', '2', @dept_quality),
    ('夏明棠', '18742063519', '123456', 'mingtang.xia@worktime.demo', '1', @dept_success),
    ('温若谷', '16897352046', '123456', 'ruogu.wen@worktime.demo', '2', @dept_success),
    ('何知许', '18456297013', '123456', 'zhixu.he@worktime.demo', '2', @dept_success),
    ('邓晨曦', '16173840592', '123456', 'chenxi.deng@worktime.demo', '2', @dept_success),
    ('胡舒意', '17529068134', '123456', 'shuyi.hu@worktime.demo', '2', @dept_success),
    ('彭予川', '14936057281', '123456', 'yuchuan.peng@worktime.demo', '2', @dept_success);

-- 4. 新增项目数据。
-- 项目分散到多个部门，让项目管理、授权管理、工时填报和统计页面都有数据可展示。
INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '前端组件库升级', 1, @dept_web
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '前端组件库升级');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '低代码工时配置台', 1, @dept_web
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '低代码工时配置台');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '移动巡检助手', 1, @dept_mobile
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '移动巡检助手');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '移动消息推送平台', 1, @dept_mobile
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '移动消息推送平台');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '桌面客户端重构', 1, @dept_windows
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '桌面客户端重构');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '离线数据同步工具', 1, @dept_windows
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '离线数据同步工具');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '合同审批流程', 1, @dept_admin
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '合同审批流程');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '绩效数据看板', 1, @dept_admin
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '绩效数据看板');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '需求池管理系统', 1, @dept_product
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '需求池管理系统');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '用户体验反馈平台', 1, @dept_product
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '用户体验反馈平台');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '数据指标中台', 1, @dept_data
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '数据指标中台');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '报表数据治理平台', 1, @dept_data
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '报表数据治理平台');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '自动化测试平台', 1, @dept_quality
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '自动化测试平台');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '缺陷追踪平台', 1, @dept_quality
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '缺陷追踪平台');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '客户服务知识库', 1, @dept_success
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '客户服务知识库');

INSERT INTO `project` (`project_name`, `project_status`, `dept_id`)
SELECT '客户工单中心', 1, @dept_success
WHERE NOT EXISTS (SELECT 1 FROM `project` WHERE `project_name` = '客户工单中心');

-- 5. 给员工分配项目授权。
-- INSERT IGNORE 会利用 user_project(user_id, project_id) 唯一索引避免重复授权。
INSERT IGNORE INTO `user_project` (`user_id`, `project_id`, `auth_time`, `auth_status`)
SELECT u.`user_id`, p.`project_id`, a.`auth_time`, a.`auth_status`
FROM (
    SELECT '15294738106' AS phone, '前端组件库升级' AS project_name, '2026-06-17 09:14:00' AS auth_time, 1 AS auth_status
    UNION ALL SELECT '18956302741', '低代码工时配置台', '2026-06-17 09:28:00', 1
    UNION ALL SELECT '17042865983', '前端组件库升级', '2026-06-17 09:43:00', 1
    UNION ALL SELECT '19970631452', '低代码工时配置台', '2026-06-17 10:06:00', 1
    UNION ALL SELECT '16659284017', '前端组件库升级', '2026-06-17 10:17:00', 1
    UNION ALL SELECT '13580724691', '低代码工时配置台', '2026-06-17 10:34:00', 1
    UNION ALL SELECT '15729368405', '移动巡检助手', '2026-06-17 11:08:00', 1
    UNION ALL SELECT '19274051836', '移动消息推送平台', '2026-06-17 11:21:00', 1
    UNION ALL SELECT '17365829041', '移动巡检助手', '2026-06-17 11:39:00', 1
    UNION ALL SELECT '14790632518', '移动消息推送平台', '2026-06-17 13:14:00', 1
    UNION ALL SELECT '18642075963', '移动巡检助手', '2026-06-17 13:37:00', 1
    UNION ALL SELECT '19825460731', '桌面客户端重构', '2026-06-17 14:12:00', 1
    UNION ALL SELECT '15069283704', '离线数据同步工具', '2026-06-17 14:25:00', 1
    UNION ALL SELECT '17730584692', '桌面客户端重构', '2026-06-17 14:51:00', 1
    UNION ALL SELECT '13691827450', '离线数据同步工具', '2026-06-17 15:05:00', 1
    UNION ALL SELECT '19162740583', '桌面客户端重构', '2026-06-17 15:22:00', 1
    UNION ALL SELECT '18024691357', '合同审批流程', '2026-06-17 15:48:00', 1
    UNION ALL SELECT '15860739241', '绩效数据看板', '2026-06-17 16:10:00', 1
    UNION ALL SELECT '19358472016', '合同审批流程', '2026-06-17 16:33:00', 1
    UNION ALL SELECT '17182936504', '绩效数据看板', '2026-06-17 16:55:00', 1
    UNION ALL SELECT '15542690837', '需求池管理系统', '2026-06-18 09:12:00', 1
    UNION ALL SELECT '19630758421', '用户体验反馈平台', '2026-06-18 09:27:00', 1
    UNION ALL SELECT '14867523019', '需求池管理系统', '2026-06-18 09:49:00', 1
    UNION ALL SELECT '18250947631', '用户体验反馈平台', '2026-06-18 10:02:00', 1
    UNION ALL SELECT '13786420591', '需求池管理系统', '2026-06-18 10:31:00', 1
    UNION ALL SELECT '19031572864', '用户体验反馈平台', '2026-06-18 10:47:00', 1
    UNION ALL SELECT '18569472038', '数据指标中台', '2026-06-18 11:03:00', 1
    UNION ALL SELECT '15623894701', '报表数据治理平台', '2026-06-18 11:24:00', 1
    UNION ALL SELECT '19745068123', '数据指标中台', '2026-06-18 11:42:00', 1
    UNION ALL SELECT '15180736492', '报表数据治理平台', '2026-06-18 13:15:00', 1
    UNION ALL SELECT '17932680541', '数据指标中台', '2026-06-18 13:38:00', 1
    UNION ALL SELECT '13495726018', '报表数据治理平台', '2026-06-18 14:01:00', 1
    UNION ALL SELECT '17250863941', '自动化测试平台', '2026-06-18 14:26:00', 1
    UNION ALL SELECT '18376049215', '缺陷追踪平台', '2026-06-18 14:52:00', 1
    UNION ALL SELECT '16029485736', '自动化测试平台', '2026-06-18 15:11:00', 1
    UNION ALL SELECT '19583716042', '缺陷追踪平台', '2026-06-18 15:36:00', 1
    UNION ALL SELECT '15360928471', '自动化测试平台', '2026-06-18 16:07:00', 1
    UNION ALL SELECT '16897352046', '客户服务知识库', '2026-06-18 16:28:00', 1
    UNION ALL SELECT '18456297013', '客户工单中心', '2026-06-18 16:44:00', 1
    UNION ALL SELECT '16173840592', '客户服务知识库', '2026-06-18 17:02:00', 1
    UNION ALL SELECT '17529068134', '客户工单中心', '2026-06-18 17:23:00', 1
    UNION ALL SELECT '14936057281', '客户服务知识库', '2026-06-18 17:41:00', 1
    UNION ALL SELECT '15294738106', '低代码工时配置台', '2026-06-20 09:25:00', 1
    UNION ALL SELECT '15729368405', '移动消息推送平台', '2026-06-20 10:10:00', 1
    UNION ALL SELECT '19825460731', '离线数据同步工具', '2026-06-20 10:44:00', 1
    UNION ALL SELECT '15542690837', '用户体验反馈平台', '2026-06-20 11:18:00', 1
    UNION ALL SELECT '18569472038', '报表数据治理平台', '2026-06-20 13:34:00', 1
    UNION ALL SELECT '17250863941', '缺陷追踪平台', '2026-06-20 14:06:00', 1
    UNION ALL SELECT '16897352046', '客户工单中心', '2026-06-20 15:29:00', 1
    UNION ALL SELECT '16659284017', '低代码工时配置台', '2026-06-21 09:37:00', 0
    UNION ALL SELECT '19358472016', '绩效数据看板', '2026-06-21 10:12:00', 0
    UNION ALL SELECT '19031572864', '需求池管理系统', '2026-06-21 11:08:00', 0
    UNION ALL SELECT '15360928471', '缺陷追踪平台', '2026-06-21 13:46:00', 0
) a
JOIN `user` u ON u.`phone` = a.`phone`
JOIN `project` p ON p.`project_name` = a.`project_name`;

-- 6. 新增 50 条工时申报单。
-- 状态分布：草稿、待审批、审批通过、已驳回都覆盖，方便测试不同页面和统计结果。
INSERT INTO `work_time_apply` (`user_id`, `project_id`, `work_date`, `work_hours`, `work_desc`, `status`, `is_deleted`)
SELECT u.`user_id`, p.`project_id`, w.`work_date`, w.`work_hours`, w.`work_desc`, w.`status`, 0
FROM (
    SELECT '15294738106' AS phone, '前端组件库升级' AS project_name, '2026-06-24' AS work_date, 7.5 AS work_hours, '整理表格组件的筛选交互和边界状态' AS work_desc, 2 AS status
    UNION ALL SELECT '18956302741', '低代码工时配置台', '2026-06-25', 6.0, '实现字段配置页的表单联动逻辑', 1
    UNION ALL SELECT '17042865983', '前端组件库升级', '2026-06-26', 8.0, '优化弹窗表单的校验提示样式', 0
    UNION ALL SELECT '19970631452', '低代码工时配置台', '2026-06-27', 5.5, '排查配置保存接口的异常提示问题', 3
    UNION ALL SELECT '16659284017', '前端组件库升级', '2026-06-28', 7.0, '补充列表页空数据状态和加载状态', 2
    UNION ALL SELECT '13580724691', '低代码工时配置台', '2026-06-29', 4.5, '整理低代码配置模块的测试记录', 1
    UNION ALL SELECT '15729368405', '移动巡检助手', '2026-06-24', 6.5, '完成巡检任务列表接口联调', 2
    UNION ALL SELECT '19274051836', '移动消息推送平台', '2026-06-25', 7.5, '设计消息模板管理页面的数据结构', 0
    UNION ALL SELECT '17365829041', '移动巡检助手', '2026-06-26', 5.0, '修复离线巡检数据同步失败问题', 3
    UNION ALL SELECT '14790632518', '移动消息推送平台', '2026-06-27', 8.0, '完成推送记录查询接口对接', 2
    UNION ALL SELECT '18642075963', '移动巡检助手', '2026-06-28', 6.0, '补充巡检详情页异常场景处理', 1
    UNION ALL SELECT '19825460731', '桌面客户端重构', '2026-06-24', 7.0, '重构客户端主窗口菜单加载逻辑', 2
    UNION ALL SELECT '15069283704', '离线数据同步工具', '2026-06-25', 5.5, '编写同步任务重试机制说明', 1
    UNION ALL SELECT '17730584692', '桌面客户端重构', '2026-06-26', 4.0, '调整客户端设置页布局细节', 0
    UNION ALL SELECT '13691827450', '离线数据同步工具', '2026-06-27', 8.0, '处理离线包校验失败的提示文案', 3
    UNION ALL SELECT '19162740583', '桌面客户端重构', '2026-06-28', 6.5, '完成客户端日志导出功能联调', 2
    UNION ALL SELECT '18024691357', '合同审批流程', '2026-06-24', 6.0, '梳理合同审批节点和权限配置', 2
    UNION ALL SELECT '15860739241', '绩效数据看板', '2026-06-25', 7.5, '整理绩效指标口径和展示字段', 1
    UNION ALL SELECT '19358472016', '合同审批流程', '2026-06-26', 5.0, '完善合同归档流程的异常分支', 0
    UNION ALL SELECT '17182936504', '绩效数据看板', '2026-06-27', 4.5, '修正部门绩效图表的数据映射', 3
    UNION ALL SELECT '15542690837', '需求池管理系统', '2026-06-24', 8.0, '完成需求优先级排序规则设计', 2
    UNION ALL SELECT '19630758421', '用户体验反馈平台', '2026-06-25', 6.5, '整理用户反馈分类和处理状态', 1
    UNION ALL SELECT '14867523019', '需求池管理系统', '2026-06-26', 7.0, '开发需求详情页评论记录模块', 2
    UNION ALL SELECT '18250947631', '用户体验反馈平台', '2026-06-27', 5.5, '优化反馈提交表单的字段校验', 0
    UNION ALL SELECT '13786420591', '需求池管理系统', '2026-06-28', 6.0, '修复需求筛选条件无法重置问题', 3
    UNION ALL SELECT '19031572864', '用户体验反馈平台', '2026-06-29', 7.5, '完成反馈看板的筛选和分页联调', 2
    UNION ALL SELECT '18569472038', '数据指标中台', '2026-06-24', 7.5, '整理部门工时统计指标口径', 2
    UNION ALL SELECT '15623894701', '报表数据治理平台', '2026-06-25', 6.0, '处理报表字段映射和数据校验规则', 1
    UNION ALL SELECT '19745068123', '数据指标中台', '2026-06-26', 8.0, '完成指标查询接口缓存策略验证', 2
    UNION ALL SELECT '15180736492', '报表数据治理平台', '2026-06-27', 5.5, '修正报表导出文件名生成规则', 0
    UNION ALL SELECT '17932680541', '数据指标中台', '2026-06-28', 4.5, '补充指标异常波动说明字段', 3
    UNION ALL SELECT '13495726018', '报表数据治理平台', '2026-06-29', 6.5, '联调报表查询条件保存功能', 2
    UNION ALL SELECT '17250863941', '自动化测试平台', '2026-06-24', 7.0, '新增接口回归测试集合配置', 2
    UNION ALL SELECT '18376049215', '缺陷追踪平台', '2026-06-25', 6.5, '整理缺陷严重级别和流转规则', 1
    UNION ALL SELECT '16029485736', '自动化测试平台', '2026-06-26', 5.0, '调试自动化测试报告生成流程', 0
    UNION ALL SELECT '19583716042', '缺陷追踪平台', '2026-06-27', 8.0, '修复缺陷列表高级筛选条件问题', 3
    UNION ALL SELECT '15360928471', '自动化测试平台', '2026-06-28', 6.0, '维护测试环境接口基础地址配置', 2
    UNION ALL SELECT '16897352046', '客户服务知识库', '2026-06-24', 7.5, '整理常见问题知识库分类结构', 2
    UNION ALL SELECT '18456297013', '客户工单中心', '2026-06-25', 6.0, '完成工单分派规则和提醒配置', 1
    UNION ALL SELECT '16173840592', '客户服务知识库', '2026-06-26', 5.5, '补充知识库搜索关键词命中规则', 0
    UNION ALL SELECT '17529068134', '客户工单中心', '2026-06-27', 7.0, '优化客户工单详情页处理记录', 3
    UNION ALL SELECT '14936057281', '客户服务知识库', '2026-06-28', 6.5, '完成知识库文章编辑流程测试', 2
    UNION ALL SELECT '15294738106', '低代码工时配置台', '2026-07-01', 4.0, '补充字段权限配置页面说明', 1
    UNION ALL SELECT '15729368405', '移动消息推送平台', '2026-07-01', 3.5, '调整推送失败记录的展示字段', 0
    UNION ALL SELECT '19825460731', '离线数据同步工具', '2026-07-02', 5.0, '验证离线任务批量重试流程', 2
    UNION ALL SELECT '15542690837', '用户体验反馈平台', '2026-07-02', 6.0, '整理反馈处理时效统计规则', 3
    UNION ALL SELECT '18569472038', '报表数据治理平台', '2026-07-03', 4.5, '补充报表口径变更记录', 2
    UNION ALL SELECT '17250863941', '缺陷追踪平台', '2026-07-03', 5.5, '维护缺陷看板统计字段', 1
    UNION ALL SELECT '16897352046', '客户工单中心', '2026-07-04', 6.0, '验证客户工单超时提醒规则', 0
    UNION ALL SELECT '19630758421', '用户体验反馈平台', '2026-07-04', 7.0, '分析反馈来源分布和标签数据', 2
) w
JOIN `user` u ON u.`phone` = w.`phone`
JOIN `project` p ON p.`project_name` = w.`project_name`
WHERE NOT EXISTS (
    SELECT 1
    FROM `work_time_apply` old_w
    WHERE old_w.`user_id` = u.`user_id`
      AND old_w.`project_id` = p.`project_id`
      AND old_w.`work_date` = w.`work_date`
      AND old_w.`is_deleted` = 0
);

-- 7. 为新增工时生成操作日志。
-- 新建日志：所有新增工时都会产生。
INSERT INTO `work_time_log` (`work_id`, `operation_type`, `operation_time`, `operator_id`, `operation_desc`)
SELECT w.`work_id`,
       0,
       CONCAT(w.`work_date`, ' 18:00:00'),
       w.`user_id`,
       CONCAT('随机测试数据：员工新建工时，', w.`work_desc`)
FROM `work_time_apply` w
JOIN `user` u ON u.`user_id` = w.`user_id`
WHERE u.`phone` IN (
    '15294738106', '18956302741', '17042865983', '19970631452', '16659284017',
    '13580724691', '15729368405', '19274051836', '17365829041', '14790632518',
    '18642075963', '19825460731', '15069283704', '17730584692', '13691827450',
    '19162740583', '18024691357', '15860739241', '19358472016', '17182936504',
    '15542690837', '19630758421', '14867523019', '18250947631', '13786420591',
    '19031572864', '18569472038', '15623894701', '19745068123', '15180736492',
    '17932680541', '13495726018', '17250863941', '18376049215', '16029485736',
    '19583716042', '15360928471', '16897352046', '18456297013', '16173840592',
    '17529068134', '14936057281'
)
  AND NOT EXISTS (
      SELECT 1
      FROM `work_time_log` old_l
      WHERE old_l.`work_id` = w.`work_id`
        AND old_l.`operation_type` = 0
  );

-- 提交日志：待审批、审批通过、已驳回三类工时都表示曾经提交过。
INSERT INTO `work_time_log` (`work_id`, `operation_type`, `operation_time`, `operator_id`, `operation_desc`)
SELECT w.`work_id`,
       2,
       CONCAT(w.`work_date`, ' 18:12:00'),
       w.`user_id`,
       '随机测试数据：员工提交工时审批'
FROM `work_time_apply` w
JOIN `user` u ON u.`user_id` = w.`user_id`
WHERE u.`phone` IN (
    '15294738106', '18956302741', '17042865983', '19970631452', '16659284017',
    '13580724691', '15729368405', '19274051836', '17365829041', '14790632518',
    '18642075963', '19825460731', '15069283704', '17730584692', '13691827450',
    '19162740583', '18024691357', '15860739241', '19358472016', '17182936504',
    '15542690837', '19630758421', '14867523019', '18250947631', '13786420591',
    '19031572864', '18569472038', '15623894701', '19745068123', '15180736492',
    '17932680541', '13495726018', '17250863941', '18376049215', '16029485736',
    '19583716042', '15360928471', '16897352046', '18456297013', '16173840592',
    '17529068134', '14936057281'
)
  AND w.`status` IN (1, 2, 3)
  AND NOT EXISTS (
      SELECT 1
      FROM `work_time_log` old_l
      WHERE old_l.`work_id` = w.`work_id`
        AND old_l.`operation_type` = 2
  );

-- 审批通过日志：状态为 2 的工时会产生审批通过日志。
INSERT INTO `work_time_log` (`work_id`, `operation_type`, `operation_time`, `operator_id`, `operation_desc`)
SELECT w.`work_id`,
       3,
       CONCAT(DATE_ADD(w.`work_date`, INTERVAL 1 DAY), ' 09:18:00'),
       (
           SELECT manager_user.`user_id`
           FROM `user` manager_user
           JOIN `user` employee_user ON employee_user.`user_id` = w.`user_id`
           WHERE manager_user.`user_role` = '1'
             AND manager_user.`dept_id` = employee_user.`dept_id`
           ORDER BY manager_user.`user_id` DESC
           LIMIT 1
       ),
       '随机测试数据：部门经理审批通过'
FROM `work_time_apply` w
JOIN `user` u ON u.`user_id` = w.`user_id`
WHERE u.`phone` IN (
    '15294738106', '18956302741', '17042865983', '19970631452', '16659284017',
    '13580724691', '15729368405', '19274051836', '17365829041', '14790632518',
    '18642075963', '19825460731', '15069283704', '17730584692', '13691827450',
    '19162740583', '18024691357', '15860739241', '19358472016', '17182936504',
    '15542690837', '19630758421', '14867523019', '18250947631', '13786420591',
    '19031572864', '18569472038', '15623894701', '19745068123', '15180736492',
    '17932680541', '13495726018', '17250863941', '18376049215', '16029485736',
    '19583716042', '15360928471', '16897352046', '18456297013', '16173840592',
    '17529068134', '14936057281'
)
  AND w.`status` = 2
  AND NOT EXISTS (
      SELECT 1
      FROM `work_time_log` old_l
      WHERE old_l.`work_id` = w.`work_id`
        AND old_l.`operation_type` = 3
  );

-- 驳回日志：状态为 3 的工时会产生驳回日志。
INSERT INTO `work_time_log` (`work_id`, `operation_type`, `operation_time`, `operator_id`, `operation_desc`)
SELECT w.`work_id`,
       4,
       CONCAT(DATE_ADD(w.`work_date`, INTERVAL 1 DAY), ' 10:06:00'),
       (
           SELECT manager_user.`user_id`
           FROM `user` manager_user
           JOIN `user` employee_user ON employee_user.`user_id` = w.`user_id`
           WHERE manager_user.`user_role` = '1'
             AND manager_user.`dept_id` = employee_user.`dept_id`
           ORDER BY manager_user.`user_id` DESC
           LIMIT 1
       ),
       '随机测试数据：描述需要补充具体产出，已驳回'
FROM `work_time_apply` w
JOIN `user` u ON u.`user_id` = w.`user_id`
WHERE u.`phone` IN (
    '15294738106', '18956302741', '17042865983', '19970631452', '16659284017',
    '13580724691', '15729368405', '19274051836', '17365829041', '14790632518',
    '18642075963', '19825460731', '15069283704', '17730584692', '13691827450',
    '19162740583', '18024691357', '15860739241', '19358472016', '17182936504',
    '15542690837', '19630758421', '14867523019', '18250947631', '13786420591',
    '19031572864', '18569472038', '15623894701', '19745068123', '15180736492',
    '17932680541', '13495726018', '17250863941', '18376049215', '16029485736',
    '19583716042', '15360928471', '16897352046', '18456297013', '16173840592',
    '17529068134', '14936057281'
)
  AND w.`status` = 3
  AND NOT EXISTS (
      SELECT 1
      FROM `work_time_log` old_l
      WHERE old_l.`work_id` = w.`work_id`
        AND old_l.`operation_type` = 4
  );

COMMIT;

-- 8. 执行后检查数据量。
-- 这几条 SELECT 用于在 Navicat 结果窗口中确认本次随机数据是否成功进入数据库。
SELECT COUNT(*) AS random_user_count
FROM `user`
WHERE `phone` IN (
    '17638492051', '15294738106', '18956302741', '17042865983', '19970631452',
    '16659284017', '13580724691', '18163495072', '15729368405', '19274051836',
    '17365829041', '14790632518', '18642075963', '15978304126', '19825460731',
    '15069283704', '17730584692', '13691827450', '19162740583', '16573908214',
    '18024691357', '15860739241', '19358472016', '17182936504', '18875301962',
    '15542690837', '19630758421', '14867523019', '18250947631', '13786420591',
    '19031572864', '16780249315', '18569472038', '15623894701', '19745068123',
    '15180736492', '17932680541', '13495726018', '19468157320', '17250863941',
    '18376049215', '16029485736', '19583716042', '15360928471', '18742063519',
    '16897352046', '18456297013', '16173840592', '17529068134', '14936057281'
);

SELECT d.`dept_name`, COUNT(u.`user_id`) AS random_user_count
FROM `department` d
LEFT JOIN `user` u ON u.`dept_id` = d.`dept_id`
WHERE u.`phone` IN (
    '17638492051', '15294738106', '18956302741', '17042865983', '19970631452',
    '16659284017', '13580724691', '18163495072', '15729368405', '19274051836',
    '17365829041', '14790632518', '18642075963', '15978304126', '19825460731',
    '15069283704', '17730584692', '13691827450', '19162740583', '16573908214',
    '18024691357', '15860739241', '19358472016', '17182936504', '18875301962',
    '15542690837', '19630758421', '14867523019', '18250947631', '13786420591',
    '19031572864', '16780249315', '18569472038', '15623894701', '19745068123',
    '15180736492', '17932680541', '13495726018', '19468157320', '17250863941',
    '18376049215', '16029485736', '19583716042', '15360928471', '18742063519',
    '16897352046', '18456297013', '16173840592', '17529068134', '14936057281'
)
GROUP BY d.`dept_id`, d.`dept_name`
ORDER BY d.`dept_id`;

SELECT d.`dept_name`, COUNT(u.`user_id`) AS manager_count
FROM `department` d
LEFT JOIN `user` u ON u.`dept_id` = d.`dept_id`
WHERE u.`phone` IN (
    '17638492051', '18163495072', '15978304126', '16573908214',
    '18875301962', '16780249315', '19468157320', '18742063519'
)
  AND u.`user_role` = '1'
GROUP BY d.`dept_id`, d.`dept_name`
ORDER BY d.`dept_id`;

SELECT COUNT(*) AS random_work_time_count
FROM `work_time_apply` w
JOIN `user` u ON u.`user_id` = w.`user_id`
WHERE u.`phone` IN (
    '15294738106', '18956302741', '17042865983', '19970631452', '16659284017',
    '13580724691', '15729368405', '19274051836', '17365829041', '14790632518',
    '18642075963', '19825460731', '15069283704', '17730584692', '13691827450',
    '19162740583', '18024691357', '15860739241', '19358472016', '17182936504',
    '15542690837', '19630758421', '14867523019', '18250947631', '13786420591',
    '19031572864', '18569472038', '15623894701', '19745068123', '15180736492',
    '17932680541', '13495726018', '17250863941', '18376049215', '16029485736',
    '19583716042', '15360928471', '16897352046', '18456297013', '16173840592',
    '17529068134', '14936057281'
);
