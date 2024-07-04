package com.qianlou.im.service.user.controller;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.user.model.req.GetUserInfoReq;
import com.qianlou.im.service.user.model.req.ImportUserReq;
import com.qianlou.im.service.user.model.resp.GetUserInfoResp;
import com.qianlou.im.service.user.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@Api(tags = "用户服务")
@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/import")
    public ResponseVO importUser(@RequestBody ImportUserReq req) {
        return userService.importUser(req);
    }

    @PostMapping("/getInfo")
    public ResponseVO<GetUserInfoResp> getUserInfo(@RequestBody GetUserInfoReq req) {
        return userService.getUserInfo(req);
    }

}
