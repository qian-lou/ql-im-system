package com.qianlou.im.service.friendship.service;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.model.req.AddFriendReq;
import com.qianlou.im.service.friendship.model.req.ImportFriendShipReq;
import com.qianlou.im.service.friendship.model.req.UpdateFriendReq;

public interface FriendService {

    ResponseVO importFriendShip(ImportFriendShipReq req);

    ResponseVO addFriend(AddFriendReq req);

    ResponseVO updateFriend(UpdateFriendReq req);
}
