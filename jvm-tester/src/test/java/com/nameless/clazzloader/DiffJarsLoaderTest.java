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
     * getListBySth只有jvm-tester-2.0-SNAPSHOT.jar中存在此方法*
     * 最终会拼成 file://${绝对路径}*
     * //因为会放入Set<T>中，所以顺序是随机的，根据hashCode有关。*
     * 解释了如果路径变化，则hashCode会变化。
     * 如果低版本先加载了，那么新版本就会被排除，这样调用新版本的方法就会出现错误。
     * BTW:顺序可以通过调整pom.xml中DIFF*.jar的顺序来测试。
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
