package com.qianlou.im.service.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import com.qianlou.im.common.constant.ImConstant;
import com.qianlou.im.common.enums.GroupErrorCode;
import com.qianlou.im.common.enums.GroupMemberRoleEnum;
import com.qianlou.im.common.enums.GroupStatusEnum;
import com.qianlou.im.common.enums.GroupTypeEnum;
import com.qianlou.im.common.execption.ApplicationException;
import com.qianlou.im.common.vo.ResponseVO;
import com.qianlou.im.service.group.dao.entity.GroupEntity;
import com.qianlou.im.service.group.dao.mapper.GroupMapper;
import com.qianlou.im.service.group.model.req.*;
import com.qianlou.im.service.group.model.resp.GetGroupResp;
import com.qianlou.im.service.group.model.resp.GetJoinedGroupResp;
import com.qianlou.im.service.group.model.resp.GetRoleInGroupResp;
import com.qianlou.im.service.group.service.GroupMemberService;
import com.qianlou.im.service.group.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 群服务
 */
@SuppressWarnings("all")
@Slf4j
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupMemberService groupMemberService;


    @Override
    public ResponseVO importGroup(ImportGroupReq req) {
        QueryWrapper<GroupEntity> query = new QueryWrapper<>();
        if (StringUtils.isEmpty(req.getGroupId())) {
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            query.eq(ImConstant.GROUP_ID_NAME, req.getGroupId());
            query.eq(ImConstant.APP_ID_NAME, req.getAppId());
            if (groupMapper.selectCount(query) > 0) {
                throw new ApplicationException(GroupErrorCode.GROUP_IS_EXIST);
            }
        }
        GroupEntity entity = new GroupEntity();
        if (GroupTypeEnum.PUBLIC.getCode() == req.getGroupType() && StringUtils.isBlank(req.getOwnerId())) {
            throw new ApplicationException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }
        if (req.getCreateTime() == null) {
            entity.setCreateTime(System.currentTimeMillis());
        }
        entity.setStatus(GroupStatusEnum.NORMAL.getCode());
        BeanUtils.copyProperties(req, entity);
        int insert = groupMapper.insert(entity);
        if (insert != 1) {
            throw new ApplicationException(GroupErrorCode.IMPORT_GROUP_ERROR);
        }
        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO createGroup(CreateGroupReq req) {
        QueryWrapper<GroupEntity> query = new QueryWrapper<>();
        if (StringUtils.isEmpty(req.getGroupId())) {
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            query.eq(ImConstant.GROUP_ID_NAME, req.getGroupId());
            query.eq(ImConstant.APP_ID_NAME, req.getAppId());
            Integer integer = groupMapper.selectCount(query);
            if (integer > 0) {
                throw new ApplicationException(GroupErrorCode.GROUP_IS_EXIST);
            }
        }
        if (req.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && StringUtils.isBlank(req.getOwnerId())) {
            throw new ApplicationException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }
        GroupEntity imGroupEntity = new GroupEntity();
        imGroupEntity.setCreateTime(System.currentTimeMillis());
        imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());
        BeanUtils.copyProperties(req, imGroupEntity);
        groupMapper.insert(imGroupEntity);

        GroupMemberDto groupMemberDto = new GroupMemberDto();
        groupMemberDto.setMemberId(req.getOwnerId());
        groupMemberDto.setRole(GroupMemberRoleEnum.OWNER.getCode());
        groupMemberDto.setJoinTime(System.currentTimeMillis());
        groupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), groupMemberDto);
        //插入群成员
        for (GroupMemberDto dto : req.getMember()) {
            groupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), dto);
        }
        return ResponseVO.successResponse();
    }


    @Override
    @Transactional
    public ResponseVO updateGroupInfo(UpdateGroupReq req) {
        QueryWrapper<GroupEntity> query = new QueryWrapper<>();
        query.eq(ImConstant.GROUP_ID_NAME, req.getGroupId());
        query.eq(ImConstant.APP_ID_NAME, req.getAppId());
        GroupEntity entity = groupMapper.selectOne(query);
        //群不存在
        if (entity == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        //群已解散
        if (entity.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_DESTROY);
        }
        //检查权限
        ResponseVO<GetRoleInGroupResp> resp = groupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
        if (!resp.isOk()) {
            return resp;
        }
        GetRoleInGroupResp data = resp.getData();
        Integer roleInfo = data.getRole();
        boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode() || roleInfo == GroupMemberRoleEnum.OWNER.getCode();
        if (!isManager) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
        }
        GroupEntity update = new GroupEntity();
        BeanUtils.copyProperties(req, update);
        update.setUpdateTime(System.currentTimeMillis());
        int row = groupMapper.update(update, query);
        if (row != 1) {
            throw new ApplicationException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
        }
        return ResponseVO.successResponse();
    }


    @Override
    public ResponseVO getJoinedGroup(GetJoinedGroupReq req) {
        ResponseVO<Collection<String>> memberJoinedGroup = groupMemberService.getMemberJoinedGroup(req);
        if (!memberJoinedGroup.isOk()) {
            return memberJoinedGroup;
        }
        GetJoinedGroupResp resp = new GetJoinedGroupResp();
        if (CollectionUtils.isEmpty(memberJoinedGroup.getData())) {
            resp.setTotalCount(0);
            resp.setGroupList(new ArrayList<>());
            return ResponseVO.successResponse(resp);
        }
        QueryWrapper<GroupEntity> query = new QueryWrapper<>();
        query.eq(ImConstant.APP_ID_NAME, req.getAppId());
        query.in(ImConstant.GROUP_ID_NAME, memberJoinedGroup.getData());
        if (CollectionUtils.isNotEmpty(req.getGroupType())) {
            query.in(ImConstant.GROUP_TYPE_NAME, req.getGroupType());
        }
        List<GroupEntity> groupList = groupMapper.selectList(query);
        resp.setGroupList(groupList);
        resp.setTotalCount(req.getLimit() == null ? groupList.size() : groupMapper.selectCount(query));
        return ResponseVO.successResponse(resp);
    }

    @Override
    @Transactional
    public ResponseVO destroyGroup(DestroyGroupReq req) {
        boolean isAdmin = false;
        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.GROUP_ID_NAME, req.getGroupId());
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        GroupEntity entity = groupMapper.selectOne(queryWrapper);
        if (entity == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        if (entity.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_DESTROY);
        }
        if (!isAdmin) {
            if (entity.getGroupType() == GroupTypeEnum.PUBLIC.getCode()) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
            if (entity.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && !entity.getOwnerId().equals(req.getOperator())) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }
        GroupEntity update = new GroupEntity();
        update.setStatus(GroupStatusEnum.DESTROY.getCode());
        int update1 = groupMapper.update(update, queryWrapper);
        if (update1 != 1) {
            throw new ApplicationException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
        }
        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO transferGroup(TransferGroupReq req) {
        //旧群主
        ResponseVO<GetRoleInGroupResp> roleInGroupOne = groupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }
        if (roleInGroupOne.getData().getRole() != GroupMemberRoleEnum.OWNER.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }
        //新群主
        ResponseVO<GetRoleInGroupResp> newOwnerRole = groupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOwnerId(), req.getAppId());
        if (!newOwnerRole.isOk()) {
            return newOwnerRole;
        }
        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImConstant.GROUP_ID_NAME, req.getGroupId());
        queryWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        GroupEntity entity = groupMapper.selectOne(queryWrapper);
        if (entity.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_DESTROY);
        }
        GroupEntity updateGroup = new GroupEntity();
        updateGroup.setOwnerId(req.getOwnerId());
        UpdateWrapper<GroupEntity> updateGroupWrapper = new UpdateWrapper<>();
        updateGroupWrapper.eq(ImConstant.GROUP_ID_NAME, req.getGroupId());
        updateGroupWrapper.eq(ImConstant.APP_ID_NAME, req.getAppId());
        groupMapper.update(updateGroup, updateGroupWrapper);
        groupMemberService.transferGroupMember(req.getOwnerId(), req.getGroupId(), req.getAppId());
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getGroup(String groupId, Integer appId) {
        QueryWrapper<GroupEntity> query = new QueryWrapper<>();
        query.eq(ImConstant.APP_ID_NAME, appId);
        query.eq(ImConstant.GROUP_ID_NAME, groupId);
        GroupEntity entity = groupMapper.selectOne(query);
        return entity == null ?
                ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST)
                :
                ResponseVO.successResponse(entity);
    }

    @Override
    public ResponseVO getGroup(GetGroupReq req) {
        ResponseVO resp = this.getGroup(req.getGroupId(), req.getAppId());
        if (!resp.isOk()) {
            return resp;
        }
        GetGroupResp groupResp = new GetGroupResp();
        BeanUtils.copyProperties(resp.getData(), groupResp);
        try {
            ResponseVO<List<GroupMemberDto>> groupMember = groupMemberService.getGroupMember(req.getGroupId(), req.getAppId());
            if (groupMember.isOk()) {
                groupResp.setMemberList(groupMember.getData());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.successResponse(groupResp);
    }

    @Override
    public ResponseVO muteGroup(MuteGroupReq req) {

        ResponseVO<GroupEntity> groupResp = getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        if (groupResp.getData().getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_DESTROY);
        }

        boolean isadmin = false;

        if (!isadmin) {
            //不是后台调用需要检查权限
            ResponseVO<GetRoleInGroupResp> role = groupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());

            if (!role.isOk()) {
                return role;
            }

            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();

            boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode() || roleInfo == GroupMemberRoleEnum.OWNER.getCode();

            //公开群只能群主修改资料
            if (!isManager) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }

        GroupEntity update = new GroupEntity();
        update.setSilence(req.getSilence());

        UpdateWrapper<GroupEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("group_id", req.getGroupId());
        wrapper.eq("app_id", req.getAppId());
        groupMapper.update(update, wrapper);

        return ResponseVO.successResponse();
    }


}
