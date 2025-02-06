package com.example.onlinestore.mapper;

import com.example.onlinestore.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    
    User findByUsername(@Param("username") String username);
    
    int updateUserToken(User user);

    int insertUser(User user);
}