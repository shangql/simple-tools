package com.nameless;

import org.junit.Test;

/**
 * Created by boysz on 2018/10/16.
 */
@TestAnnotation
@TestAnnotationInherited
public class TestAnnotationUsecase {

    @Test
    public void test(){
        Class clazz = TestAnnotationUsecase.class;
        System.out.println(clazz.getAnnotation(TestAnnotation.class));
        System.out.println(clazz.getAnnotation(TestAnnotationInherited.class));
    }
}
