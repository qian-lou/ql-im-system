package com.qianlou.im.service.friendship.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_friendship_group")
public class FriendShipGroupEntity {

    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;

    private String fromId;

    private Integer appId;

    private String groupName;
    /**
     * 备注
     */
    private Long createTime;

    /**
     * 备注
     */
    private Long updateTime;

    /**
     * 序列号
     */
    private Long sequence;

    private int delFlag;


}
