package com.nameless;

import com.alibaba.fastjson.JSONObject;
import com.sap.xi.ap.common.gdt.*;
import com.sap.xi.ap.crm.global.*;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;

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

        /**
         * Name
         */
        EXTENDEDName extendedName = new EXTENDEDName();
        extendedName.setLanguageCode("EN");
        extendedName.setValue("VK135马达上海20181205主题-muller");
        serviceOrderMaintainRequest.setName(extendedName);

        /**
         * BuyerID
         */
        BusinessTransactionDocumentID buyerID = new BusinessTransactionDocumentID();
        buyerID.setValue("RPB181205CC0072");
        serviceOrderMaintainRequest.setBuyerID(buyerID); //DEMO BUYER

        /**
         * SalesUnitParty
         */
        PartyID salesUnitPartyPartyID = new PartyID();
        salesUnitPartyPartyID.setValue("A-KS-R6-SH0303CD11");
        ServiceOrderMaintainRequestPartyParty salesUnitParty = new ServiceOrderMaintainRequestPartyParty();
        salesUnitParty.setPartyID(salesUnitPartyPartyID);
        serviceOrderMaintainRequest.setSalesUnitParty(salesUnitParty);

        /**
         * DateTime
         */
        XMLGregorianCalendar dateTime = new XMLGregorianCalendarImpl();
        serviceOrderMaintainRequest.setDateTime(dateTime);

        /**
         * BuyerParty
         */
        PartyID buyerPartyPartyId = new PartyID();
        buyerPartyPartyId.setValue("SV009");
        ServiceOrderMaintainRequestPartyParty buyerParty = new ServiceOrderMaintainRequestPartyParty();
        buyerParty.setPartyID(buyerPartyPartyId);
        serviceOrderMaintainRequest.setBuyerParty(buyerParty);

        /**
         * InvoiceTerms
         */
        XMLGregorianCalendar proposedInvoiceDate = new XMLGregorianCalendarImpl();
        ServiceOrderMaintainRequestInvoiceTerms invoiceTerms = new ServiceOrderMaintainRequestInvoiceTerms();
        invoiceTerms.setActionCode("01");
        invoiceTerms.setInvoicingBlockingReasonCode("01");
        invoiceTerms.setProposedInvoiceDate(proposedInvoiceDate);
        serviceOrderMaintainRequest.setInvoiceTerms(invoiceTerms);

        /**
         * Item
         */
        NOCONVERSIONProductID itemItemProductProductId0 = new NOCONVERSIONProductID();
        itemItemProductProductId0.setValue("60631");

        ServiceOrderMaintainRequestBundleItemProduct itemProduct0 = new ServiceOrderMaintainRequestBundleItemProduct();
        itemProduct0.setProductID(itemItemProductProductId0);

        Quantity itemQuantity = new Quantity();
        itemQuantity.setUnitCode("HUR");
        itemQuantity.setValue(new BigDecimal("5.0"));

        ServiceOrderMaintainRequestItems item0 = new ServiceOrderMaintainRequestItems();
        item0.setID("10");
        item0.setItemProduct(itemProduct0);
        item0.setQuantity(itemQuantity);
        serviceOrderMaintainRequest.getItem().add(item0);


        /**
         * SerInvoiceTypeCI
         */
        serviceOrderMaintainRequest.setSerInvoiceTypeCI("102");

        /**
         * SerInvoiceCustomerAddressTelCI
         */
        serviceOrderMaintainRequest.setSerInvoiceCustomerAddressTelCI("13564549041");

        /**
         * SerInvoiceCustomerTaxNumberCI
         */
        serviceOrderMaintainRequest.setSerInvoiceCustomerTaxNumberCI("13564549041");


        request.getServiceOrder().add(serviceOrderMaintainRequest);
        request.setBasicMessageHeader(header);
        ServiceOrderBundleMaintainConfirmation result = manageServiceOrderIn.maintainBundle(request);
        System.out.println(JSONObject.toJSONString(result,true));
    }
}
