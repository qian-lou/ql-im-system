package com.qianlou.im.service.friendship.controller;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.model.req.*;
import com.qianlou.im.service.friendship.service.FriendShipService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "朋友关系服务")
@RestController
@RequestMapping("/v1/friendship")
public class FriendShipController {

    @Autowired
    private FriendShipService friendService;

    @PostMapping("/import")
    public ResponseVO importFriends(@RequestBody ImportFriendShipReq req) {
        return friendService.importFriendShip(req);
    }

    @PostMapping("/add")
    public ResponseVO addFriend(@RequestBody @Validated AddFriendReq req) {
        return friendService.addFriend(req);
    }

    @PostMapping("/update")
    public ResponseVO updateFriend(@RequestBody @Validated UpdateFriendReq req) {
        return friendService.updateFriend(req);
    }

    @PostMapping("/deleteOne")
    public ResponseVO deleteFriend(@RequestBody @Validated DeleteFriendReq req) {
        return friendService.deleteFriend(req);
    }

    @PostMapping("/deleteAll")
    public ResponseVO deleteAllFriend(@RequestBody @Validated DeleteFriendReq req) {
        return friendService.deleteAllFriend(req);
    }

    @PostMapping("/getOne")
    public ResponseVO getFriend(@RequestBody @Validated GetFriendReq req) {
        return friendService.getFriend(req);
    }

    @PostMapping("/getAll")
    public ResponseVO getAllFriend(@RequestBody @Validated GetAllFriendShipReq req) {
        return friendService.getAllFriend(req);
    }

    @PostMapping("/checkFriendship")
    public ResponseVO checkFriendship(@RequestBody @Validated CheckFriendShipReq req) {
        return friendService.checkFriendship(req);
    }

    @PostMapping("/addBlack")
    public ResponseVO addBlack(@RequestBody @Validated AddFriendShipBlackReq req) {
        return friendService.addBlack(req);
    }

    @PostMapping("/deleteBlack")
    public ResponseVO deleteBlack(@RequestBody @Validated DeleteBlackReq req) {
        return friendService.deleteBlack(req);
    }

    @PostMapping("/checkBlack")
    public ResponseVO checkBlack(@RequestBody @Validated CheckFriendShipReq req) {
        return friendService.checkBlack(req);
    }
}
