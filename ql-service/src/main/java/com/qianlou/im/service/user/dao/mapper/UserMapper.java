package com.qianlou.im.service.user.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qianlou.im.service.user.dao.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}