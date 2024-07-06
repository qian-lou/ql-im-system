package com.qianlou.im.service.group.service;


import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.group.model.req.*;
import com.qianlou.im.service.group.model.resp.GetRoleInGroupResp;

import java.util.Collection;
import java.util.List;

/**
 * 群成员服务
 */
public interface GroupMemberService {

    /**
     * 导入群成员
     */
    ResponseVO importGroupMember(ImportGroupMemberReq req);

    /**
     * 添加群成员，拉人入群的逻辑，直接进入群聊。如果是后台管理员，则直接拉入群，
     * 否则只有私有群可以调用本接口，并且群成员也可以拉人入群.只有私有群可以调用本接口
     */
    ResponseVO addMember(AddGroupMemberReq req);

    ResponseVO removeMember(RemoveGroupMemberReq req);

    /**
     * 添加群成员
     */
    ResponseVO addGroupMember(String groupId, Integer appId, GroupMemberDto dto);

    /**
     * 删除群成员，内部调用
     */
    ResponseVO removeGroupMember(String groupId, Integer appId, String memberId);

    /**
     * 查询用户在群内的角色
     */
    ResponseVO<GetRoleInGroupResp> getRoleInGroupOne(String groupId, String memberId, Integer appId);

    ResponseVO<Collection<String>> getMemberJoinedGroup(GetJoinedGroupReq req);

    /**
     * 获取群成员
     * @param groupId
     * @param appId
     */
    ResponseVO<List<GroupMemberDto>> getGroupMember(String groupId, Integer appId);

    List<String> getGroupMemberId(String groupId, Integer appId);

    List<GroupMemberDto> getGroupManager(String groupId, Integer appId);

    ResponseVO updateGroupMember(UpdateGroupMemberReq req);

    /**
     * 切换群主
     * @param owner 新群主
     * @param groupId 群号
     * @param appId
     */
    ResponseVO transferGroupMember(String owner, String groupId, Integer appId);

    ResponseVO speak(SpeaMemberReq req);

    ResponseVO<Collection<String>> syncMemberJoinedGroup(String operator, Integer appId);
}
