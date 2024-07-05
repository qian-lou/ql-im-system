CREATE TABLE `t_friendship_group_member`
(
    `group_id` bigint                                 NOT NULL,
    `to_id`    varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
    PRIMARY KEY (`group_id`, `to_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;