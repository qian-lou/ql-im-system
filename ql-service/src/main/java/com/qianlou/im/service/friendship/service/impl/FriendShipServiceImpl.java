package com.qianlou.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qianlou.im.common.constant.ImConstant;
import com.qianlou.im.common.enums.CheckFriendShipTypeEnum;
import com.qianlou.im.common.enums.FriendAllowTypeEnum;
import com.qianlou.im.common.enums.FriendShipErrorCode;
import com.qianlou.im.common.enums.FriendShipStatusEnum;
import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.dao.FriendShipEntity;
import com.qianlou.im.service.friendship.dao.mapper.FriendShipMapper;
import com.qianlou.im.service.friendship.model.req.*;
import com.qianlou.im.service.friendship.model.resp.CheckFriendShipResp;
import com.qianlou.im.service.friendship.model.resp.ImportFriendShipResp;
import com.qianlou.im.service.friendship.service.FriendShipRequestService;
import com.qianlou.im.service.friendship.service.FriendShipService;
import com.qianlou.im.service.user.dao.UserEntity;
import com.qianlou.im.service.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("all")
@Slf4j
@Service
public class FriendShipServiceImpl implements FriendShipService {

    @Autowired
    private FriendShipMapper friendshipMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendShipRequestService friendshipRequestService;

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
        UserEntity toUserEntity = toInfo.getData();
        if (toUserEntity.getFriendAllowType() != null && toUserEntity.getFriendAllowType() == FriendAllowTypeEnum.NOT_NEED.getCode()) {
            return doAddFriend(req.getFromId(), req.getToItem(), req.getAppId());
        }
        ResponseVO added = friendshipRequestService.addFriendShipRequest(req.getFromId(), req.getToItem(), req.getAppId());
        return added.isOk() ? ResponseVO.successResponse() : added;
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
        queryWrapper.eq(ImConstant.FROM_ID_NAME, req.getFromId());
        queryWrapper.eq(ImConstant.TO_ID_NAME, req.getToId());
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
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
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        queryWrapper.eq(ImConstant.FROM_ID_NAME, req.getFromId());
        queryWrapper.eq("status", FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
        FriendShipEntity update = new FriendShipEntity();
        update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        friendshipMapper.update(update, queryWrapper);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getFriend(GetFriendReq req) {
        QueryWrapper<FriendShipEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        queryWrapper.eq(ImConstant.FROM_ID_NAME, req.getFromId());
        queryWrapper.eq(ImConstant.TO_ID_NAME, req.getToId());
        FriendShipEntity friendShipEntity = friendshipMapper.selectOne(queryWrapper);
        if (friendShipEntity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIENDSHIP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(friendShipEntity);
    }

    @Override
    public ResponseVO getAllFriend(GetAllFriendShipReq req) {
        QueryWrapper<FriendShipEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        queryWrapper.eq(ImConstant.FROM_ID_NAME, req.getFromId());
        return ResponseVO.successResponse(friendshipMapper.selectList(queryWrapper));
    }

    @Override
    public ResponseVO checkFriendship(CheckFriendShipReq req) {
        List<CheckFriendShipResp> resps = new ArrayList<>();
        if (CheckFriendShipTypeEnum.SINGLE.getType() == req.getCheckType()) {
            resps = friendshipMapper.checkFriendShip(req);
        }
        if (CheckFriendShipTypeEnum.BOTH.getType() == req.getCheckType()) {
            resps = friendshipMapper.checkFriendShipBoth(req);
        }
        Map<String, Integer> toReqMap = req.getToIds().stream().collect(Collectors.toMap(Function.identity(), s -> 0));
        Map<String, Integer> toIdFromDbMap = resps.stream().collect(Collectors.toMap(CheckFriendShipResp::getToId, CheckFriendShipResp::getStatus));
        for (Map.Entry<String, Integer> entry : toReqMap.entrySet()) {
            String toId = entry.getKey();
            Integer status = entry.getValue();
            if (!toIdFromDbMap.containsKey(toId)) {
                CheckFriendShipResp resp = new CheckFriendShipResp();
                resp.setFromId(req.getFromId());
                resp.setToId(toId);
                resp.setStatus(status);
                resps.add(resp);
            }
        }
        return ResponseVO.successResponse(resps);
    }

    @Override
    public ResponseVO addBlack(AddFriendShipBlackReq req) {
        ResponseVO<UserEntity> fromInfo = userService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }
        ResponseVO<UserEntity> toInfo = userService.getSingleUserInfo(req.getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }
        QueryWrapper<FriendShipEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        queryWrapper.eq(ImConstant.FROM_ID_NAME, req.getFromId());
        queryWrapper.eq(ImConstant.TO_ID_NAME, req.getToId());
        FriendShipEntity fse = friendshipMapper.selectOne(queryWrapper);
        //不存在关系链
        if (fse == null) {
            fse = new FriendShipEntity();
            fse.setFromId(req.getFromId());
            fse.setToId(req.getToId());
            fse.setAppId(req.getAppId());
            fse.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
            fse.setCreateTime(System.currentTimeMillis());
            int insert = friendshipMapper.insert(fse);
            return insert != 1 ? ResponseVO.errorResponse(FriendShipErrorCode.ADD_BLACK_ERROR) : ResponseVO.successResponse();
        }
        //已经拉黑
        if (fse.getBlack() != null && FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode() == fse.getBlack()) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
        }
        //没拉黑
        FriendShipEntity updateEntity = new FriendShipEntity();
        updateEntity.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
        int update = friendshipMapper.update(updateEntity, queryWrapper);
        return update != 1 ? ResponseVO.errorResponse(FriendShipErrorCode.ADD_BLACK_ERROR) : ResponseVO.successResponse();
    }

