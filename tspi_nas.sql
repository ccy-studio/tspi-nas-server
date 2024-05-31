

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_buckets
-- ----------------------------
DROP TABLE IF EXISTS `t_buckets`;
CREATE TABLE `t_buckets`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `buckets_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '桶名称',
  `mount_point` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '挂载点路径',
  `res_id` bigint(0) UNSIGNED NOT NULL COMMENT '资源ID',
  `permissions` int(0) NOT NULL DEFAULT 0 COMMENT '权限:0私有,1公读公写,2公读私写',
  `permissions_scope` int(0) NOT NULL DEFAULT 0 COMMENT '权限范围:0私有,1资源内公开,2全公开,',
  `static_page` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是静态页面',
  `is_delete` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除 0:未删除',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_t_buckets`(`mount_point`, `is_delete`) USING BTREE,
  UNIQUE INDEX `uk_t_buckets_buckets_name`(`buckets_name`, `is_delete`) USING BTREE,
  INDEX `idx_t_buckets_res_id`(`res_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '存储桶' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_buckets
-- ----------------------------
INSERT INTO `t_buckets` VALUES (24, '测试', '/Users/chen/Downloads/NAS_2', 5, 0, 0, 0, 0, '2024-05-26 09:15:46', '2024-05-26 09:17:23', 1, NULL);

-- ----------------------------
-- Table structure for t_file_block_records
-- ----------------------------
DROP TABLE IF EXISTS `t_file_block_records`;
CREATE TABLE `t_file_block_records`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `file_object_parent_id` bigint(0) UNSIGNED NOT NULL COMMENT '父文件对象id',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名称',
  `dir_path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实物理路径',
  `file_md5` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MD5值',
  `file_size` bigint(0) UNSIGNED NOT NULL COMMENT '文件大小',
  `block_count` int(0) NOT NULL COMMENT '分块总数',
  `is_overwrite` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否覆盖',
  `is_delete` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除 0:未删除',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_t_file_block_records`(`file_object_parent_id`) USING BTREE,
  CONSTRAINT `fk_t_file_block_records` FOREIGN KEY (`file_object_parent_id`) REFERENCES `t_file_object` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件分块续传记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_file_object
-- ----------------------------
DROP TABLE IF EXISTS `t_file_object`;
CREATE TABLE `t_file_object`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `buckets_id` bigint(0) UNSIGNED NOT NULL COMMENT '桶ID',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件对象名称',
  `file_content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件后缀类型',
  `file_path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `real_path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实物理路径',
  `file_size` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '文件大小',
  `file_md5` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MD5值',
  `is_dir` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是目录',
  `parent_id` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '父目录ID,无则为空',
  `is_delete` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除 0:未删除',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_t_file_object_buckets_id`(`buckets_id`) USING BTREE,
  CONSTRAINT `t_file_object_ibfk_1` FOREIGN KEY (`buckets_id`) REFERENCES `t_buckets` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21154 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件对象' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_file_object
-- ----------------------------
INSERT INTO `t_file_object` VALUES (21132, 24, '/', NULL, '/', '/Users/chen/Downloads/NAS_2', NULL, NULL, 1, NULL, 0, '2024-05-26 09:15:46', '2024-05-26 09:17:51', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21133, 24, '5563.zip', NULL, '/5563.zip', '/Users/chen/Downloads/NAS_2/5563.zip', NULL, NULL, 1, 21132, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:18', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21134, 24, 'app测试复制target', NULL, '/app测试复制target', '/Users/chen/Downloads/NAS_2/app测试复制target', NULL, NULL, 1, 21132, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:18', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21135, 24, 'app移动测试', NULL, '/app测试复制target/app移动测试', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试', NULL, NULL, 1, 21134, 0, '2024-05-26 17:18:19', '2024-05-26 17:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21136, 24, 'assets', NULL, '/app测试复制target/app移动测试/assets', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试/assets', NULL, NULL, 1, 21135, 0, '2024-05-26 17:18:19', '2024-05-26 17:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21137, 24, '136k.jpg', 'image/jpeg', '/136k.jpg', '/Users/chen/Downloads/NAS_2/136k.jpg', 1088346, 'a5fabafced80cc59310035d8611bf15b', 0, 21132, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21138, 24, '.DS_Store', 'application/octet-stream', '/.DS_Store', '/Users/chen/Downloads/NAS_2/.DS_Store', 6148, '2695e47f7e789172300f0bc4e60a86da', 0, 21132, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21139, 24, '20240115_210618.jpg', 'image/jpeg', '/20240115_210618.jpg', '/Users/chen/Downloads/NAS_2/20240115_210618.jpg', 3527423, 'f99d08ab146c15480537831443bd588f', 0, 21132, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21140, 24, 'wallhaven-ex136k.jpg', 'image/jpeg', '/wallhaven-ex136k.jpg', '/Users/chen/Downloads/NAS_2/wallhaven-ex136k.jpg', 1088346, 'a5fabafced80cc59310035d8611bf15b', 0, 21132, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21141, 24, '.DS_Store', 'application/octet-stream', '/app测试复制target/.DS_Store', '/Users/chen/Downloads/NAS_2/app测试复制target/.DS_Store', 6148, '194577a7e20bdcc7afbb718f502c134c', 0, 21134, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21142, 24, 'project.properties', 'text/x-java-properties', '/app测试复制target/app移动测试/project.properties', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试/project.properties', 563, '6bc29827a20f0391406586601017da3b', 0, 21135, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21143, 24, 'local.properties', 'text/x-java-properties', '/app测试复制target/app移动测试/local.properties', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试/local.properties', 350, '3409ef581fdee415ca3ed0c0de625005', 0, 21135, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21144, 24, 'README.md', 'text/x-web-markdown', '/app测试复制target/app移动测试/README.md', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试/README.md', 165, '0d25234f1b1df4ba255f14c9924b6d7f', 0, 21135, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21145, 24, 'TTKD.txt', 'text/plain', '/app测试复制target/app移动测试/assets/TTKD.txt', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试/assets/TTKD.txt', 3177, 'e4cb73166b1984dabc9c444f9c8ac0b6', 0, 21136, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21146, 24, 'logo_sto_print1.png', 'image/png', '/app测试复制target/app移动测试/assets/logo_sto_print1.png', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试/assets/logo_sto_print1.png', 3433, 'd788a3b867a86b6ee7ed40b45809515f', 0, 21136, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21147, 24, 'logo_sto_print2.png', 'image/png', '/app测试复制target/app移动测试/assets/logo_sto_print2.png', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试/assets/logo_sto_print2.png', 2709, '483979ef76378ff219b7c29e3f3f1fe7', 0, 21136, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21148, 24, 'logo3.png', 'image/png', '/app测试复制target/app移动测试/assets/logo3.png', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试/assets/logo3.png', 7023, '6d0ff87186a3726800edc92519840f6a', 0, 21136, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21149, 24, 'ZhongTong.txt', 'text/plain', '/app测试复制target/app移动测试/assets/ZhongTong.txt', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试/assets/ZhongTong.txt', 3034, '4cd442905626ea97ab1c127a34009748', 0, 21136, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21150, 24, 'Android_CPCL_SDK说明文档_V05.pdf', 'application/pdf', '/app测试复制target/app移动测试/assets/Android_CPCL_SDK说明文档_V05.pdf', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试/assets/Android_CPCL_SDK说明文档_V05.pdf', 303947, '2762cdf88e338c5e797d8dd6c81b708a', 0, 21136, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21151, 24, 'STO_CPCL.txt', 'text/plain', '/app测试复制target/app移动测试/assets/STO_CPCL.txt', '/Users/chen/Downloads/NAS_2/app测试复制target/app移动测试/assets/STO_CPCL.txt', 2084, '8f1d68c5ce2ee1f8c1bac99bb8be1fcd', 0, 21136, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21152, 24, 'description', 'text/plain', '/description', '/Users/chen/Downloads/NAS_2/description', 73, 'a0a7c3fff21f2aea3cfa1d0316dd816c', 0, 21132, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);
INSERT INTO `t_file_object` VALUES (21153, 24, '推荐使用！！！手机端软件3不要进行修改颜色以外的操作,不要修改对比度和亮度，不要点恢复出厂设置_1.apk', 'application/vnd.android.package-archive', '/推荐使用！！！手机端软件3不要进行修改颜色以外的操作,不要修改对比度和亮度，不要点恢复出厂设置_1.apk', '/Users/chen/Downloads/NAS_2/推荐使用！！！手机端软件3不要进行修改颜色以外的操作,不要修改对比度和亮度，不要点恢复出厂设置_1.apk', 28962945, '01c0bbe63fea449401a760d7bb156a4d', 0, 21132, 0, '2024-05-26 17:18:19', '2024-05-26 09:18:19', NULL, NULL);

-- ----------------------------
-- Table structure for t_file_object_share
-- ----------------------------
DROP TABLE IF EXISTS `t_file_object_share`;
CREATE TABLE `t_file_object_share`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `file_object_id` bigint(0) UNSIGNED NOT NULL COMMENT '对象ID',
  `sign_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分享签名KEY',
  `expiration_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间,为空永久',
  `access_password` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '访问密码',
  `click_count` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '访问次数',
  `is_symlink` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是直链',
  `is_delete` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除 0:未删除',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `t_file_object_share_t_file_object_id_fk`(`file_object_id`) USING BTREE,
  CONSTRAINT `t_file_object_share_t_file_object_id_fk` FOREIGN KEY (`file_object_id`) REFERENCES `t_file_object` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件对象分享' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_hard_disk
-- ----------------------------
DROP TABLE IF EXISTS `t_hard_disk`;
CREATE TABLE `t_hard_disk`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `mount_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '硬盘挂载点路径',
  `device` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名称/sdax',
  `disk_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '硬盘唯一ID标识',
  `is_delete` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除 0:未删除',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '磁盘配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_policies_rule
-- ----------------------------
DROP TABLE IF EXISTS `t_policies_rule`;
CREATE TABLE `t_policies_rule`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint(0) UNSIGNED NOT NULL COMMENT '绑定用户ID',
  `buckets_id` bigint(0) UNSIGNED NOT NULL COMMENT '桶ID',
  `effect` tinyint(1) NOT NULL DEFAULT 1 COMMENT '动作:true允许,false拒绝',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作:get_obj,put_obj,del_obj,share_obj,super多个分号分隔',
  `is_delete` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除 0:未删除',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `t_policies_rule_pk`(`user_id`, `buckets_id`) USING BTREE,
  INDEX `fk_t_policies_rule_buckets_id`(`buckets_id`) USING BTREE,
  CONSTRAINT `t_policies_rule_ibfk_1` FOREIGN KEY (`buckets_id`) REFERENCES `t_buckets` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `t_policies_rule_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '策略规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_policies_rule
-- ----------------------------
INSERT INTO `t_policies_rule` VALUES (27, 1, 24, 1, 'super', 0, '2024-05-26 09:15:46', '2024-05-26 09:15:46', 1, NULL);

-- ----------------------------
-- Table structure for t_resources
-- ----------------------------
DROP TABLE IF EXISTS `t_resources`;
CREATE TABLE `t_resources`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `res_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源名称',
  `res_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资源描述',
  `res_type` int(0) NOT NULL DEFAULT 0 COMMENT '资源类型 0:FILE,1:SMB,2:FTP,3.WebDav',
  `res_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源路径',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '使能状态:true使能,false禁用',
  `is_delete` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除 0:未删除',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '存储资源' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_resources
-- ----------------------------
INSERT INTO `t_resources` VALUES (5, 'LOCLA', '', 0, 'D:\\TempFiles\\NAS_FILES', 1, 0, '2024-05-18 12:43:55', '2024-05-18 12:43:55', 1, NULL);
INSERT INTO `t_resources` VALUES (6, '资源2', '', 1, '/mnt/ssd1', 1, 0, '2024-05-23 07:12:43', '2024-05-23 07:12:43', 1, NULL);

-- ----------------------------
-- Table structure for t_resources_user_group
-- ----------------------------
DROP TABLE IF EXISTS `t_resources_user_group`;
CREATE TABLE `t_resources_user_group`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `res_id` bigint(0) UNSIGNED NOT NULL COMMENT '资源ID',
  `user_group_id` bigint(0) UNSIGNED NOT NULL COMMENT '用户组ID',
  `is_delete` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除 0:未删除',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_t_resources_user_group`(`user_group_id`, `res_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '存储资源关联用户组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_resources_user_group
-- ----------------------------
INSERT INTO `t_resources_user_group` VALUES (14, 5, 8, 0, '2024-05-18 12:44:07', '2024-05-18 12:44:07', NULL, NULL);
INSERT INTO `t_resources_user_group` VALUES (15, 5, 9, 0, '2024-05-23 07:12:55', '2024-05-23 07:12:55', NULL, NULL);
INSERT INTO `t_resources_user_group` VALUES (16, 6, 9, 0, '2024-05-23 07:12:55', '2024-05-23 07:12:55', NULL, NULL);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_account` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账户',
  `user_password` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户密码',
  `salt` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '盐',
  `nick_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `mobile` varchar(14) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `access_key` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'AK',
  `secret_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SK',
  `is_delete` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除 0:未删除',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_t_user`(`user_account`, `is_delete`) USING BTREE,
  INDEX `idx_t_user_access_id`(`access_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'admin', 'e250f91573d4cdd009540353eb3765bf', '71669abec7de4934a4a52535a8ece190', 'admin', '1888888', 'fc8959dae8fa4dbd9b17', 'e859a1c101b44a95a35661c29a04db510212dabd', 0, '2024-03-09 06:17:30', '2024-03-09 07:03:30', NULL, NULL);
INSERT INTO `t_user` VALUES (6, 'test', 'ab4c9494ee0b5ee1dee9ff6feb31ef83', 'ab7096d0ae574c17b16dc6534edb147e', '测试用户1', '', '69eb2091943e45a7be2d', '2e801e3624d84785b84b24a52738befb95be63e0', 0, '2024-05-18 20:44:24', '2024-05-18 12:44:23', 1, 1);
INSERT INTO `t_user` VALUES (7, 'test2', 'bb3893b3e5756ab52171ec8e83d2e3c7', 'e44f8b4033784da18b926ea2f08ad9b3', '测试没有ACL', '', '28c37719309f4a7ca29a', '36ec5ac7440042b49e461a2e0f7cd3777a862c08', 0, '2024-05-23 10:04:24', '2024-05-23 02:04:20', 1, NULL);
INSERT INTO `t_user` VALUES (8, 'group', '3763a9388a6355957448c9b9dacc5eff', 'e0c2436703934aeeaac6efa06c772e77', '多组用户', '', 'ba28f49651e14ecb8058', 'e90d701bb8d84a3984bcdc37ceca91f279289599', 0, '2024-05-23 15:13:28', '2024-05-23 07:13:24', 1, NULL);

-- ----------------------------
-- Table structure for t_user_group
-- ----------------------------
DROP TABLE IF EXISTS `t_user_group`;
CREATE TABLE `t_user_group`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `group_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组名称',
  `is_delete` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除 0:未删除',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_t_user_group`(`group_name`, `is_delete`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_group
-- ----------------------------
INSERT INTO `t_user_group` VALUES (8, '普通用户组', 0, '2024-05-18 12:44:07', '2024-05-18 12:44:07', NULL, NULL);
INSERT INTO `t_user_group` VALUES (9, '超级组', 0, '2024-05-23 07:12:55', '2024-05-23 07:12:55', NULL, NULL);

-- ----------------------------
-- Table structure for t_user_group_bind
-- ----------------------------
DROP TABLE IF EXISTS `t_user_group_bind`;
CREATE TABLE `t_user_group_bind`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint(0) UNSIGNED NOT NULL COMMENT '用户ID',
  `user_group_id` bigint(0) UNSIGNED NOT NULL COMMENT '用户组ID',
  `is_delete` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除 0:未删除',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_t_user_group_bind`(`user_id`, `user_group_id`) USING BTREE,
  CONSTRAINT `t_user_group_bind_t_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户组绑定关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_group_bind
-- ----------------------------
INSERT INTO `t_user_group_bind` VALUES (19, 6, 8, 0, '2024-05-18 12:44:23', '2024-05-18 12:44:23', NULL, NULL);
INSERT INTO `t_user_group_bind` VALUES (20, 7, 8, 0, '2024-05-23 02:04:20', '2024-05-23 02:04:20', NULL, NULL);
INSERT INTO `t_user_group_bind` VALUES (21, 8, 8, 0, '2024-05-23 07:13:24', '2024-05-23 07:13:24', NULL, NULL);
INSERT INTO `t_user_group_bind` VALUES (22, 8, 9, 0, '2024-05-23 07:13:24', '2024-05-23 07:13:24', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
