CREATE TABLE `tcc_main_log_order` (
  `biz_id` bigint(20) NOT NULL COMMENT '业务id',
  `status` int(2) NOT NULL DEFAULT '0' COMMENT '状态: 1 创建事务成功 2  回滚成功  3 完成本地事务成功  4 提交事务成功',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
  `last_update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `checked_times` int(2) NOT NULL DEFAULT '0' COMMENT '失败检查次数',
  PRIMARY KEY (`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='主事务日志表';



CREATE TABLE `tcc_sub_log_order` (
  `biz_id` bigint(20) NOT NULL COMMENT '业务id',
  `status` int(2) NOT NULL DEFAULT '0' COMMENT '状态: 1 创建事务成功 2  提交事务成功  3 回滚事务成功',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
  `last_update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='子事务日志表';