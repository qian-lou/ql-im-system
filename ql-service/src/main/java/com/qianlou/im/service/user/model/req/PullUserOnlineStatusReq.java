package com.qianlou.im.service.user.model.req;

import com.qianlou.im.common.model.RequestBase;
import lombok.Data;

import java.util.List;


@Data
public class PullUserOnlineStatusReq extends RequestBase {

    private List<String> userList;

}
