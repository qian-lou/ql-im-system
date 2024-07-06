CREATE TABLE `t_group`
(
    `group_id`         varchar(50) NOT NULL,
    `app_id`           int(11) NOT NULL,
    `owner_id`         varchar(50)   DEFAULT NULL COMMENT '群主id',
    `group_type`       tinyint(4) NOT NULL COMMENT '群类型 1私有群 2公开群',
    `group_name`       varchar(50) NOT NULL COMMENT '群名称',
    `silence`          tinyint(4) DEFAULT NULL COMMENT '是否开启群禁言 1 开启',
    `status`           tinyint(4) DEFAULT NULL COMMENT '群状态 1正常 2解散',
    `apply_type`       tinyint(4) DEFAULT NULL COMMENT '申请加群类型',
    `introduction`     varchar(255)  DEFAULT NULL COMMENT '群简介',
    `notification`     varchar(1000) DEFAULT NULL COMMENT '群公告',
    `img`              varchar(500)  DEFAULT NULL COMMENT '头像',
    `max_member_count` int(11) DEFAULT NULL COMMENT '最大的群成员人数',
    `sequence`         bigint(20) DEFAULT NULL,
    `extra`            varchar(1000) DEFAULT NULL,
    `create_time`      bigint(20) DEFAULT NULL,
    `update_time`      bigint(20) DEFAULT NULL,
    PRIMARY KEY (`app_id`, `group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群信息表';