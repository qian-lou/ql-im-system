package com.qianlou.im.service.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.qianlou.im.common.constant.ImConstant;
import com.qianlou.im.common.enums.GroupErrorCode;
import com.qianlou.im.common.enums.GroupMemberRoleEnum;
import com.qianlou.im.common.enums.GroupStatusEnum;
import com.qianlou.im.common.enums.GroupTypeEnum;
import com.qianlou.im.common.execption.ApplicationException;
import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.group.dao.entity.GroupEntity;
import com.qianlou.im.service.group.dao.entity.GroupMemberEntity;
import com.qianlou.im.service.group.dao.mapper.GroupMemberMapper;
import com.qianlou.im.service.group.model.req.*;
import com.qianlou.im.service.group.model.resp.AddMemberResp;
import com.qianlou.im.service.group.model.resp.GetRoleInGroupResp;
import com.qianlou.im.service.group.service.GroupMemberService;
import com.qianlou.im.service.group.service.GroupService;
import com.qianlou.im.service.user.dao.entity.UserEntity;
import com.qianlou.im.service.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 群成员服务
 */
@SuppressWarnings("all")
@Service
@Slf4j
public class GroupMemberServiceImpl implements GroupMemberService {

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private UserService userService;


    @Override
    public ResponseVO importGroupMember(ImportGroupMemberReq req) {
        List<AddMemberResp> respList = new ArrayList<>();
        ResponseVO<GroupEntity> groupResp = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }
        for (GroupMemberDto memberId : req.getMembers()) {
            ResponseVO resp = null;
            try {
                resp = groupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), memberId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                resp = ResponseVO.errorResponse();
            }
            AddMemberResp memberResp = new AddMemberResp();
            memberResp.setMemberId(memberId.getMemberId());
            memberResp.setResult(resp.isOk() ? 0 : ((resp.getCode() == GroupErrorCode.USER_IS_JOINED_GROUP.getCode() ? 2 : 1)));
            respList.add(memberResp);
        }
        return ResponseVO.successResponse(respList);
    }

    @Override
    @Transactional
    public ResponseVO addGroupMember(String groupId, Integer appId, GroupMemberDto dto) {
        //检查用户状态
        ResponseVO<UserEntity> singleUserInfo = userService.getSingleUserInfo(dto.getMemberId(), appId);
        if(!singleUserInfo.isOk()){
            return singleUserInfo;
        }
        //当前成员想成为群主
        if (dto.getRole() != null && GroupMemberRoleEnum.OWNER.getCode() == dto.getRole()) {
            QueryWrapper<GroupMemberEntity> queryOwner = new QueryWrapper<>();
            queryOwner.eq(ImConstant.GROUP_ID_NAME, groupId);
            queryOwner.eq(ImConstant.APP_ID_NAME, appId);
            queryOwner.eq(ImConstant.ROLE_NAME, GroupMemberRoleEnum.OWNER.getCode());
            if (groupMemberMapper.selectCount(queryOwner) > 0) {
                return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_HAVE_OWNER);
            }
        }
        QueryWrapper<GroupMemberEntity> query = new QueryWrapper<>();
        query.eq(ImConstant.GROUP_ID_NAME, groupId);
        query.eq(ImConstant.APP_ID_NAME, appId);
        query.eq(ImConstant.MEMBER_ID_NAME, dto.getMemberId());
        GroupMemberEntity memberEntity = groupMemberMapper.selectOne(query);
        long now = System.currentTimeMillis();
        //初次加群
        if (memberEntity == null) {
            memberEntity = new GroupMemberEntity();
            BeanUtils.copyProperties(dto, memberEntity);
            memberEntity.setGroupId(groupId);
            memberEntity.setAppId(appId);
            memberEntity.setJoinTime(now);
            int insert = groupMemberMapper.insert(memberEntity);
            return insert != 1 ? ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR): ResponseVO.successResponse();
        }
        //重新进群
        if (GroupMemberRoleEnum.LEAVE.getCode() == memberEntity.getRole()) {
            memberEntity = new GroupMemberEntity();
            BeanUtils.copyProperties(dto, memberEntity);
            memberEntity.setJoinTime(now);
            int update = groupMemberMapper.update(memberEntity, query);
            return update != 1 ? ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR) : ResponseVO.successResponse();
        }
        //已经在群
        return ResponseVO.errorResponse(GroupErrorCode.USER_IS_JOINED_GROUP);
    }

    @Override
    public ResponseVO removeGroupMember(String groupId, Integer appId, String memberId) {

        ResponseVO<UserEntity> singleUserInfo = userService.getSingleUserInfo(memberId, appId);
        if(!singleUserInfo.isOk()){
            return singleUserInfo;
        }

        ResponseVO<GetRoleInGroupResp> roleInGroupOne = getRoleInGroupOne(groupId, memberId, appId);
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }

        GetRoleInGroupResp data = roleInGroupOne.getData();
        GroupMemberEntity imGroupMemberEntity = new GroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.LEAVE.getCode());
        imGroupMemberEntity.setLeaveTime(System.currentTimeMillis());
        imGroupMemberEntity.setGroupMemberId(data.getGroupMemberId());
        groupMemberMapper.updateById(imGroupMemberEntity);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<GetRoleInGroupResp> getRoleInGroupOne(String groupId, String memberId, Integer appId) {
        GetRoleInGroupResp resp = new GetRoleInGroupResp();
        QueryWrapper<GroupMemberEntity> queryOwner = new QueryWrapper<>();
        queryOwner.eq(ImConstant.GROUP_ID_NAME, groupId);
        queryOwner.eq(ImConstant.APP_ID_NAME, appId);
        queryOwner.eq(ImConstant.MEMBER_ID_NAME, memberId);
        GroupMemberEntity member = groupMemberMapper.selectOne(queryOwner);
        //成员不存在或已离开
        if (member == null || member.getRole() == GroupMemberRoleEnum.LEAVE.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }
        resp.setSpeakDate(member.getSpeakDate());
        resp.setGroupMemberId(member.getGroupMemberId());
        resp.setMemberId(member.getMemberId());
        resp.setRole(member.getRole());
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<Collection<String>> getMemberJoinedGroup(GetJoinedGroupReq req) {
        if (req.getLimit() != null) {
            Page<GroupMemberEntity> objectPage = new Page<>(req.getOffset(), req.getLimit());
            QueryWrapper<GroupMemberEntity> query = new QueryWrapper<>();
            query.eq(ImConstant.APP_ID_NAME, req.getAppId());
            query.eq(ImConstant.MEMBER_ID_NAME, req.getMemberId());
            IPage<GroupMemberEntity> imGroupMemberEntityPage = groupMemberMapper.selectPage(objectPage, query);
            Set<String> groupId = new HashSet<>();
            List<GroupMemberEntity> records = imGroupMemberEntityPage.getRecords();
            records.forEach(e -> {
                groupId.add(e.getGroupId());
            });
            return ResponseVO.successResponse(groupId);
        } else {
            return ResponseVO.successResponse(groupMemberMapper.getJoinedGroupId(req.getAppId(), req.getMemberId()));
        }
    }

    @Override
    public ResponseVO addMember(AddGroupMemberReq req) {
        List<AddMemberResp> resp = new ArrayList<>();
        boolean isAdmin = false;
        ResponseVO<GroupEntity> groupResp = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }
        List<GroupMemberDto> memberDtos = req.getMembers();
        GroupEntity group = groupResp.getData();
        /**
         * 私有群（private）	类似普通微信群，创建后仅支持已在群内的好友邀请加群，且无需被邀请方同意或群主审批
         * 公开群（Public）	类似 QQ 群，创建后群主可以指定群管理员，需要群主或管理员审批通过才能入群
         * 群类型 1私有群（类似微信） 2公开群(类似qq）
         *
         */

        if (!isAdmin && GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_APPMANAGER_ROLE);
        }

        List<String> successId = new ArrayList<>();
        for (GroupMemberDto memberId :
                memberDtos) {
            ResponseVO responseVO = null;
            try {
                responseVO = groupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), memberId);
            } catch (Exception e) {
                e.printStackTrace();
                responseVO = ResponseVO.errorResponse();
            }
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setMemberId(memberId.getMemberId());
            if (responseVO.isOk()) {
                successId.add(memberId.getMemberId());
                addMemberResp.setResult(0);
            } else if (responseVO.getCode() == GroupErrorCode.USER_IS_JOINED_GROUP.getCode()) {
                addMemberResp.setResult(2);
                addMemberResp.setResultMessage(responseVO.getMsg());
            } else {
                addMemberResp.setResult(1);
                addMemberResp.setResultMessage(responseVO.getMsg());
            }
            resp.add(addMemberResp);
        }

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO removeMember(RemoveGroupMemberReq req) {

        List<AddMemberResp> resp = new ArrayList<>();
        boolean isAdmin = false;
        ResponseVO<GroupEntity> groupResp = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        GroupEntity group = groupResp.getData();

        if (!isAdmin) {
            if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {

                //获取操作人的权限 是管理员or群主or群成员
                ResponseVO<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
                if (!role.isOk()) {
                    return role;
                }

                GetRoleInGroupResp data = role.getData();
                Integer roleInfo = data.getRole();

                boolean isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
                boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode();

                if (!isOwner && !isManager) {
                    throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
                }

                //私有群必须是群主才能踢人
                if (!isOwner && GroupTypeEnum.PRIVATE.getCode() == group.getGroupType()) {
                    throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }

                //公开群管理员和群主可踢人，但管理员只能踢普通群成员
                if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
//                    throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
                    //获取被踢人的权限
                    ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                    if (!roleInGroupOne.isOk()) {
                        return roleInGroupOne;
                    }
                    GetRoleInGroupResp memberRole = roleInGroupOne.getData();
                    if (memberRole.getRole() == GroupMemberRoleEnum.OWNER.getCode()) {
                        throw new ApplicationException(GroupErrorCode.GROUP_OWNER_IS_NOT_REMOVE);
                    }
                    //是管理员并且被踢人不是群成员，无法操作
                    if (isManager && memberRole.getRole() != GroupMemberRoleEnum.ORDINARY.getCode()) {
                        throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                    }
                }
            }
        }
        ResponseVO responseVO = groupMemberService.removeGroupMember(req.getGroupId(), req.getAppId(), req.getMemberId());
        return responseVO;
    }

    @Override
    public ResponseVO<List<GroupMemberDto>> getGroupMember(String groupId, Integer appId) {
        return ResponseVO.successResponse(groupMemberMapper.getGroupMember(appId, groupId));
    }

    @Override
    public List<String> getGroupMemberId(String groupId, Integer appId) {
        return groupMemberMapper.getGroupMemberId(appId, groupId);
    }

    @Override
    public List<GroupMemberDto> getGroupManager(String groupId, Integer appId) {
        return groupMemberMapper.getGroupManager(groupId, appId);
    }

    @Override
    public ResponseVO updateGroupMember(UpdateGroupMemberReq req) {

        boolean isadmin = false;

        ResponseVO<GroupEntity> group = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!group.isOk()) {
            return group;
        }

        GroupEntity groupData = group.getData();
        if (groupData.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_DESTROY);
        }

        //是否是自己修改自己的资料
        boolean isMeOperate = req.getOperator().equals(req.getMemberId());

        if (!isadmin) {
            //昵称只能自己修改 权限只能群主或管理员修改
            if (StringUtils.isBlank(req.getAlias()) && !isMeOperate) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_ONESELF);
            }
            //私有群不能设置管理员
            if (groupData.getGroupType() == GroupTypeEnum.PRIVATE.getCode() &&
                    req.getRole() != null && (req.getRole() == GroupMemberRoleEnum.MANAGER.getCode() ||
                    req.getRole() == GroupMemberRoleEnum.OWNER.getCode())) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            //如果要修改权限相关的则走下面的逻辑
            if(req.getRole() != null){
                //获取被操作人的是否在群内
                ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                if(!roleInGroupOne.isOk()){
                    return roleInGroupOne;
                }

                //获取操作人权限
                ResponseVO<GetRoleInGroupResp> operateRoleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
                if(!operateRoleInGroupOne.isOk()){
                    return operateRoleInGroupOne;
                }

                GetRoleInGroupResp data = operateRoleInGroupOne.getData();
                Integer roleInfo = data.getRole();
                boolean isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
                boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode();

                //不是管理员不能修改权限
                if(req.getRole() != null && !isOwner && !isManager){
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
                }

                //管理员只有群主能够设置
                if(req.getRole() != null && req.getRole() == GroupMemberRoleEnum.MANAGER.getCode() && !isOwner){
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }

            }
        }

        GroupMemberEntity update = new GroupMemberEntity();

        if (StringUtils.isNotBlank(req.getAlias())) {
            update.setAlias(req.getAlias());
        }

        //不能直接修改为群主
        if(req.getRole() != null && req.getRole() != GroupMemberRoleEnum.OWNER.getCode()){
            update.setRole(req.getRole());
        }

        UpdateWrapper<GroupMemberEntity> objectUpdateWrapper = new UpdateWrapper<>();
        objectUpdateWrapper.eq("app_id", req.getAppId());
        objectUpdateWrapper.eq("member_id", req.getMemberId());
        objectUpdateWrapper.eq("group_id", req.getGroupId());
        groupMemberMapper.update(update, objectUpdateWrapper);
        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO transferGroupMember(String owner, String groupId, Integer appId) {
        //更新旧群主
        GroupMemberEntity oldOwner = new GroupMemberEntity();
        oldOwner.setRole(GroupMemberRoleEnum.ORDINARY.getCode());
        UpdateWrapper<GroupMemberEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(ImConstant.APP_ID_NAME, appId);
        updateWrapper.eq(ImConstant.GROUP_ID_NAME, groupId);
        updateWrapper.eq(ImConstant.ROLE_NAME, GroupMemberRoleEnum.OWNER.getCode());
        groupMemberMapper.update(oldOwner, updateWrapper);

        //更新新群主
        GroupMemberEntity newOwner = new GroupMemberEntity();
        newOwner.setRole(GroupMemberRoleEnum.OWNER.getCode());
        UpdateWrapper<GroupMemberEntity> ownerWrapper = new UpdateWrapper<>();
        ownerWrapper.eq(ImConstant.APP_ID_NAME, appId);
        ownerWrapper.eq(ImConstant.GROUP_ID_NAME, groupId);
        ownerWrapper.eq(ImConstant.MEMBER_ID_NAME, owner);
        groupMemberMapper.update(newOwner, ownerWrapper);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO speak(SpeaMemberReq req) {

        ResponseVO<GroupEntity> groupResp = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        boolean isadmin = false;
        boolean isOwner = false;
        boolean isManager = false;
        GetRoleInGroupResp memberRole = null;

        if (!isadmin) {

            //获取操作人的权限 是管理员or群主or群成员
            ResponseVO<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
            if (!role.isOk()) {
                return role;
            }

            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();

            isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
            isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode();

            if (!isOwner && !isManager) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            //获取被操作的权限
            ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
            if (!roleInGroupOne.isOk()) {
                return roleInGroupOne;
            }
            memberRole = roleInGroupOne.getData();
            //被操作人是群主只能app管理员操作
            if (memberRole.getRole() == GroupMemberRoleEnum.OWNER.getCode()) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_APPMANAGER_ROLE);
            }

            //是管理员并且被操作人不是群成员，无法操作
            if (isManager && memberRole.getRole() != GroupMemberRoleEnum.ORDINARY.getCode()) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }

        GroupMemberEntity imGroupMemberEntity = new GroupMemberEntity();
        if(memberRole == null){
            //获取被操作的权限
            ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
            if (!roleInGroupOne.isOk()) {
                return roleInGroupOne;
            }
            memberRole = roleInGroupOne.getData();
        }

        imGroupMemberEntity.setGroupMemberId(memberRole.getGroupMemberId());
        if(req.getSpeakDate() > 0){
            imGroupMemberEntity.setSpeakDate(System.currentTimeMillis() + req.getSpeakDate());
        }else{
            imGroupMemberEntity.setSpeakDate(req.getSpeakDate());
        }

        int i = groupMemberMapper.updateById(imGroupMemberEntity);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<Collection<String>> syncMemberJoinedGroup(String operater, Integer appId) {
        return ResponseVO.successResponse(groupMemberMapper.syncJoinedGroupId(appId,operater,GroupMemberRoleEnum.LEAVE.getCode()));
    }


}
