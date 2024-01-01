package com.spring.aspect;

import com.spring.annotation.Around;
import com.spring.annotation.Aspect;
import com.spring.annotation.PointCut;

@Aspect
public class AspectTest {

    @PointCut("com.spring.userservice.UserServiceImpl.test")
    public void pt(){

    }

    @Around
    public void around(ProcessedJointPoint jointPoint){

        jointPoint.processd();

    }

}
