package com.example.onlinestore.mapper;

import com.example.onlinestore.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    
    User findByUsername(String username);
    
    int updateUserToken(User user);

    void insertUser(User user);
    
    List<User> findAllWithPagination(@Param("offset") int offset, @Param("limit") int limit);
    
    long countTotal();

    List<User> findAll();
}