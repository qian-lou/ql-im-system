package com.qianlou.im.service.friendship.service;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.model.req.FriendDto;
import com.qianlou.im.service.friendship.model.req.FriendRequestApproveReq;
import com.qianlou.im.service.friendship.model.req.FriendShipRequestGetReq;
import com.qianlou.im.service.friendship.model.req.FriendShipRequestReadReq;

public interface FriendShipRequestService {

    /**
     * 添加好友请求
     * @param fromId 发起请求方
     * @param dto    被请求方
     * @param appId  appId
     */
    ResponseVO addFriendShipRequest(String fromId, FriendDto dto, Integer appId);

    /**
     * 审批好友请求
     * @param req
     */
    ResponseVO approveFriendRequest(FriendRequestApproveReq req);

    /**
     * 更新请求记录为已读
     * @param req
     */
    ResponseVO readFriendShipRequest(FriendShipRequestReadReq req);

    /**
     * 获取所有的申请记录
     * @param req
     */
    ResponseVO getFriendShipRequest(FriendShipRequestGetReq req);
}
