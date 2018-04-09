package com.nameless;

import static org.junit.Assert.assertTrue;

import com.nameless.hibernate.entity.CCPDataSource;
import com.nameless.hibernate.repository.HelloRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * Unit test for simple App.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-core.xml"})
public class AppTest {

    @Resource
    private HelloRepository helloRepository;

    @Test
    //@Transactional(Transactional.TxType.REQUIRED)
    public void shouldAnswerWithTrue() {

        CCPDataSource c = new CCPDataSource();
        c.setId(1);
        c.setCode("TEST");
        c.setName("测试");

        //helloRepository.delete(c);

        helloRepository.save(c);



        //assertTrue(true);
    }
}
