package com.qianlou.im.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qianlou.im.common.constant.ImConstant;
import com.qianlou.im.common.enums.DelFlagEnum;
import com.qianlou.im.common.enums.FriendAllowTypeEnum;
import com.qianlou.im.common.enums.UserErrorCode;
import com.qianlou.im.common.execption.ApplicationException;
import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.user.dao.entity.UserEntity;
import com.qianlou.im.service.user.dao.mapper.UserMapper;
import com.qianlou.im.service.user.model.req.*;
import com.qianlou.im.service.user.model.resp.GetUserInfoResp;
import com.qianlou.im.service.user.model.resp.ImportUserResp;
import com.qianlou.im.service.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseVO importUser(ImportUserReq req) {
        if (req.getUserList().size() > ImConstant.USER_IMPORT_NUM_MIN) {
            return ResponseVO.errorResponse(UserErrorCode.IMPORT_SIZE_BEYOND);
        }
        ImportUserResp resp = new ImportUserResp();
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();
        for (UserEntity data : req.getUserList()) {
            try {
                data.setAppId(req.getAppId());
                data.setFriendAllowType(FriendAllowTypeEnum.NEED.getCode());
                data.setDelFlag(DelFlagEnum.NORMAL.getCode());
                int insert = userMapper.insert(data);
                if (insert == 1) {
                    successId.add(data.getUserId());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                errorId.add(data.getUserId());
            }
        }
        resp.setFailIds(errorId);
        resp.setSuccessIds(successId);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        queryWrapper.in(ImConstant.USER_ID_NAME, req.getUserIds());
        queryWrapper.eq(ImConstant.DELETE_FLAG_NAME, DelFlagEnum.NORMAL.getCode());
        List<UserEntity> userDataEntities = userMapper.selectList(queryWrapper);
        HashMap<String, UserEntity> map = new HashMap<>();
        for (UserEntity data : userDataEntities) {
            map.put(data.getUserId(), data);
        }
        List<String> failUser = new ArrayList<>();
        for (String uid : req.getUserIds()) {
            if (!map.containsKey(uid)) {
                failUser.add(uid);
            }
        }
        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(userDataEntities);
        resp.setFailUser(failUser);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<UserEntity> getSingleUserInfo(String userId, Integer appId) {
        QueryWrapper objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq(ImConstant.APP_ID_NAME, appId);
        objectQueryWrapper.eq(ImConstant.USER_ID_NAME, userId);
        objectQueryWrapper.eq(ImConstant.DELETE_FLAG_NAME, DelFlagEnum.NORMAL.getCode());
        UserEntity userEntity = userMapper.selectOne(objectQueryWrapper);
        if (userEntity == null) {
            return ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(userEntity);
    }


    @Override
    public ResponseVO deleteUser(DeleteUserReq req) {
        UserEntity entity = new UserEntity();
        entity.setDelFlag(DelFlagEnum.DELETE.getCode());
        List<String> failId = new ArrayList<>();
        List<String> successId = new ArrayList<>();
        for (String userId : req.getUserId()) {
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
            wrapper.eq(ImConstant.USER_ID_NAME, userId);
            wrapper.eq(ImConstant.DELETE_FLAG_NAME, DelFlagEnum.NORMAL.getCode());
            try {
                int update = userMapper.update(entity, wrapper);
                if (update > 1) {
                    successId.add(userId);
                } else {
                    failId.add(userId);
                }
            } catch (Exception e) {
                failId.add(userId);
            }
        }

        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessIds(successId);
        resp.setFailIds(failId);
        return ResponseVO.successResponse(resp);
    }

    @Override
    @Transactional
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req) {
        QueryWrapper query = new QueryWrapper<>();
        query.eq(ImConstant.APP_ID_NAME, req.getAppId());
        query.eq(ImConstant.USER_ID_NAME, req.getUserId());
        query.eq(ImConstant.DELETE_FLAG_NAME, DelFlagEnum.NORMAL.getCode());
        UserEntity user = userMapper.selectOne(query);
        if (user == null) {
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        UserEntity update = new UserEntity();
        BeanUtils.copyProperties(req, update);

        update.setAppId(null);
        update.setUserId(null);
        int update1 = userMapper.update(update, query);
//        if(update1 == 1){
//            UserModifyPack pack = new UserModifyPack();
//            BeanUtils.copyProperties(req,pack);
//            messageProducer.sendToUser(req.getUserId(),req.getClientType(),req.getImei(),
//                    UserEventCommand.USER_MODIFY,pack,req.getAppId());
//
//            if(appConfig.isModifyUserAfterCallback()){
//                callbackService.callback(req.getAppId(),
//                        Constants.CallbackCommand.ModifyUserAfter,
//                        JSONObject.toJSONString(req));
//            }
//            return ResponseVO.success();
//        }
        throw new ApplicationException(UserErrorCode.MODIFY_USER_ERROR);
    }

    @Override
    public ResponseVO login(LoginReq req) {
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getUserSequence(GetUserSequenceReq req) {
//        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(req.getAppId() + ":" + Constants.RedisConstants.SeqPrefix + ":" + req.getUserId());
//        Long groupSeq = imGroupService.getUserGroupMaxSeq(req.getUserId(),req.getAppId());
//        map.put(Constants.SeqConstants.Group,groupSeq);
        return ResponseVO.successResponse();
    }
}
