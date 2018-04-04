package com.nameless;

import com.nameless.aop.service.IHello;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        ApplicationContext ac = new ClassPathXmlApplicationContext("/spring-core.xml");
        IHello hello = (IHello) ac.getBean("hello");
        hello.sayHello("你好");

    }
}
