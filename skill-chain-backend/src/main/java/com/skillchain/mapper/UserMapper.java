package com.skillchain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skillchain.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}