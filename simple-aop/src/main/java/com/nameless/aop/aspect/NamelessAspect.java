package com.nameless.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Thinkpad on 2018/4/3.
 */
public class NamelessAspect {

    private static Logger logger = LoggerFactory.getLogger(NamelessAspect.class);

    /**
     * method of aop around
     * @param proceedingJoinPoint
     * @return
     */
    public Object namelessAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {

        Object[] params = proceedingJoinPoint.getArgs();
        logger.info(String.format("method [%s] input parameters is [%s]",proceedingJoinPoint.toString(),params));
        Object result = null ;
        try {
            result = proceedingJoinPoint.proceed(params);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return result ;
    }
}
