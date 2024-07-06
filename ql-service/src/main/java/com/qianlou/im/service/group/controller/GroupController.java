package com.qianlou.im.service.group.controller;


import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.group.model.req.*;
import com.qianlou.im.service.group.service.GroupService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "群组服务")
@RestController
@RequestMapping("/v1/group")
public class GroupController {

    @Autowired
    private GroupService groupService;


    @PostMapping("/import")
    public ResponseVO importGroup(@RequestBody @Validated ImportGroupReq req) {
        return groupService.importGroup(req);
    }

    @PostMapping("/create")
    public ResponseVO createGroup(@RequestBody @Validated CreateGroupReq req) {
        return groupService.createGroup(req);
    }

    @PostMapping("/get")
    public ResponseVO getGroupInfo(@RequestBody @Validated GetGroupReq req) {
        return groupService.getGroup(req);
    }

    @PostMapping("/update")
    public ResponseVO update(@RequestBody @Validated UpdateGroupReq req) {
        return groupService.updateGroupInfo(req);
    }

    @PostMapping("/getJoined")
    public ResponseVO getJoinedGroup(@RequestBody @Validated GetJoinedGroupReq req) {
        return groupService.getJoinedGroup(req);
    }

    @PostMapping("/destroy")
    public ResponseVO destroyGroup(@RequestBody @Validated DestroyGroupReq req) {
        return groupService.destroyGroup(req);
    }

    @PostMapping("/transfer")
    public ResponseVO transferGroup(@RequestBody @Validated TransferGroupReq req) {
        return groupService.transferGroup(req);
    }

    @PostMapping("/forbidSendMessage")
    public ResponseVO forbidSendMessage(@RequestBody @Validated MuteGroupReq req) {
        return groupService.muteGroup(req);
    }


}
