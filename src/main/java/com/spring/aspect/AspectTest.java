package com.spring.aspect;

import com.spring.annotation.Around;
import com.spring.annotation.Aspect;
import com.spring.annotation.PointCut;

import java.lang.reflect.InvocationTargetException;

@Aspect
public class AspectTest {

    @PointCut("com.spring.userservice.UserServiceImpl.test")
    public void pt(){
    }

    @Around
    public void around(ProcessedJointPoint jointPoint) throws Throwable {

        System.out.println("前置通知");
        jointPoint.processd(jointPoint.getMethod(),jointPoint.getObj(),jointPoint.getArgs());
        System.out.println("后置通知");

    }

}
