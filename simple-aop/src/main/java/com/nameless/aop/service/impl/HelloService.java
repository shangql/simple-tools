package com.nameless.aop.service.impl;

import com.nameless.aop.service.IHello;

/**
 * Created by Thinkpad on 2018/4/3.
 */
public class HelloService implements IHello {

    public String sayHello(String msg) {
        return String.format("%s",msg);
    }
}
