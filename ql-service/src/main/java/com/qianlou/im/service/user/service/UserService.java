package com.qianlou.im.service.user.service;

import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.user.dao.UserEntity;
import com.qianlou.im.service.user.model.req.*;
import com.qianlou.im.service.user.model.resp.GetUserInfoResp;

import java.util.List;

public interface UserService {

    /**
     * 导入用户数据
     */
    ResponseVO importUser(ImportUserReq req);

    /**
     * 获取用户信息
     */
    ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req);

    /**
     * 获取单个用户信息
     * @param userId 用户id
     * @param appId  应用id
     */
    ResponseVO<UserEntity> getSingleUserInfo(String userId, Integer appId);

    /**
     * 删除用户
     * @param req
     */
    ResponseVO deleteUser(DeleteUserReq req);


    /**
     * 修改用户信息
     * @param req
     */
    ResponseVO modifyUserInfo(ModifyUserInfoReq req);

    ResponseVO login(LoginReq req);

    ResponseVO getUserSequence(GetUserSequenceReq req);
}
