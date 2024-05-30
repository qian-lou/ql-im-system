package com.qianlou.im.service.user.service;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.user.dao.UserEntity;
import com.qianlou.im.service.user.model.req.*;
import com.qianlou.im.service.user.model.resp.GetUserInfoResp;

public interface UserService {

    ResponseVO importUser(ImportUserReq req);

    ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req);

    ResponseVO<UserEntity> getSingleUserInfo(String userId, Integer appId);

    ResponseVO deleteUser(DeleteUserReq req);

    ResponseVO modifyUserInfo(ModifyUserInfoReq req);

    ResponseVO login(LoginReq req);

    ResponseVO getUserSequence(GetUserSequenceReq req);
}
