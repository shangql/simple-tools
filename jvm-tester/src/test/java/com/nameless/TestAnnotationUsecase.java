package com.nameless;

import org.junit.Test;

import java.lang.annotation.Annotation;

/**
 * Created by boysz on 2018/10/16.
 */
@TestAnnotation(name="TestAnnotationUsecase.TestAnnotation")
@TestAnnotationInherited(name="TestAnnotationUsecase.TestAnnotationInherited")
public class TestAnnotationUsecase {

    @Test
    public void test(){
        Class clazz = TestAnnotationUsecase.class;
        TestAnnotation testAnnotation = (TestAnnotation)clazz.getAnnotation(TestAnnotation.class);
        System.out.println(testAnnotation.name());
        TestAnnotationInherited testAnnotationInherited = (TestAnnotationInherited)clazz.getAnnotation(TestAnnotationInherited.class);
        System.out.println(testAnnotationInherited.name());
    }
}
