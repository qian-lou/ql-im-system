package com.qianlou.im.service.group.model.resp;

import com.qianlou.im.service.group.dao.entity.GroupEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetJoinedGroupResp {

    private Integer totalCount;

    private List<GroupEntity> groupList;

}
