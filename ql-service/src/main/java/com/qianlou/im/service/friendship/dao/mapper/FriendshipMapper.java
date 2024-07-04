package com.qianlou.im.service.friendship.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qianlou.im.service.friendship.dao.FriendShipEntity;
import com.qianlou.im.service.friendship.model.req.CheckFriendShipReq;
import com.qianlou.im.service.friendship.model.resp.CheckFriendShipResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FriendshipMapper extends BaseMapper<FriendShipEntity> {


    @Select("<script>" +
            "select from_id as fromId, to_id as toId, if(status = 1, 1, 0) as status from t_friendship where from_id = #{fromId} and to_id in" +
            "<foreach collection='toIds' index = 'index' item='id' separator=',' open='(' close=')'>#{id}</foreach>" +
            "</script>")
    CheckFriendShipResp checkFriendShip(CheckFriendShipReq req);
}
