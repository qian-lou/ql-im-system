package com.qianlou.im.service.user.controller;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.user.model.req.ImportUserReq;
import com.qianlou.im.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/importUser")
    public ResponseVO importUser(@RequestBody ImportUserReq req) {
        return userService.importUser(req);
    }

}
