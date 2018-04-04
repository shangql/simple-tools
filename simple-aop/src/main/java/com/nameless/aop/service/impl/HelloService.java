package com.nameless.aop.service.impl;

import com.nameless.aop.service.IHello;
import org.springframework.stereotype.Service;

/**
 * Created by Thinkpad on 2018/4/3.
 */
@Service("hello")
public class HelloService implements IHello {

    public String sayHello(String msg) {
        return String.format("%s",msg);
    }
}
