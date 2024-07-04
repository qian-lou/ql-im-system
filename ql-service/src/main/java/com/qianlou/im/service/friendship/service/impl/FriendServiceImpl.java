package com.qianlou.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qianlou.im.common.constant.ImConstant;
import com.qianlou.im.common.enums.CheckFriendShipTypeEnum;
import com.qianlou.im.common.enums.FriendShipErrorCode;
import com.qianlou.im.common.enums.FriendShipStatusEnum;
import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.dao.FriendShipEntity;
import com.qianlou.im.service.friendship.dao.mapper.FriendshipMapper;
import com.qianlou.im.service.friendship.model.req.*;
import com.qianlou.im.service.friendship.model.resp.ImportFriendShipResp;
import com.qianlou.im.service.friendship.service.FriendService;
import com.qianlou.im.service.user.dao.UserEntity;
import com.qianlou.im.service.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
@Slf4j
@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Autowired
    private UserService userService;

    @Override
    public ResponseVO importFriendShip(ImportFriendShipReq req) {
        if (req.getFriendList().size() > ImConstant.FRIENDSHIP_IMPORT_NUM_MAX) {
            return ResponseVO.errorResponse(FriendShipErrorCode.IMPORT_SIZE_BEYOND);
        }
        List<String> successIds = new ArrayList<>();
        List<String> failIds = new ArrayList<>();
        List<ImportFriendShipReq.ImportFriendDto> friendItem = req.getFriendList();
        for (ImportFriendShipReq.ImportFriendDto friendDto : friendItem) {
            FriendShipEntity friendShipEntity = new FriendShipEntity();
            BeanUtils.copyProperties(friendDto, friendShipEntity);
            friendShipEntity.setAppId(req.getAppId());
            friendShipEntity.setFromId(req.getFromId());
            try {
                int insert = friendshipMapper.insert(friendShipEntity);
                if (insert == 1) {
                    successIds.add(friendDto.getToId());
                } else {
                    failIds.add(friendDto.getToId());
                }
            } catch (Exception ex) {
                log.info("[appId={}, fromId={}, toId={}]关系导入出现异常, 异常信息: {}",
                        req.getAppId(), req.getFromId(), friendDto.getToId(), ex.getMessage());
                failIds.add(friendDto.getToId());
            }
        }
        return ResponseVO.successResponse(new ImportFriendShipResp(successIds, failIds));
    }

    @Override
    public ResponseVO addFriend(AddFriendReq req) {
        ResponseVO<UserEntity> fromInfo = userService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }
        ResponseVO<UserEntity> toInfo = userService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }
        return doAddFriend(req.getFromId(), req.getToItem(), req.getAppId());
    }

    @Override
    public ResponseVO updateFriend(UpdateFriendReq req) {
        ResponseVO<UserEntity> fromInfo = userService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }
        ResponseVO<UserEntity> toInfo = userService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }
        return doUpdateFriend(req.getFromId(), req.getToItem(), req.getAppId());
    }

    @Override
    public ResponseVO deleteFriend(DeleteFriendReq req) {
        QueryWrapper<FriendShipEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("from_id", req.getFromId());
        queryWrapper.eq("to_id", req.getToId());
        queryWrapper.eq("app_id", req.getAppId());
        FriendShipEntity friendShipEntity = friendshipMapper.selectOne(queryWrapper);
        if (friendShipEntity == null) {
            //不是好友
            return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        }
        if (FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode() == friendShipEntity.getStatus()) {
            //该好友已经删除
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
        }
        if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() == friendShipEntity.getStatus()) {
            //是好友，可删除
            FriendShipEntity update = new FriendShipEntity();
            update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
            friendshipMapper.update(update, queryWrapper);
            return ResponseVO.successResponse();
        }
        //TODO 其他状态
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO deleteAllFriend(DeleteFriendReq req) {
        QueryWrapper<FriendShipEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.eq("from_id", req.getFromId());
        queryWrapper.eq("status", FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
        FriendShipEntity update = new FriendShipEntity();
        update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        friendshipMapper.update(update, queryWrapper);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getFriend(GetFriendReq req) {
        QueryWrapper<FriendShipEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.eq("from_id", req.getFromId());
        queryWrapper.eq("to_id", req.getToId());
        FriendShipEntity friendShipEntity = friendshipMapper.selectOne(queryWrapper);
        if (friendShipEntity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIENDSHIP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(friendShipEntity);
    }

    @Override
    public ResponseVO getAllFriend(GetAllFriendShipReq req) {
        QueryWrapper<FriendShipEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.eq("from_id", req.getFromId());
        return ResponseVO.successResponse(friendshipMapper.selectList(queryWrapper));
    }

    @Override
    public ResponseVO checkFriendship(CheckFriendShipReq req) {
        if (CheckFriendShipTypeEnum.SINGLE.getType() == req.getCheckType()) {
            return ResponseVO.successResponse(friendshipMapper.checkFriendShip(req));
        }
        return null;
    }

    public ResponseVO doUpdateFriend(String fromId, FriendDto dto, Integer appId) {
        UpdateWrapper<FriendShipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(FriendShipEntity::getAddSource, dto.getAddSource())
                .set(FriendShipEntity::getRemark, dto.getRemark())
                .set(FriendShipEntity::getExtra, dto.getExtra())
                .eq(FriendShipEntity::getFromId, fromId)
                .eq(FriendShipEntity::getToId, dto.getToId())
                .eq(FriendShipEntity::getAppId, appId);
        friendshipMapper.update(null, updateWrapper);
        return ResponseVO.successResponse();
    }

    @Transactional
    public ResponseVO doAddFriend(String fromId, FriendDto dto, Integer appId) {
        //A-B
        //添加A->B 和B->A
        QueryWrapper<FriendShipEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId);
        queryWrapper.eq("from_id", fromId);
        queryWrapper.eq("to_id", dto.getToId());

        FriendShipEntity fromEntity = friendshipMapper.selectOne(queryWrapper);
        if (fromEntity == null) {
            fromEntity = new FriendShipEntity();
            BeanUtils.copyProperties(dto, fromEntity);
            fromEntity.setAppId(appId);
            fromEntity.setFromId(fromId);
            fromEntity.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromEntity.setCreateTime(System.currentTimeMillis());
            int insert = friendshipMapper.insert(fromEntity);
            return insert == 1 ? ResponseVO.successResponse() : ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
        }
        if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() == fromEntity.getStatus()) {
            return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
        }
        FriendShipEntity update = new FriendShipEntity();
        update.setAddSource(dto.getAddSource());
        update.setRemark(dto.getRemark());
        update.setExtra(dto.getExtra());
        update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
        int updated = friendshipMapper.update(update, queryWrapper);
        return updated == 1 ? ResponseVO.successResponse() : ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
    }
}
