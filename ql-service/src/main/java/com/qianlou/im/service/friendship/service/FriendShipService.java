package com.qianlou.im.service.friendship.service;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.model.req.*;

public interface FriendShipService {

    /**
     * 导入朋友关系
     * @param req
     */
    ResponseVO importFriendShip(ImportFriendShipReq req);

    /**
     * 添加好友
     * @param req
     */
    ResponseVO addFriend(AddFriendReq req);

    ResponseVO doAddFriend(String fromId, FriendDto dto, Integer appId);

    /**
     * 更新好友
     * @param req
     */
    ResponseVO updateFriend(UpdateFriendReq req);

    /**
     * 删除好友
     * @param req
     */
    ResponseVO deleteFriend(DeleteFriendReq req);

    /**
     * 删除所有好友
     * @param req
     */
    ResponseVO deleteAllFriend(DeleteFriendReq req);

    /**
     * 获取指定好友
     * @param req
     */
    ResponseVO getFriend(GetFriendReq req);

    /**
     * 获取所有好友
     * @param req
     */
    ResponseVO getAllFriend(GetAllFriendShipReq req);

    /**
     * 检查好友关系
     * @param req
     */
    ResponseVO checkFriendship(CheckFriendShipReq req);

    /**
     * 添加黑名单
     * @param req
     */
    ResponseVO addBlack(AddFriendShipBlackReq req);

    /**
     * 删除黑名单
     * @param req
     */
    ResponseVO deleteBlack(DeleteBlackReq req);

    /**
     * 校验黑名单
     * @param req
     */
    ResponseVO checkBlack(CheckFriendShipReq req);
}
