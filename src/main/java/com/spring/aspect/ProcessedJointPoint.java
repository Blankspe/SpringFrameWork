package com.spring.aspect;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProcessedJointPoint {

    Object obj;
    Object[] args;
    private MethodProxy method;

    public ProcessedJointPoint(Object obj,Object[] args, MethodProxy method) {
        this.obj = obj;
        this.args = args;
        this.method = method;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public MethodProxy getMethod() {
        return method;
    }

    public void setMethod(MethodProxy method) {
        this.method = method;
    }

    public Object processd(MethodProxy method,Object obj,Object[] args) throws Throwable {
        this.method = method;
        return this.method.invokeSuper(obj,args);
    }
}
