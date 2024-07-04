CREATE TABLE `t_user`
(
    `user_id`            varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
    `app_id`             int                                    NOT NULL,
    `nickname`           varchar(50) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '昵称',
    `gender`             tinyint                                  DEFAULT NULL COMMENT '性别',
    `birthday`           varchar(30) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '生日',
    `location`           varchar(255) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '所在地',
    `signature`          varchar(255) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '个性签名',
    `friend_allow_type`  tinyint                                NOT NULL COMMENT '添加好友方式 1无需验证 2 需要验证',
    `img`                varchar(500) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '头像地址',
    `password`           varchar(255) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '密码',
    `disable_add_friend` tinyint                                  DEFAULT NULL COMMENT '管理员禁止用户添加好友： 0 未禁用 1 已禁用',
    `silent_flag`        tinyint                                  DEFAULT NULL COMMENT '禁言标志 1 禁言',
    `forbidden_flag`     tinyint                                  DEFAULT NULL COMMENT '禁用标志 1 禁用',
    `user_type`          tinyint                                  DEFAULT NULL COMMENT '用户类型 1 im 用户',
    `del_flag`           tinyint                                  DEFAULT NULL COMMENT '删除标志 1 删除',
    `extra`              varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '拓展',
    PRIMARY KEY (`app_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;