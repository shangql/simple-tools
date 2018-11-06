package com.nameless.clazzloader;

import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
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

        /**
         * 将 protected addURL 方法设置为再外部可以调用。
         */
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

    /**
     * 测试父URLClassLoader.addURL后，子this.getClass().getClassLoader()能不能Class.forName(...).
     * 子AppClassLoader可以找到父ExtClassLoader，Loader的Class
     */
    @Test
    public void testParentLoadJars() throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        ClassLoader parentClassLoader = ClassLoader.getSystemClassLoader().getParent();
        //Parent Class Loader : sun.misc.Launcher$ExtClassLoader@452b3a41
        System.out.println("Parent Class Loader : " + parentClassLoader);

        Method addURL = URLClassLoader.class.getDeclaredMethod("addURL",new Class[]{ URL.class });
        addURL.setAccessible(true);
        addURL.invoke(parentClassLoader,
                new Object[]{
                new File("D:/workspace/simple-tools/jvm-tester/resources/jars/DIFF-2.0-SNAPSHOT.jar").toURI().toURL()});


        ClassLoader thisClassLoader = this.getClass().getClassLoader();
        thisClassLoader = URLClassLoader.getSystemClassLoader();
        //This Class Loader : sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println("This Class Loader : " + thisClassLoader);

        Class clazz = Class.forName("com.nameless.clazzloader.DiffVersionService",true,thisClassLoader);
        Object obj = clazz.newInstance();
        Method method = clazz.getMethod("getListBySth",new Class[]{String.class});
        //Reflection.getCallerClass();
        Object r = method.invoke(obj,new Object[]{""});
        System.out.println(r);
        System.out.println(clazz.getName());

    }

    /**
     * 测试子this.getClass().getClassLoader().addURL后，子URLClassLoader.addURL能不能Class.forName(...).
     *
     * AppClassLoader extends URLClassLoader , CloassLoader.getSystemClassLoader() 返回了 AppClassLoader
     *
     * 在 AppClassLoader 中注册的jar文件，在父Loader(extClassLoader, bootClassLoader中是找不到的。)
     *
     * 所以 tomcat 启动有自己的classLoader，加载 hibernate*.jar的是另一个子ClassLoader,
     * 在Tomcat环境中，可能是找不到应用中的类的。因为应用的ClassLoader是Tomcat.ClassLoader的子Loader
     */
    @Test
    public void testSubLoadJars() throws Exception {

        ClassLoader urlClassLoader = URLClassLoader.getSystemClassLoader();
        System.out.println("系统类装载器:" + urlClassLoader);
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println("系统类装载器:" + systemClassLoader);
        ClassLoader extClassLoader = systemClassLoader.getParent();
        System.out.println("系统类装载器的父类加载器——扩展类加载器:" + extClassLoader);
        ClassLoader bootClassLoader = extClassLoader.getParent();
        System.out.println("扩展类加载器的父类加载器——引导类加载器:" + bootClassLoader);

        Method addURL = URLClassLoader.class.getDeclaredMethod("addURL",new Class[]{ URL.class });
        addURL.setAccessible(true);

        // systemClassLoader 改成 extClassLoader 就可以加载到了
        addURL.invoke(systemClassLoader,
                new Object[]{
                        new File("D:/workspace/simple-tools/jvm-tester/resources/jars/DIFF-2.0-SNAPSHOT.jar").toURI().toURL()});

        Class clazz = Class.forName("com.nameless.clazzloader.DiffVersionService",true,extClassLoader);
        Object obj = clazz.newInstance();
        Method method = clazz.getMethod("getListBySth",new Class[]{String.class});
        //Reflection.getCallerClass();
        Object r = method.invoke(obj,new Object[]{""});
        System.out.println(r);
        System.out.println(clazz.getName());
    }


    private static class MyClassLoader extends URLClassLoader {
        public MyClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }
    }
    @Test
    public void testListClassLoaderUrls(){
        /**
         *
         * 如何获取当前ClassLoader已经加载的Class
         * 设置当前线程的ClassLoader
         * Thread.currentThread().setContextClassLoader(WebappLoader.class.getClassLoader());
         *
         *
         */
        ClassLoader myClassLoader = new MyClassLoader(new URL[0], ClassLoader.getSystemClassLoader());
        System.out.println(myClassLoader);
        Thread.currentThread().setContextClassLoader(myClassLoader);
        System.out.println(Thread.currentThread().getContextClassLoader());
    }

}
