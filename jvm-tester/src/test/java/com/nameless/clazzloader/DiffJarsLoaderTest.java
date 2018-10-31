package com.nameless.clazzloader;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by boysz on 2018/10/31.
 */
public class DiffJarsLoaderTest {


    /**
     * 测试1.0版本和2.0版本共同加载时是否报错
     * getListBySth只有jvm-tester-2.0-SNAPSHOT.jar中存在此方法
     */
    @Test
    public void test() {
        String classname = "com.nameless.clazzloader.DiffVersionService";
        String methodname = "getListBySth";
        try {
            Class clazz = Class.forName(classname);
            Object obj = clazz.newInstance();
            Method method = clazz.getMethod(methodname,new Class[]{String.class});
            //Reflection.getCallerClass();
            Object r = method.invoke(obj,new Object[]{""});
            System.out.println(r);
            Thread.sleep(1000*60*60L);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
