package com.qianlou.im.service.group.controller;


import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.group.model.req.*;
import com.qianlou.im.service.group.service.GroupMemberService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "群成员服务")
@RestController
@RequestMapping("/v1/group/member")
public class GroupMemberController {

    @Autowired
    private GroupMemberService groupMemberService;

    @PostMapping("/import")
    public ResponseVO importGroupMember(@RequestBody @Validated ImportGroupMemberReq req)  {
        return groupMemberService.importGroupMember(req);
    }

    @PostMapping("/add")
    public ResponseVO addMember(@RequestBody @Validated AddGroupMemberReq req)  {
        return groupMemberService.addMember(req);
    }

    @PostMapping("/remove")
    public ResponseVO removeMember(@RequestBody @Validated RemoveGroupMemberReq req)  {
        return groupMemberService.removeMember(req);
    }

    @PostMapping("/update")
    public ResponseVO updateGroupMember(@RequestBody @Validated UpdateGroupMemberReq req)  {
        return groupMemberService.updateGroupMember(req);
    }

    @PostMapping("/speak")
    public ResponseVO speak(@RequestBody @Validated SpeaMemberReq req)  {
        return groupMemberService.speak(req);
    }

}
