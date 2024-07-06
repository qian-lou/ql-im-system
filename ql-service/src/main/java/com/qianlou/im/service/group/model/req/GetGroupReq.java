package com.qianlou.im.service.group.model.req;

import com.qianlou.im.common.model.RequestBase;
import lombok.Data;


@Data
public class GetGroupReq extends RequestBase {

    private String groupId;

}