    @Override
    public ResponseVO deleteBlack(DeleteBlackReq req) {
        QueryWrapper<FriendShipEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        queryWrapper.eq(ImConstant.FROM_ID_NAME, req.getFromId());
        queryWrapper.eq(ImConstant.TO_ID_NAME, req.getToId());
        FriendShipEntity shipEntity = friendshipMapper.selectOne(queryWrapper);
        if (shipEntity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIENDSHIP_IS_NOT_EXIST);
        }
        if (shipEntity.getBlack() != null && FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode() == shipEntity.getBlack()) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_NOT_YOUR_BLACK);
        }
        FriendShipEntity updateEntity = new FriendShipEntity();
        updateEntity.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
        int update = friendshipMapper.update(updateEntity, queryWrapper);
        return update != 1 ? ResponseVO.errorResponse(FriendShipErrorCode.DELETE_BLACK_ERROR) : ResponseVO.successResponse();
    }

    @Override
    public ResponseVO checkBlack(CheckFriendShipReq req) {
        List<CheckFriendShipResp> resps = new ArrayList<>();
        if (CheckFriendShipTypeEnum.SINGLE.getType() == req.getCheckType()) {
            resps = friendshipMapper.checkBlack(req);
        }
        if (CheckFriendShipTypeEnum.BOTH.getType() == req.getCheckType()) {
            resps = friendshipMapper.checkBlackBoth(req);
        }
        Map<String, Integer> toReqMap = req.getToIds().stream().collect(Collectors.toMap(Function.identity(), s -> 0));
        Map<String, Integer> toIdFromDbMap = resps.stream().collect(Collectors.toMap(CheckFriendShipResp::getToId, CheckFriendShipResp::getStatus));
        for (Map.Entry<String, Integer> entry : toReqMap.entrySet()) {
            String toId = entry.getKey();
            Integer status = entry.getValue();
            if (!toIdFromDbMap.containsKey(toId)) {
                CheckFriendShipResp resp = new CheckFriendShipResp();
                resp.setFromId(req.getFromId());
                resp.setToId(toId);
                resp.setStatus(status);
                resps.add(resp);
            }
        }
        return ResponseVO.successResponse(resps);
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
        ResponseVO fromVo = doAddFriend(fromId, dto.getToId(), dto, appId);
        if (!fromVo.isOk()) {
            return fromVo;
        }
        ResponseVO toVo = doAddFriend(dto.getToId(), fromId, dto, appId);
        if (!toVo.isOk()) {
            return toVo;
        }
        return ResponseVO.successResponse();
    }

    private ResponseVO doAddFriend(String fromId, String toId, FriendDto dto, Integer appId) {
        //A-B
        QueryWrapper<FriendShipEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.APP_ID_NAME, appId);
        queryWrapper.eq(ImConstant.FROM_ID_NAME, fromId);
        queryWrapper.eq(ImConstant.TO_ID_NAME, toId);

        FriendShipEntity fromEntity = friendshipMapper.selectOne(queryWrapper);
        if (fromEntity == null) {
            fromEntity = new FriendShipEntity();
            fromEntity.setAppId(appId);
            fromEntity.setFromId(fromId);
            fromEntity.setToId(toId);
            fromEntity.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromEntity.setRemark(dto.getRemark());
            fromEntity.setExtra(dto.getExtra());
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
