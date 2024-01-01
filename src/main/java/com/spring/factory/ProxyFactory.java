package com.spring.factory;

import com.spring.MySpringApplicationContext;
import com.spring.annotation.JoinPoint;
import com.spring.exception.BeanNotFoundException;
import com.spring.userservice.UserServiceImpl;
import com.spring.utilBean.AspectDefinition;
import com.spring.utilBean.BeanDefinition;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ProxyFactory {

    private Map<String , String> aspectMap = new HashMap<>();

    public ProxyFactory(Map<String, String> aspectMap) {
        this.aspectMap = aspectMap;
        System.out.println(aspectMap);
    }

    public Object AOPEnhanceByCGLIB(Class<?> aclass, Object obj) throws BeanNotFoundException, InstantiationException, IllegalAccessException {
        if (aspectMap.containsKey(aclass.getSimpleName())){
            Enhancer enhancer = new Enhancer();
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    Object instance = null;
                    if (aspectMap.containsValue(method.getName())) {
                        System.out.println("UserServiceImpl已经被CGLIB成功代理了");
                        System.out.println("前置通知");
                    }
                    System.out.println(method.getName());
                    instance = methodProxy.invokeSuper(o, objects);
                    if (aspectMap.containsValue(method.getName())){
                        System.out.println("后置通知");
                    }
                    return instance;
                }
            });
            enhancer.setSuperclass(aclass);
            Object newObj = enhancer.create();

            return newObj;
        }
        return obj;
    }

    private Object AOPEnhanceByJDK(Class<?> aClass, Object obj) {
        if (obj instanceof UserServiceImpl){
            System.out.println("注意这里为impl生成bean");
            Object finalObj = obj;
            Object newProxyInstance = Proxy.newProxyInstance(MySpringApplicationContext.class.getClassLoader(), aClass.getInterfaces(),
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            //切面
                            if (method.isAnnotationPresent(JoinPoint.class)) {
                                //前置通知
                                System.out.println("我是增强字段");//构造方法也被代理
                            }
                            return method.invoke(finalObj,args);
                        }
                    });
            obj = newProxyInstance;
            System.out.println("UserServiceImpl已经被代理成功了");
        }
        return obj;
    }
}
