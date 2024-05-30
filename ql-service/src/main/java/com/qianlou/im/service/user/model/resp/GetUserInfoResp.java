package com.qianlou.im.service.user.model.resp;

import com.qianlou.im.service.user.dao.UserEntity;
import lombok.Data;

import java.util.List;


@Data
public class GetUserInfoResp {

    private List<UserEntity> userDataItem;

    private List<String> failUser;


}
