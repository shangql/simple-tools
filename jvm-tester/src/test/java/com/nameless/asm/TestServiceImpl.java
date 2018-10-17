package com.nameless.asm;

/**
 * Created by boysz on 2018/10/17.
 */
public class TestServiceImpl implements TestService {

    @Override
    public void say() {
        System.out.println("...String say()");
    }

    @Override
    public String say(String message ){
        System.out.println("...String say(String message )");
        return message ;
    }

    @Override
    public TestBean say(String name, String value) {
        System.out.println("...String say(String name, String value)");
        TestBean tb = new TestBean();
        tb.setName(name);
        tb.setValue(value);
        return tb;
    }
}
