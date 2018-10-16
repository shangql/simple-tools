package com.nameless;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by boysz on 2018/10/16.
 */
//@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//被子类继承
@Inherited
public @interface TestAnnotationInherited {
    String name() default "";
}
