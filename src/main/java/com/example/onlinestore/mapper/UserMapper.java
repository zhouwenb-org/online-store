package com.example.onlinestore.mapper;

import com.example.onlinestore.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Update("UPDATE users SET token = #{token}, token_expire_time = #{tokenExpireTime} WHERE username = #{username}")
    void updateUserToken(User user);
} 