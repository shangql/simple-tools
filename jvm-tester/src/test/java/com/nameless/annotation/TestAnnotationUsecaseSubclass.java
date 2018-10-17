package com.nameless.annotation;

import org.junit.Test;

/**
 * Created by boysz on 2018/10/16.
 */
//子类注解的值会覆盖掉父类的值
//@TestAnnotationInherited(name="A")
public class TestAnnotationUsecaseSubclass extends TestAnnotationUsecase {

    @Test
    public void test(){

        Class clazz = TestAnnotationUsecaseSubclass.class;
        //子类不能继承的注解
        System.out.println(clazz.getAnnotation(TestAnnotation.class));
        //子类可以继承的注解 @Inherited
        System.out.println(clazz.getAnnotation(TestAnnotationInherited.class));

        TestAnnotationInherited testAnnotationInherited = (TestAnnotationInherited)clazz.getAnnotation(TestAnnotationInherited.class);
        System.out.println(testAnnotationInherited.name());

    }
}
