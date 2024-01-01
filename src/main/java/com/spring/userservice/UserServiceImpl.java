package com.spring.userservice;

import com.spring.annotation.AutoWired;
import com.spring.annotation.Component;
import com.spring.annotation.JoinPoint;
import com.spring.annotation.Scope;
import com.spring.usermapper.UserMapper;

@Component
@Scope
public class UserServiceImpl implements UserService{
    @AutoWired
    private UserMapper userMapper;

    @JoinPoint
    public UserMapper getUserMapper() {
        return userMapper;
    }

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserServiceImpl(){
        System.out.println("UserServiceImpl已被建立");
    }

    @Override
    public void test() {
        System.out.println("这是一个被增强的的方法");
    }


    public void test1(){
        System.out.println("这是自带的方法");
    }
}
