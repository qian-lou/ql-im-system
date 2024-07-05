package com.qianlou.im.service.friendship.service;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.model.req.AddFriendShipGroupMemberReq;

public interface FriendShipGroupMemberService {

    /**
     * 创建分组成员
     * @param req
     */
    ResponseVO addGroupMember(AddFriendShipGroupMemberReq req);

    int doAddGroupMember(Long groupId, String toId);

    /**
     * 情况分组下成员
     * @param groupId
     */
    int clearGroupMember(Long groupId);
}
