package com.qianlou.im.service.friendship.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qianlou.im.service.friendship.dao.entity.FriendShipEntity;
import com.qianlou.im.service.friendship.model.req.CheckFriendShipReq;
import com.qianlou.im.service.friendship.model.resp.CheckFriendShipResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FriendShipMapper extends BaseMapper<FriendShipEntity> {

    /**
     * 单向检查好友关系
     *
     * @param req
     */
    @Select("<script>" +
            "select from_id as fromId, to_id as toId, if(status = 1, 1, 0) as status from t_friendship where from_id = #{fromId} and to_id in" +
            "<foreach collection='toIds' index = 'index' item='id' separator=',' open='(' close=')'>#{id}</foreach>" +
            "</script>")
    List<CheckFriendShipResp> checkFriendShip(CheckFriendShipReq req);

    /**
     * 双向检查好友关系
     *
     * @param req
     */
    @Select("<script>" +
            "select a.from_id as fromId, a.to_id as toId,\n" +
            "(case when a.status = 1 and b.status = 1 then 1\n" +
            "    when a.status != 1 and b.status = 1 then 2\n" +
            "    when a.status = 1 and b.status != 1 then 3\n" +
            "    when a.status != 1 and b.status != 1 then 4\n" +
            "end) as status\n" +
            "from (select from_id, to_id,if (status = 1, 1, 0) as status from t_friendship\n" +
            "where from_id = #{fromId} and to_id in" +
            "<foreach collection='toIds' index = 'index' item='id' separator=',' open='(' close=')'>#{id}</foreach>" +
            ") a inner join (\n" +
            "    select from_id, to_id, if (status = 1, 1, 0) as status from t_friendship\n" +
            "where to_id = #{fromId} and from_id in" +
            "<foreach collection='toIds' index = 'index' item='id' separator=',' open='(' close=')'>#{id}</foreach>" +
            ") b on a.from_id = b.to_id and a.to_id = b.from_id" +
            "</script>"
    )
    List<CheckFriendShipResp> checkFriendShipBoth(CheckFriendShipReq req);


    /**
     * 单向检查黑名单关系
     *
     * @param req
     */
    @Select("<script>" +
            "select from_id as fromId, to_id as toId, if(black = 1, 1, 0) as status from t_friendship where from_id = #{fromId} and to_id in" +
            "<foreach collection='toIds' index = 'index' item='id' separator=',' open='(' close=')'>#{id}</foreach>" +
            "</script>")
    List<CheckFriendShipResp> checkBlack(CheckFriendShipReq req);

    /**
     * 双向检查黑名单关系
     *
     * @param req
     */
    @Select("<script>" +
            "select a.from_id as fromId, a.to_id as toId,\n" +
            "(case when a.status = 1 and b.status = 1 then 1\n" +
            "    when a.status != 1 and b.status = 1 then 2\n" +
            "    when a.status = 1 and b.status != 1 then 3\n" +
            "    when a.status != 1 and b.status != 1 then 4\n" +
            "end) as status\n" +
            "from (select from_id, to_id,if (black = 1, 1, 0) as status from t_friendship\n" +
            "where from_id = #{fromId} and to_id in" +
            "<foreach collection='toIds' index = 'index' item='id' separator=',' open='(' close=')'>#{id}</foreach>" +
            ") a inner join (\n" +
            "    select from_id, to_id, if (black = 1, 1, 0) as status from t_friendship\n" +
            "where to_id = #{fromId} and from_id in" +
            "<foreach collection='toIds' index = 'index' item='id' separator=',' open='(' close=')'>#{id}</foreach>" +
            ") b on a.from_id = b.to_id and a.to_id = b.from_id" +
            "</script>"
    )
    List<CheckFriendShipResp> checkBlackBoth(CheckFriendShipReq req);
}
