package com.spring;

import com.spring.exception.BeanNotFoundException;
import com.spring.factory.BeanFactory;

public class MySpringApplicationContext {

    private BeanFactory beanFactory = new BeanFactory();

    public MySpringApplicationContext(Class clazz) throws Exception {
        beanFactory.doCreateBean(clazz);
    }

    public Object getBean(String key) throws InstantiationException, IllegalAccessException, BeanNotFoundException {
        return beanFactory.getBean(key);
    }

}