package com.nameless;

import com.sap.xi.ap.common.gdt.BusinessDocumentBasicMessageHeader;
import com.sap.xi.ap.common.gdt.BusinessTransactionDocumentID;
import com.sap.xi.ap.crm.global.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * 有报错的类需要线 package
 * Created by boysz on 2018/11/16.
 */
@Slf4j
public class JaxWsClient {

    /**
     * END_POINT from SOAPUI
     */
    private final static String END_POINT = "https://my337109.sapbydesign.com/sap/bc/srt/scs/sap/manageserviceorderin4?sap-vhost=my337109.sapbydesign.com";


    public static void main(String[] args) throws StandardFaultMessage {


        String username = System.getProperty("username");
        String password = System.getProperty("password");
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
        clientFactory.setServiceClass(ManageServiceOrderIn.class);
        clientFactory.setUsername(username);
        clientFactory.setPassword(password);


        ManageServiceOrderIn manageServiceOrderIn = (ManageServiceOrderIn) clientFactory.create();

        /**
         * 参数拼写
         */
        ServiceOrderBundleMaintainRequest request = new ServiceOrderBundleMaintainRequest();
        BusinessDocumentBasicMessageHeader header = new BusinessDocumentBasicMessageHeader();
        ServiceOrderMaintainRequest serviceOrderMaintainRequest = new ServiceOrderMaintainRequest();
        BusinessTransactionDocumentID buyerID = new BusinessTransactionDocumentID();
        buyerID.setValue("DEMO BUYER");
        serviceOrderMaintainRequest.setBuyerID(buyerID); //DEMO BUYER
        request.getServiceOrder().add(serviceOrderMaintainRequest);
        request.setBasicMessageHeader(header);

        ServiceOrderBundleMaintainConfirmation result = manageServiceOrderIn.maintainBundle(request);
        System.out.println(result);
    }
}
