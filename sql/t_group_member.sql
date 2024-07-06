CREATE TABLE `t_group_member`
(
    `group_member_id` bigint(20) NOT NULL,
    `app_id`          int(11) NOT NULL,
    `group_id`        varchar(50) NOT NULL,
    `member_id`       varchar(50) NOT NULL,
    `speak_`          varchar(255)  DEFAULT NULL COMMENT '禁言到期时间',
    `role`            tinyint(4) NOT NULL COMMENT '群成员类型 0 普通成员 1 管理员 2 群主 3',
    `alias`           varchar(50)   DEFAULT NULL COMMENT '群昵称',
    `join_time`       bigint(20) DEFAULT NULL COMMENT '加入时间',
    `leave_time`      bigint(20) DEFAULT NULL COMMENT '离开时间',
    `join_type`       varchar(50)   DEFAULT NULL COMMENT '加入方式',
    `extra`           varchar(1000) DEFAULT NULL,
    PRIMARY KEY (`group_member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;