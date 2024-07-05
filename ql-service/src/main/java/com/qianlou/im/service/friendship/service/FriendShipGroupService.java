package com.qianlou.im.service.friendship.service;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.dao.entity.FriendShipGroupEntity;
import com.qianlou.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.qianlou.im.service.friendship.model.req.DeleteFriendShipGroupReq;

public interface FriendShipGroupService {

    /**
     * 创建分组
     * @param req
     */
    ResponseVO addGroup(AddFriendShipGroupReq req);

    /**
     * 获取分组
     * @param fromId
     * @param groupName
     * @param appId
     */
    ResponseVO<FriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId);

    /**
     * 删除分组
     * @param req
     */
    ResponseVO deleteGroup(DeleteFriendShipGroupReq req);
}
