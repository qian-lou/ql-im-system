package com.qianlou.im.service.group.service;


import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.group.dao.entity.GroupEntity;
import com.qianlou.im.service.group.model.req.*;

public interface GroupService {

    /**
     * 导入群组
     */
    ResponseVO importGroup(ImportGroupReq req);

    /**
     * 创建组
     */
    ResponseVO createGroup(CreateGroupReq req);

    /**
     * 修改群基础信息，
     * 如果是后台管理员调用，则不检查权限，
     * 如果不是则检查权限，
     * 如果是私有群（微信群）任何人都可以修改资料，公开群只有管理员可以修改
     * 如果是群主或者管理员可以修改其他信息。
     */
    ResponseVO updateGroupInfo(UpdateGroupReq req);

    /**
     * 获取用户加入的群组
     */
    ResponseVO getJoinedGroup(GetJoinedGroupReq req);

    /**
     * 解散群组，只支持后台管理员和群主解散
     */
    ResponseVO destroyGroup(DestroyGroupReq req);

    ResponseVO transferGroup(TransferGroupReq req);

    /**
     * 获取群的信息
     * @param groupId
     * @param appId
     */
    ResponseVO<GroupEntity> getGroup(String groupId, Integer appId);

    /**
     * 获取群信息
     * @param req
     */
    ResponseVO getGroup(GetGroupReq req);

    ResponseVO muteGroup(MuteGroupReq req);
}
