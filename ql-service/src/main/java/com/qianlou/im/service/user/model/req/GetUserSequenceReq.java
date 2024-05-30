package com.qianlou.im.service.user.model.req;

import com.qianlou.im.common.model.RequestBase;
import lombok.Data;


@Data
public class GetUserSequenceReq extends RequestBase {

    private String userId;

}
