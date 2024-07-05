package com.qianlou.im.service.friendship.model.req;

import com.qianlou.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class FriendShipRequestReadReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String fromId;
}
