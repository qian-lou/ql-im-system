CREATE TABLE `t_friendship_request`
(
    `id`             int NOT NULL AUTO_INCREMENT,
    `from_id`        varchar(50) COLLATE utf8mb4_general_ci  DEFAULT NULL,
    `to_id`          varchar(50) COLLATE utf8mb4_general_ci  DEFAULT NULL,
    `app_id`         int                                     DEFAULT NULL,
    `read_status`    tinyint                                 DEFAULT NULL COMMENT '是否已读 1 已读',
    `add_message`    varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '好友添加信息',
    `remark`         varchar(50) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '备注',
    `approve_status` tinyint                                 DEFAULT NULL COMMENT '审核结果',
    `create_time`    bigint                                  DEFAULT NULL,
    `update_time`    bigint                                  DEFAULT NULL,
    `sequence`       bigint                                  DEFAULT NULL,
    `add_source`     varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;