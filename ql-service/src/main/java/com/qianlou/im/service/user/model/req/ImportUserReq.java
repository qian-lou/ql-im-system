package com.qianlou.im.service.user.model.req;

import com.qianlou.im.common.model.RequestBase;
import com.qianlou.im.service.user.dao.entity.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class ImportUserReq extends RequestBase {

    private List<UserEntity> userList;
}
