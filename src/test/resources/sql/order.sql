-- 创建事务表
CREATE TABLE `tcc_main_log_order` (
  `biz_id` bigint NOT NULL COMMENT '业务id',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态: 1 创建事务成功 2  回滚成功  3 完成本地事务成功  4 提交事务成功',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号',
  `last_update_time` bigint NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  `create_time` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `checked_times` int NOT NULL DEFAULT '0' COMMENT '失败检查次数',
  PRIMARY KEY (`biz_id`),
  KEY `idx_status_checked_times_create_time` (`status`,`checked_times`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='主事务日志表';

-- 创建订单表
CREATE TABLE `tcc_demo_order` (
  `order_id` bigint NOT NULL,
  `status` int NOT NULL,
  `user_id` bigint NOT NULL,
  `deduction_points` bigint DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;