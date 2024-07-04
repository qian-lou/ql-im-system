package com.qianlou.im.service.user.model.req;

import com.qianlou.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;


@EqualsAndHashCode(callSuper = true)
@Data
public class ModifyUserInfoReq extends RequestBase {

    // 用户id
    @NotEmpty(message = "用户id不能为空")
    private String userId;

    // 用户名称
    private String nickName;

    //位置
    private String location;

    //生日
    private String birthDay;

    private String password;

    // 头像
    private String img;

    // 性别
    private String gender;

    // 个性签名
    private String signature;

    // 加好友验证类型（Friend_AllowType） 1需要验证
    private Integer friendAllowType;

    private String extra;


}
