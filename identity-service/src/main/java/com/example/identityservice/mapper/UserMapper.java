package com.example.identityservice.mapper;

import com.example.identityservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }
}