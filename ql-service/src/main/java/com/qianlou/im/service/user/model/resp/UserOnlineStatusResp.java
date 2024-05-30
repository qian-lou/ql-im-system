package com.qianlou.im.service.user.model.resp;

import lombok.Data;

import java.util.List;


@Data
public class UserOnlineStatusResp {

    //private List<UserSession> session;

    private String customText;

    private Integer customStatus;

}
