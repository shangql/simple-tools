package com.nameless.clazzloader;

import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Created by boysz on 2018/10/31.
 */
public class DiffJarsLoaderTest {


    /**
     * 需改pom.xml文件中注释掉的jar
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

    /**
     * 测试读取jar文件
     * 改变jar文件的加载顺序可以测试版本差异报错的问题。
     */
    @Test
    public void testLoadJars() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        Method addURL = URLClassLoader.class.getDeclaredMethod("addURL",new Class[]{ URL.class });
        addURL.setAccessible(true);

        //TODO 随机出现版本[1,2]先加载版本1则报错，现在在版本2则不报错。随机方法需要优化。
        String filename = "D:/workspace/simple-tools/jvm-tester/resources/jars/DIFF-%s.0-SNAPSHOT.jar";
        int f = 0x11;
        for(int i=0;i<100;i++){
            int version = 1 + (int)(Math.random()*2);
            if(version == 1) {
                f = f & 0x01;
                addURL.invoke(ClassLoader.getSystemClassLoader(),new Object[]{new File(String.format(filename,version)).toURI().toURL()});
            }
            if(version == 2) {
                f = f & 0x10;
                addURL.invoke(ClassLoader.getSystemClassLoader(),new Object[]{new File(String.format(filename,version)).toURI().toURL()});
            }
        }

        Class clazz = Class.forName("com.nameless.clazzloader.DiffVersionService");
        Object obj = clazz.newInstance();
        Method method = clazz.getMethod("getListBySth",new Class[]{String.class});
        //Reflection.getCallerClass();
        Object r = method.invoke(obj,new Object[]{""});
        System.out.println(r);
        System.out.println(clazz.getName());

    }

}
