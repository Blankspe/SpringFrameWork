package com.spring.usermapper;

import com.spring.annotation.AutoWired;
import com.spring.annotation.Component;
import com.spring.annotation.Scope;
import com.spring.userservice.UserServiceImpl;

@Component
@Scope
public class UserMapper {
    @AutoWired
    private UserServiceImpl userServiceImpl;

    public UserMapper(){
        System.out.println("无参构造器，UserMapper已被建立");
    }

    public UserServiceImpl getUserServiceImpl() {
        return userServiceImpl;
    }

    public void setUserServiceImpl(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    public UserMapper(String name){
        System.out.println("有参构造器");
    }
}