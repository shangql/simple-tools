package com.nameless.aop.advisor;

import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

/**
 * Created by Thinkpad on 2018/4/3.
 */
public class NamelessPointcutAdvisor extends AspectJAroundAdvice {

    public NamelessPointcutAdvisor(Method aspectJAroundAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {
        super(aspectJAroundAdviceMethod, pointcut, aif);
    }
}
