package com.qianlou.im.service.friendship.controller;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.qianlou.im.service.friendship.model.req.DeleteFriendShipGroupReq;
import com.qianlou.im.service.friendship.service.FriendShipGroupService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "分组服务")
@RequestMapping("/v1/friendship/group")
@RestController
public class FriendShipGroupController {


    @Autowired
    private FriendShipGroupService friendShipGroupService;


    @PostMapping("/add")
    public ResponseVO add(@RequestBody @Validated AddFriendShipGroupReq req) {
        return friendShipGroupService.addGroup(req);
    }

    @PostMapping("/del")
    public ResponseVO delete(@RequestBody @Validated DeleteFriendShipGroupReq req) {
        return friendShipGroupService.deleteGroup(req);
    }
}
