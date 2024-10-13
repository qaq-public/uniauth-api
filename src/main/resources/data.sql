-- ----------------------------
-- Records of role
-- ----------------------------
INSERT IGNORE INTO `app_role` (`id`, `name`, `app_id`, `description`, `pre_defined`) VALUES (1, '应用管理员', NULL, '应用管理员', 1);
INSERT IGNORE INTO `project_role` (`id`, `name`,  `description`, `pre_defined`) VALUES (1, '项目管理员', '管理项目的人', 1);
INSERT IGNORE INTO `project_role` (`id`, `name`,  `description`, `pre_defined`) VALUES (2, '策划', 'QA以外的人', 1);
INSERT IGNORE INTO `project_role` (`id`, `name`,  `description`, `pre_defined`) VALUES (3, '程序', '程序', 1);
INSERT IGNORE INTO `project_role` (`id`, `name`,  `description`, `pre_defined`) VALUES (4, '美术', '美术', 1);
INSERT IGNORE INTO `project_role` (`id`, `name`,  `description`, `pre_defined`) VALUES (5, 'PM', 'PM', 1);
INSERT IGNORE INTO `project_role` (`id`, `name`,  `description`, `pre_defined`) VALUES (6, 'UI', 'UI', 1);
INSERT IGNORE INTO `project_role` (`id`, `name`,  `description`, `pre_defined`) VALUES (7, '发行', '发行', 1);
INSERT IGNORE INTO `project_role` (`id`, `name`,  `description`, `pre_defined`) VALUES (8, 'QA', '中台QA正职', 1);
INSERT IGNORE INTO `project_role` (`id`, `name`,  `description`, `pre_defined`) VALUES (9, '中台程序', '中台程序(全职)', 1);
-- ----------------------------
-- Records of app
-- ----------------------------
INSERT IGNORE INTO `app` ( `id`, `code`, `share_role`, `name`, `share_member`, `description`, `avatar`, `create_time`, `create_user_id` )
VALUES
	( 1, 'uniauth', 1, '权限管理', 1, '权限管理', '', NOW( ), NULL );
-- ----------------------------
-- Records of project
-- ----------------------------
INSERT IGNORE INTO `project` ( `id`, `code`, `name`, `description`, `stage`, `studio`, `create_time`, `create_user_id`, `last_modify_user_id`, `last_modify_time`, `game_type`, `avatar` )
VALUES
	( 1, 'H100', 'demo', '测试中台', 'DEMO阶段', '测试中台', NOW(), NULL, NULL, NOW(), '[\"Moba\", \"MMO\"]', '' );

INSERT IGNORE INTO `permission` (`id`, `name`, `app_id`, `description`, `pre_defined`) VALUES (1, 'app_create', 1, '应用创建', 1);
INSERT IGNORE INTO `permission` (`id`, `name`, `app_id`, `description`, `pre_defined`) VALUES (2, 'platform', 1, 'QAQ平台管理员', 1);
INSERT IGNORE INTO `permission` (`id`, `name`, `app_id`, `description`, `pre_defined`) VALUES (3, 'project_modify', 1, '项目信息修改', 1);
INSERT IGNORE INTO `permission` (`id`, `name`, `app_id`, `description`, `pre_defined`) VALUES (4, 'project_member_add', 1, '项目成员添加', 1);
INSERT IGNORE INTO `permission` (`id`, `name`, `app_id`, `description`, `pre_defined`) VALUES (5, 'project_member_modify', 1, '项目成员修改', 1);
INSERT IGNORE INTO `permission` (`id`, `name`, `app_id`, `description`, `pre_defined`) VALUES (6, 'project_member_delete', 1, '项目成员删除', 1);

INSERT IGNORE INTO `project_role_permission` (`role_id`, `permission_id`) VALUES (1, 3);
INSERT IGNORE INTO `project_role_permission` (`role_id`, `permission_id`) VALUES (1, 4);
INSERT IGNORE INTO `project_role_permission` (`role_id`, `permission_id`) VALUES (1, 5);
INSERT IGNORE INTO `project_role_permission` (`role_id`, `permission_id`) VALUES (1, 6);