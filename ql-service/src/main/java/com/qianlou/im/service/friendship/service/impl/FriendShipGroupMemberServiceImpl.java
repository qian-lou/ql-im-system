package com.qianlou.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qianlou.im.common.constant.ImConstant;
import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.dao.entity.FriendShipGroupEntity;
import com.qianlou.im.service.friendship.dao.entity.FriendShipGroupMemberEntity;
import com.qianlou.im.service.friendship.dao.mapper.FriendShipGroupMemberMapper;
import com.qianlou.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.qianlou.im.service.friendship.service.FriendShipGroupMemberService;
import com.qianlou.im.service.friendship.service.FriendShipGroupService;
import com.qianlou.im.service.user.dao.UserEntity;
import com.qianlou.im.service.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FriendShipGroupMemberServiceImpl implements FriendShipGroupMemberService {

    @Autowired
    private FriendShipGroupMemberMapper friendShipGroupMemberMapper;

    @Autowired
    private FriendShipGroupService friendShipGroupService;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendShipGroupMemberService friendShipGroupMemberService;

    @Override
    public ResponseVO addGroupMember(AddFriendShipGroupMemberReq req) {
        ResponseVO<FriendShipGroupEntity> group = friendShipGroupService.getGroup(req.getFromId(), req.getGroupName(), req.getAppId());
        if (!group.isOk()) {
            return group;
        }
        List<String> successIds = new ArrayList<>();
        List<String> toIds = req.getToIds();
        for (String toId : toIds) {
            ResponseVO<UserEntity> userInfo = userService.getSingleUserInfo(toId, req.getAppId());
            if (userInfo.isOk()) {
                int add = friendShipGroupMemberService.doAddGroupMember(group.getData().getGroupId(), toId);
                if (add == 1) successIds.add(toId);
            }
        }
        return ResponseVO.successResponse(successIds);
    }

    @Transactional
    @Override
    public int doAddGroupMember(Long groupId, String toId) {
        FriendShipGroupMemberEntity entity = new FriendShipGroupMemberEntity();
        entity.setGroupId(groupId);
        entity.setToId(toId);
        try {
            return friendShipGroupMemberMapper.insert(entity);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public int clearGroupMember(Long groupId) {
        QueryWrapper<FriendShipGroupMemberEntity> query = new QueryWrapper<>();
        query.eq(ImConstant.GROUP_ID_NAME,groupId);
        return friendShipGroupMemberMapper.delete(query);
    }
}
