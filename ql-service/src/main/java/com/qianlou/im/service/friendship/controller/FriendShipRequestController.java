package com.qianlou.im.service.friendship.controller;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.model.req.*;
import com.qianlou.im.service.friendship.service.FriendShipRequestService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "审批好友服务")
@RestController
@RequestMapping("/v1/friendRequest")
public class FriendShipRequestController {

    @Autowired
    private FriendShipRequestService friendShipRequestService;


    @PostMapping("/approve")
    public ResponseVO approveFriendRequest(@RequestBody @Validated FriendRequestApproveReq req) {
        return friendShipRequestService.approveFriendRequest(req);
    }

    @PostMapping("/read")
    public ResponseVO readFriendShipRequest(@RequestBody @Validated FriendShipRequestReadReq req) {
        return friendShipRequestService.readFriendShipRequest(req);
    }

    @PostMapping("/get")
    public ResponseVO getFriendShipRequest(@RequestBody @Validated FriendShipRequestGetReq req) {
        return friendShipRequestService.getFriendShipRequest(req);
    }

}
