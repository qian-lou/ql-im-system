CREATE TABLE `t_friendship`
(
    `app_id`          int                                    NOT NULL,
    `from_id`         varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
    `to_id`           varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
    `remark`          varchar(255) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '备注',
    `status`          tinyint                                  DEFAULT NULL COMMENT '1正常 2 删除 0 未知',
    `black`           tinyint                                  DEFAULT NULL COMMENT '1正常 2 拉黑',
    `black_sequence`  varchar(255) COLLATE utf8mb4_general_ci  DEFAULT NULL,
    `create_time`     bigint                                   DEFAULT NULL,
    `friend_sequence` bigint                                   DEFAULT NULL COMMENT 'seq',
    `add_source`      varchar(20) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '来源',
    `extra`           varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL,
    PRIMARY KEY (`app_id`, `from_id`, `to_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;