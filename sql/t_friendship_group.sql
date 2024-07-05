CREATE TABLE `t_friendship_group`
(
    `group_id`    bigint NOT NULL AUTO_INCREMENT,
    `from_id`     varchar(50) COLLATE utf8mb4_general_ci  DEFAULT NULL,
    `app_id`      int                                     DEFAULT NULL,
    `group_name`  varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `sequence`    bigint                                  DEFAULT NULL,
    `create_time` bigint                                  DEFAULT NULL,
    `update_time` bigint                                  DEFAULT NULL,
    `del_flag`    int                                     DEFAULT NULL,
    PRIMARY KEY (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;