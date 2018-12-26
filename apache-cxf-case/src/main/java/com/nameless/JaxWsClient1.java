package com.nameless;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sap.xi.a1s.global.*;
import com.sap.xi.a1s.global.StandardFaultMessage;
import com.sap.xi.ap.common.gdt.*;
import com.sap.xi.ap.crm.global.*;
import com.sap.xi.basis.global.Amount;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 有报错的类需要线 package
 * Created by boysz on 2018/11/16.
 */
@Slf4j
public class JaxWsClient1 {

    private final static Gson gson = new GsonBuilder()
//            .excludeFieldsWithoutExposeAnnotation() //不导出实体中没有用@Expose注解的属性
            .enableComplexMapKeySerialization() //支持Map的key为复杂对象的形式
            .serializeNulls()
//            .setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")//时间转化为特定格式
//            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)//会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
            .setPrettyPrinting() //对json结果格式化.
            .create();

    /**
     * END_POINT from SOAPUI
     */
    private final static String END_POINT = "https://my337109.sapbydesign.com/sap/bc/srt/scs/sap/queryserviceorderin1?sap-vhost=my337109.sapbydesign.com";


    public static void main(String[] args) throws StandardFaultMessage, com.sap.xi.ap.crm.global.StandardFaultMessage {


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
        clientFactory.setServiceClass(QueryServiceOrderIn.class);
        clientFactory.setUsername(username);
        clientFactory.setPassword(password);

        // 服务订单处理 管理服务订单 - 入站
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
        extendedName.setValue("wise程序提交 by ken 2018-12-26 11:14:59");
        // ServiceOrder set
        serviceOrderMaintainRequest.setName(extendedName);

        /**
         * BuyerID
         */
        BusinessTransactionDocumentID buyerID = new BusinessTransactionDocumentID();
        buyerID.setValue("RTM181210SH0068");
        // ServiceOrder set
        serviceOrderMaintainRequest.setBuyerID(buyerID); //DEMO BUYER

        /**
         * SalesUnitParty
         */
        PartyID salesUnitPartyPartyID = new PartyID();
        salesUnitPartyPartyID.setValue("A-KS-R6-SH0303CD11");
        ServiceOrderMaintainRequestPartyParty salesUnitParty = new ServiceOrderMaintainRequestPartyParty();
        salesUnitParty.setPartyID(salesUnitPartyPartyID);
        // ServiceOrder set
        serviceOrderMaintainRequest.setSalesUnitParty(salesUnitParty);

        /**
         * BuyerParty
         */
        ServiceOrderMaintainRequestPartyParty buyerParty = new ServiceOrderMaintainRequestPartyParty();
        // PartyID
        PartyID buyerPartyPartyID = new PartyID();
        buyerPartyPartyID.setValue("SV005");

        // BuyerParty set PartyID
        buyerParty.setPartyID(buyerPartyPartyID);

        // Address Obj
        ServiceOrderMaintainRequestPartyAddress buyerPartyAddress = new ServiceOrderMaintainRequestPartyAddress();

        // DisplayName Arr
        List<ServiceOrderMaintainRequestPartyAddressDisplayName> displayNameArr = buyerPartyAddress.getDisplayName();
        // DisplayName Item
        ServiceOrderMaintainRequestPartyAddressDisplayName displayName = new ServiceOrderMaintainRequestPartyAddressDisplayName();
        LONGName formattedName = new LONGName();
        formattedName.setValue("服务维修-A-KS-长春");
        displayName.setFormattedName(formattedName);
        // DisplayName Arr add Item
        displayNameArr.add(displayName);

        // Facsimile Arr
        List<ServiceOrderMaintainRequestPartyAddressFascmile> facsimileArr = buyerPartyAddress.getFacsimile();
        ServiceOrderMaintainRequestPartyAddressFascmile facsimile = new ServiceOrderMaintainRequestPartyAddressFascmile();
        facsimile.setFormattedNumberDescription("024-62538955");
        facsimile.setDefaultIndicator(true);
        // Facsimile Arr add Item
        facsimileArr.add(facsimile);

        // Telephone Arr
        List<ServiceOrderMaintainRequestPartyAddressTelephone> telephoneArr1 = buyerPartyAddress.getTelephone();
        // Telephone Item
        ServiceOrderMaintainRequestPartyAddressTelephone telephone1 = new ServiceOrderMaintainRequestPartyAddressTelephone();
        telephone1.setFormattedNumberDescription("15840058477");
        telephone1.setDefaultConventionalPhoneNumberIndicator(true);
        // telephoneArr1 add Item
        telephoneArr1.add(telephone1);

        // Telephone Arr
        List<ServiceOrderMaintainRequestPartyAddressTelephone> telephoneArr2 = buyerPartyAddress.getTelephone();
        // Telephone Item
        ServiceOrderMaintainRequestPartyAddressTelephone telephone2 = new ServiceOrderMaintainRequestPartyAddressTelephone();
        telephone2.setFormattedNumberDescription("15840058499");
        telephone2.setDefaultConventionalPhoneNumberIndicator(true);
        // telephoneArr2 add Item
        telephoneArr2.add(telephone2);

        // PostalAddress Arr
        List<ServiceOrderMaintainRequestPartyAddressPostalAddress> postalAddressArr = buyerPartyAddress.getPostalAddress();
        // PostalAddress Item
        ServiceOrderMaintainRequestPartyAddressPostalAddress postalAddress = new ServiceOrderMaintainRequestPartyAddressPostalAddress();
        postalAddress.setCountryCode("CN");
        postalAddress.setCountyName("China");
        postalAddress.setCityName("沈阳");
        postalAddress.setStreetPostalCode("888888");
        // PostalAddress Arr add Item
        postalAddressArr.add(postalAddress);

        // buyerParty set Address
        buyerParty.setAddress(buyerPartyAddress);

        // ServiceOrder set
        serviceOrderMaintainRequest.setBuyerParty(buyerParty);

        /**
         * ServicePerformerParty
         */
        PartyID servicePerformerPartyPartyID = new PartyID();
        servicePerformerPartyPartyID.setValue("8000000135");
        ServiceOrderMaintainRequestPartyParty servicePerformerParty = new ServiceOrderMaintainRequestPartyParty();
        servicePerformerParty.setPartyID(servicePerformerPartyPartyID);
        // ServiceOrder set
        serviceOrderMaintainRequest.setServicePerformerParty(servicePerformerParty);

        /**
         * Item [0]
         */
        ServiceOrderMaintainRequestItems serviceOrderMaintainRequestItems = new ServiceOrderMaintainRequestItems();
        // ID
//		BusinessTransactionDocumentID businessTransactionDocumentID = new BusinessTransactionDocumentID();
//		businessTransactionDocumentID.setValue("10");
//		serviceOrderMaintainRequest.setID(businessTransactionDocumentID);
        // Item [0] set
        serviceOrderMaintainRequestItems.setID("10");
        // ItemProduct
        ServiceOrderMaintainRequestBundleItemProduct itemProduct = new ServiceOrderMaintainRequestBundleItemProduct();
        // ProductInternalID
        ProductInternalID productInternalID = new ProductInternalID();
        productInternalID.setValue("60631");
        // UnitOfMeasure
        String unitOfMeasure = "EA";
        // ItemProduct set
        itemProduct.setProductInternalID(productInternalID);
        itemProduct.setUnitOfMeasure(unitOfMeasure);
        // Item [0] set
        serviceOrderMaintainRequestItems.setItemProduct(itemProduct);
        // Quantity
        Quantity quantity = new Quantity();
        quantity.setUnitCode("EA");
        BigDecimal quantityValue = new BigDecimal("1");
        quantity.setValue(quantityValue);
        // Item [0] set
        serviceOrderMaintainRequestItems.setQuantity(quantity);
        // MigrationListPrice
        Amount migrationListPrice = new Amount();
        migrationListPrice.setCurrencyCode("CNY");
        BigDecimal migrationListPriceValue = new BigDecimal("560.34");
        migrationListPrice.setValue(migrationListPriceValue);
        serviceOrderMaintainRequestItems.setMigrationListPrice(migrationListPrice);

        // Item arr add
        serviceOrderMaintainRequest.getItem().add(serviceOrderMaintainRequestItems);



        request.getServiceOrder().add(serviceOrderMaintainRequest);
        request.setBasicMessageHeader(header);

        log.info("==============manageServiceOrderIn.maintainBundle.request=============START");
        String requestJsonStr = JSONObject.toJSONString(request,true);
        log.info(requestJsonStr);
        log.info("==============manageServiceOrderIn.maintainBundle.request============END");

        ServiceOrderBundleMaintainConfirmation result = manageServiceOrderIn.maintainBundle(request); // 维护服务订单

        log.info("==============manageServiceOrderIn.maintainBundle.request============START");
        String resultJsonStr = JSONObject.toJSONString(result,true);
        log.info(resultJsonStr);
        log.info("==============manageServiceOrderIn.maintainBundle.request============END");




    }
}
