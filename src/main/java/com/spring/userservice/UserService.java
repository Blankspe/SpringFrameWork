package com.spring.userservice;

import com.spring.annotation.JoinPoint;

public interface UserService {
    @JoinPoint
    void test();

    void test1();
}
