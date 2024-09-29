-- 创建积分变动日志表
CREATE TABLE `tcc_demo_points_changing_log` (
  `biz_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `change_points` bigint NOT NULL,
  `change_type` int NOT NULL,
  `status` int NOT NULL,
  PRIMARY KEY (`biz_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 创建用户积分表
CREATE TABLE `tcc_demo_user_points` (
  `user_id` bigint NOT NULL,
  `points` bigint NOT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 初始化用户积分
INSERT INTO `tcc_demo_user_points` (`user_id`, `points`) VALUES (12345678, 999999999989989999);