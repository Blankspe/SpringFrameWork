package com.spring.factory;

import com.spring.MySpringApplicationContext;
import com.spring.annotation.*;
import com.spring.exception.BeanNotFoundException;
import com.spring.userservice.UserServiceImpl;
import com.spring.utilBean.AspectDefinition;
import com.spring.utilBean.BeanDefinition;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BeanFactory {
    public static Map<String,Object> beanMap = new HashMap<>();
    public static Map<String,BeanDefinition> beanDefinitionMap = new HashMap<>();
    public static Map<String,String> aspectMap = new HashMap<>();

    public void doCreateBean(Class clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException, BeanNotFoundException {
        //1.获取字节码文件路径
        ClassLoader classLoader = MySpringApplicationContext.class.getClassLoader();
        ComponentScan componentScan = (ComponentScan) clazz.getAnnotation(ComponentScan.class);
        String path = componentScan.value().replace('.','/');
        System.out.println(path);
        URL resource = classLoader.getResource(path);

        //2.打开文件进行bean创建
        File f = new File(resource.getFile());
        fileLoadAndBeanDefinition(classLoader,f);
        initBean(classLoader);
    }

    private void fileLoadAndBeanDefinition(ClassLoader classLoader, File f) throws ClassNotFoundException, InstantiationException, IllegalAccessException, BeanNotFoundException {
        if (f.isDirectory()){
            File[] files = f.listFiles();
            for (File file:files){
                doBeanDefiniton(classLoader,file);
            }
            for (File file:files) {
                if(file.isDirectory()){
                    fileLoadAndBeanDefinition(classLoader,file);
                }
                doBeanDefiniton(classLoader,file);
            }
        }
    }

    private void doBeanDefiniton(ClassLoader classLoader, File file) throws ClassNotFoundException {
        if (file.getName().endsWith(".class")) {
            //获取文件名
            String fileName = file.getName().substring(0, file.getName().indexOf(".class"));
            System.out.println(fileName);
            System.out.println(fileName);
            String beanName = fileName.substring(0, 1).toLowerCase() + fileName.substring(1);

            String className = file.getPath().substring(file.getPath().indexOf("com"), file.getPath().indexOf(".class"));
            className = className.replace('\\', '.');
//            System.out.println(className);
            Class<?> aClass = classLoader.loadClass(className);
            if (aClass.isAnnotationPresent(Aspect.class)) {
                AspectDefinition aspectDefinition = new AspectDefinition();
                Method[] declaredMethods = aClass.getDeclaredMethods();
                for (Method method : declaredMethods) {
                    if (method.isAnnotationPresent(PointCut.class)) {
                        String methodToEnhance = method.getAnnotation(PointCut.class).value();
                        String methodName = methodToEnhance.substring(methodToEnhance.lastIndexOf('.')+1);
                        String clazzAllName = methodToEnhance.substring(
                                0,methodToEnhance.lastIndexOf('.')
                        );
                        String clazzName = clazzAllName.substring(clazzAllName.lastIndexOf('.')+1);
                        aspectDefinition.setMethod(methodName);
                        aspectMap.put(clazzName,methodName);
                    }
                }


            }
            if (aClass.isAnnotationPresent(Component.class) ) {
                //定义bean
                BeanDefinition beanDefinition = new BeanDefinition();
                String scope = aClass.getDeclaredAnnotation(Scope.class).value();
                beanDefinition.setScope(scope);
                beanDefinition.setBeanName(beanName);
                beanDefinition.setaClass(aClass);

                beanDefinitionMap.put(beanName, beanDefinition);
            }
        }
    }

    private void initBean(ClassLoader classLoader) throws ClassNotFoundException, InstantiationException, IllegalAccessException, BeanNotFoundException {

        Collection<BeanDefinition> beanDefinitions = beanDefinitionMap.values();

        for (BeanDefinition beanDefinition:beanDefinitions) {
            Class<?> aClass = beanDefinition.getaClass();

            if (aClass.isAnnotationPresent(Component.class)) {
                Object obj = aClass.newInstance();
                attributePadding(aClass, obj);

                //AOP代理工厂
                ProxyFactory proxyFactory = new ProxyFactory(aspectMap);
                obj = proxyFactory.AOPEnhanceByCGLIB(aClass, obj);

                attributePadding(obj.getClass(),obj);

                beanMap.put(beanDefinition.getBeanName(), obj);
            }
        }

    }

    private void attributePadding(Class<?> aClass, Object obj) throws InstantiationException, IllegalAccessException {
        Field[] parentFields = aClass.getSuperclass().getDeclaredFields();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(AutoWired.class)){
                String fieldName = field.getName();
                Object bean = getSingletonForPadding(fieldName,field.getType());
                field.setAccessible(true);
                field.set(obj,bean);
            }
        }
        for (Field field : parentFields) {
            if (field.isAnnotationPresent(AutoWired.class)){
                String fieldName = field.getName();
                Object bean = getSingletonForPadding(fieldName,field.getType());
                field.setAccessible(true);
                field.set(obj,bean);
            }
        }
    }

    public Object getSingletonForPadding(String key,Class clazz) throws InstantiationException, IllegalAccessException {
        Object obj = beanMap.get(key);
        if (obj != null){
            return obj;
        }
        Object instance = clazz.newInstance();

        beanMap.put(key,instance);

        return instance;
    }

    public Object getBean(String key) throws InstantiationException, IllegalAccessException, BeanNotFoundException {
        //无
        BeanDefinition beanDefinition = beanDefinitionMap.get(key);
        if (beanDefinition==null){
            throw new BeanNotFoundException();
        }
        String scope = beanDefinition.getScope();

        //非单例，就要创造返回
        if (scope.equals("multi")){
            return beanDefinitionMap.get(key).getaClass().newInstance();
        }

        //单例，直接返回
        return beanMap.get(key);
    }


}
