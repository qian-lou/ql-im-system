package com.qianlou.im.service.friendship.controller;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.model.req.ImportFriendShipReq;
import com.qianlou.im.service.friendship.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/friendship")
public class FriendShipController {


    @Autowired
    private FriendService friendService;


    @PostMapping("/import")
    public ResponseVO importFriends(@RequestBody ImportFriendShipReq req) {
        return friendService.importFriendShip(req);
    }
}
