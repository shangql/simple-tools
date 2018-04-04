package com.nameless;

import com.nameless.aop.service.IHello;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-core.xml"})
public class AppTest {


    @Resource
    private IHello hello ;

    @org.junit.Test
    public void testAround() {

        hello.sayHello("你好");


    }


}
