package com.spring;

import com.spring.config.SpringConfig;
import com.spring.usermapper.UserMapper;
import com.spring.userservice.UserServiceImpl;

public class Main {
    public static void main(String[] args) throws Exception {
        MySpringApplicationContext applicationContext = new MySpringApplicationContext(SpringConfig.class);
        UserMapper userMapper = (UserMapper) applicationContext.getBean("userMapper");
        UserServiceImpl userService = (UserServiceImpl) applicationContext.getBean("userServiceImpl");

        System.out.println("========================");
        System.out.println(userService.getUserMapper());
        System.out.println(userMapper.getUserServiceImpl());
        System.out.println("========================");
        userService.test();
    }
}
