package com.qianlou.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qianlou.im.common.enums.FriendShipErrorCode;
import com.qianlou.im.common.enums.FriendShipStatusEnum;
import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.dao.FriendShipEntity;
import com.qianlou.im.service.friendship.dao.mapper.FriendshipMapper;
import com.qianlou.im.service.friendship.model.req.AddFriendReq;
import com.qianlou.im.service.friendship.model.req.FriendDto;
import com.qianlou.im.service.friendship.model.req.ImportFriendShipReq;
import com.qianlou.im.service.friendship.model.req.UpdateFriendReq;
import com.qianlou.im.service.friendship.model.resp.ImportFriendShipResp;
import com.qianlou.im.service.friendship.service.FriendService;
import com.qianlou.im.service.user.dao.UserEntity;
import com.qianlou.im.service.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Autowired
    private UserService userService;

    @Override
    public ResponseVO importFriendShip(ImportFriendShipReq req) {
        if (req.getFriendItem().size() > 100) {
            return ResponseVO.errorResponse(FriendShipErrorCode.IMPORT_SIZE_BEYOND);
        }
        List<String> successIds = new ArrayList<>();
        List<String> failIds = new ArrayList<>();
        List<ImportFriendShipReq.ImportFriendDto> friendItem = req.getFriendItem();
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
        return null;
    }

    public ResponseVO doUpdateFriend(String fromId, FriendDto dto, Integer appId) {
        QueryWrapper<FriendShipEntity> queryWrapper = new QueryWrapper<>();
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
            //默认添加状态
            fromEntity.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromEntity.setCreateTime(System.currentTimeMillis());
            int insert = friendshipMapper.insert(fromEntity);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() == fromEntity.getStatus()) {
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            }
            FriendShipEntity update = new FriendShipEntity();
            if (StringUtils.isNotBlank(dto.getAddSource())) {
                update.setAddSource(dto.getAddSource());
            }
            if(StringUtils.isNotBlank(dto.getRemark())){
                update.setRemark(dto.getRemark());
            }
            if(StringUtils.isNotBlank(dto.getExtra())){
                update.setExtra(dto.getExtra());
            }
            update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            int updated = friendshipMapper.update(update, queryWrapper);
            if (updated != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        }
        return ResponseVO.successResponse();
    }
}
