package com.nameless;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * 有报错的类需要线 package
 * Created by boysz on 2018/11/16.
 */
@Slf4j
public class JaxWsClient1 {

    /**
     * END_POINT from SOAPUI
     */
    private final static String END_POINT = "https://my337109.sapbydesign.com/sap/bc/srt/scs/sap/queryserviceorderin1?sap-vhost=my337109.sapbydesign.com";


    public static void main(String[] args){


        String username = "_SERVICESORD";
        String password = "Vorwerk123";
        System.out.println(String.format("username=%s,password=%s.",username,password));

        /**
         * 如果 classpath:cxf.xml / classpath:META-INF/cxf.xml
         * 存在蔡荣如下方式创建.
         * org.apache.cxf.bus.spring.BusApplicationContext getConfigResources
         * 不存在采用如下方式创建.
         * org.apache.cxf.wsdl.service.factory.ReflectionServiceFactoryBean buildServiceFromClass
         */
        JaxWsProxyFactoryBean clientFactory = new JaxWsProxyFactoryBean();
        clientFactory.setAddress(END_POINT);


//        System.out.println(JSONObject.toJSONString(result,true));
    }
}
