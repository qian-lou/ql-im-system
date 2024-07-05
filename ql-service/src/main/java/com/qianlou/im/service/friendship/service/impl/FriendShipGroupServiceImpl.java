package com.qianlou.im.service.friendship.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qianlou.im.common.constant.ImConstant;
import com.qianlou.im.common.enums.DelFlagEnum;
import com.qianlou.im.common.enums.FriendShipErrorCode;
import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.dao.entity.FriendShipGroupEntity;
import com.qianlou.im.service.friendship.dao.mapper.FriendShipGroupMapper;
import com.qianlou.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.qianlou.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.qianlou.im.service.friendship.model.req.DeleteFriendShipGroupReq;
import com.qianlou.im.service.friendship.service.FriendShipGroupMemberService;
import com.qianlou.im.service.friendship.service.FriendShipGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("all")
@Slf4j
@Service
public class FriendShipGroupServiceImpl implements FriendShipGroupService {

    @Autowired
    private FriendShipGroupMapper friendShipGroupMapper;

    @Autowired
    private FriendShipGroupMemberService friendShipGroupMemberService;

    @Override
    public ResponseVO addGroup(AddFriendShipGroupReq req) {
        QueryWrapper<FriendShipGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.GROUP_NAME, req.getGroupName());
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        queryWrapper.eq(ImConstant.FROM_ID_NAME, req.getFromId());
        queryWrapper.eq(ImConstant.DELETE_FLAG_NAME, DelFlagEnum.NORMAL.getCode());
        FriendShipGroupEntity queryEntity = friendShipGroupMapper.selectOne(queryWrapper);
        if (queryEntity != null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        FriendShipGroupEntity entity = new FriendShipGroupEntity();
        entity.setAppId(req.getAppId());
        entity.setCreateTime(System.currentTimeMillis());
        entity.setGroupName(req.getGroupName());
        entity.setFromId(req.getFromId());
        entity.setDelFlag(DelFlagEnum.NORMAL.getCode());
        try {
            int insert = friendShipGroupMapper.insert(entity);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
            }
            if (CollectionUtil.isNotEmpty(req.getToIds())) {
                AddFriendShipGroupMemberReq memberReq = new AddFriendShipGroupMemberReq();
                memberReq.setFromId(req.getFromId());
                memberReq.setToIds(req.getToIds());
                memberReq.setGroupName(req.getGroupName());
                memberReq.setAppId(req.getAppId());
                friendShipGroupMemberService.addGroupMember(memberReq);
                return ResponseVO.successResponse();
            }
        } catch (DuplicateKeyException ex) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getGroup(String fromId, String groupName, Integer appId) {
        QueryWrapper<FriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq(ImConstant.GROUP_NAME, groupName);
        query.eq(ImConstant.APP_ID_NAME, appId);
        query.eq(ImConstant.FROM_ID_NAME, fromId);
        query.eq(ImConstant.DELETE_FLAG_NAME, DelFlagEnum.NORMAL.getCode());
        FriendShipGroupEntity entity = friendShipGroupMapper.selectOne(query);
        return entity == null ?
                ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST)
                : ResponseVO.successResponse(entity);
    }


    @Override
    @Transactional
    public ResponseVO deleteGroup(DeleteFriendShipGroupReq req) {
        for (String groupName : req.getGroupName()) {
            QueryWrapper<FriendShipGroupEntity> query = new QueryWrapper<>();
            query.eq(ImConstant.GROUP_NAME, groupName);
            query.eq(ImConstant.APP_ID_NAME, req.getAppId());
            query.eq(ImConstant.FROM_ID_NAME, req.getFromId());
            query.eq(ImConstant.DELETE_FLAG_NAME, DelFlagEnum.NORMAL.getCode());
            FriendShipGroupEntity entity = friendShipGroupMapper.selectOne(query);
            if (entity != null) {
                FriendShipGroupEntity update = new FriendShipGroupEntity();
                update.setGroupId(entity.getGroupId());
                update.setDelFlag(DelFlagEnum.DELETE.getCode());
                friendShipGroupMapper.updateById(update);
                friendShipGroupMemberService.clearGroupMember(entity.getGroupId());
            }
        }
        return ResponseVO.successResponse();
    }
}
