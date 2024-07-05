package com.qianlou.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qianlou.im.common.constant.ImConstant;
import com.qianlou.im.common.enums.FriendShipErrorCode;
import com.qianlou.im.common.enums.FriendShipRequestApproverStatusEnum;
import com.qianlou.im.common.enums.FriendShipRequestReadStatusEnum;
import com.qianlou.im.common.execption.ApplicationException;
import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.friendship.dao.entity.FriendShipRequestEntity;
import com.qianlou.im.service.friendship.dao.mapper.FriendShipRequestMapper;
import com.qianlou.im.service.friendship.model.req.FriendDto;
import com.qianlou.im.service.friendship.model.req.FriendRequestApproveReq;
import com.qianlou.im.service.friendship.model.req.FriendShipRequestGetReq;
import com.qianlou.im.service.friendship.model.req.FriendShipRequestReadReq;
import com.qianlou.im.service.friendship.service.FriendShipRequestService;
import com.qianlou.im.service.friendship.service.FriendShipService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("all")
@Slf4j
@Service
public class FriendShipRequestServiceImpl implements FriendShipRequestService {

    @Autowired
    private FriendShipRequestMapper friendShipRequestMapper;

    @Autowired
    private FriendShipService friendShipService;

    @Override
    public ResponseVO addFriendShipRequest(String fromId, FriendDto dto, Integer appId) {
        QueryWrapper<FriendShipRequestEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.FROM_ID_NAME, fromId);
        queryWrapper.eq(ImConstant.TO_ID_NAME, dto.getToId());
        queryWrapper.eq(ImConstant.APP_ID_NAME, appId);
        FriendShipRequestEntity requestEntity = friendShipRequestMapper.selectOne(queryWrapper);
        if (requestEntity == null) {
            requestEntity = new FriendShipRequestEntity();
            requestEntity.setFromId(fromId);
            requestEntity.setToId(dto.getToId());
            requestEntity.setAppId(appId);
            requestEntity.setAddMessage(dto.getAddMessage());
            requestEntity.setAddSource(dto.getAddSource());
            requestEntity.setReadStatus(FriendShipRequestReadStatusEnum.UNREAD.getCode());
            requestEntity.setApproveStatus(FriendShipRequestApproverStatusEnum.DEFAULT.getCode());
            requestEntity.setRemark(dto.getRemark());
            requestEntity.setCreateTime(System.currentTimeMillis());
            requestEntity.setUpdateTime(System.currentTimeMillis());
            friendShipRequestMapper.insert(requestEntity);
            return ResponseVO.successResponse();
        }
        if (StringUtils.isNotBlank(requestEntity.getRemark())) {
            requestEntity.setRemark(dto.getRemark());
        }
        if (StringUtils.isNotBlank(requestEntity.getAddSource())) {
            requestEntity.setAddSource(dto.getAddSource());
        }
        if (StringUtils.isNotBlank(requestEntity.getAddMessage())) {
            requestEntity.setAddMessage(dto.getAddMessage());
        }
        friendShipRequestMapper.updateById(requestEntity);
        return ResponseVO.successResponse();
    }

    @Transactional
    @Override
    public ResponseVO approveFriendRequest(FriendRequestApproveReq req) {
        FriendShipRequestEntity friendShipRequestEntity = friendShipRequestMapper.selectById(req.getId());
        if (friendShipRequestEntity == null) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_REQUEST_IS_NOT_EXIST);
        }
        if (!req.getOperator().equals(friendShipRequestEntity.getToId())) {
            //只能审批发给自己的好友请求
            throw new ApplicationException(FriendShipErrorCode.NOT_APPROVE_OTHER_MAN_REQUEST);
        }
        FriendShipRequestEntity update = new FriendShipRequestEntity();
        update.setApproveStatus(req.getStatus());
        update.setUpdateTime(System.currentTimeMillis());
        update.setId(req.getId());
        friendShipRequestMapper.updateById(update);
        if (FriendShipRequestApproverStatusEnum.AGREE.getCode() == req.getStatus()) {
            //同意 ===> 去执行添加好友逻辑
            FriendDto dto = new FriendDto();
            dto.setAddSource(friendShipRequestEntity.getAddSource());
            dto.setAddMessage(friendShipRequestEntity.getAddMessage());
            dto.setRemark(friendShipRequestEntity.getRemark());
            dto.setToId(friendShipRequestEntity.getToId());
            ResponseVO responseVO = friendShipService.doAddFriend(friendShipRequestEntity.getFromId(), dto, req.getAppId());
            if (!responseVO.isOk() && responseVO.getCode() != FriendShipErrorCode.TO_IS_YOUR_FRIEND.getCode()) {
                return responseVO;
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO readFriendShipRequest(FriendShipRequestReadReq req) {
        QueryWrapper<FriendShipRequestEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.FROM_ID_NAME, req.getFromId());
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        FriendShipRequestEntity update = new FriendShipRequestEntity();
        update.setReadStatus(FriendShipRequestReadStatusEnum.READ.getCode());
        friendShipRequestMapper.update(update, queryWrapper);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getFriendShipRequest(FriendShipRequestGetReq req) {
        QueryWrapper<FriendShipRequestEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.TO_ID_NAME, req.getFromId());
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        return ResponseVO.successResponse(friendShipRequestMapper.selectList(queryWrapper));
    }
}
