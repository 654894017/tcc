-- 创建积分变动日志表
CREATE TABLE `tcc_demo_points_changing_log`
(
    `biz_id`        bigint NOT NULL,
    `user_id`       bigint NOT NULL,
    `change_points` bigint NOT NULL,
    `change_type`   int    NOT NULL,
    `status`        int    NOT NULL,
    PRIMARY KEY (`biz_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 创建用户积分表
CREATE TABLE `tcc_demo_user_points`
(
    `user_id` bigint NOT NULL,
    `points`  bigint NOT NULL,
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 初始化用户积分
INSERT INTO `tcc_demo_user_points` (`user_id`, `points`)
VALUES (12345678, 999999999989989999);


CREATE TABLE `tcc_sub_log_order`
(
    `biz_id`           bigint NOT NULL COMMENT '业务id',
    `sub_biz_id`       bigint NOT NULL DEFAULT '0' COMMENT '子业务id',
    `status`           int    NOT NULL DEFAULT '0' COMMENT '状态: 1 创建事务成功 2  提交事务成功  3 回滚事务成功',
    `version`          int    NOT NULL DEFAULT '0' COMMENT '版本号',
    `last_update_time` bigint NOT NULL DEFAULT '0' COMMENT '最后更新时间',
    `create_time`      bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (`biz_id`, `sub_biz_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='子事务日志表';