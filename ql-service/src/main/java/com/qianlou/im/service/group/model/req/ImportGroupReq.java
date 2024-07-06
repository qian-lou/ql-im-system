package com.qianlou.im.service.group.model.req;

import com.qianlou.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;


@EqualsAndHashCode(callSuper = true)
@Data
public class ImportGroupReq extends RequestBase {

    private String groupId;
    /**
     * 群主id
     */
    private String ownerId;

    /**
     * 群类型 1私有群（类似微信） 2公开群(类似qq）
     */
    private Integer groupType;

    @NotBlank(message = "群名称不能为空")
    private String groupName;

    /**
     * 是否全员禁言，0 不禁言；1 全员禁言。
     */
    private Integer silence;

    /**
     * 加入群权限，0 所有人可以加入；1 群成员可以拉人；2 群管理员或群组可以拉人。
     */
    private Integer applyType;

    /**
     * 群简介
     */
    private String introduction;

    /**
     * 群公告
     */
    private String notification;

    /**
     * 群头像
     */
    private String img;

    private Integer MaxMemberCount;

    private String extra;

    private Long createTime;

}
