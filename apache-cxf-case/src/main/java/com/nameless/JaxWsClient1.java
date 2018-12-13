package com.nameless;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sap.xi.a1s.global.*;
import com.sap.xi.ap.common.gdt.BusinessTransactionDocumentID;
import com.sap.xi.ap.common.gdt.QueryProcessingConditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

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


    public static void main(String[] args) throws StandardFaultMessage {


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

        // service
        QueryServiceOrderIn queryServiceOrderIn = (QueryServiceOrderIn) clientFactory.create();

        /**
         * 参数拼写
         */

        // ServiceOrderByElementsQuery_SYNC
        ServiceOrderByElementsQueryMessageSYNC request = new ServiceOrderByElementsQueryMessageSYNC();

        // ServiceOrderSelectionByElements
        ServiceOrderByElementsQuerySelectionByElementS serviceOrderSelectionByElements = new ServiceOrderByElementsQuerySelectionByElementS();
        // SelectionByID
        ServiceOrderByElementsQuerySelectionByid selectionByIDItem = new ServiceOrderByElementsQuerySelectionByid();
        selectionByIDItem.setInclusionExclusionCode("I");
        selectionByIDItem.setIntervalBoundaryTypeCode("1");
        BusinessTransactionDocumentID lowerBoundaryID = new BusinessTransactionDocumentID();
        lowerBoundaryID.setValue("4321");
        selectionByIDItem.setLowerBoundaryID(lowerBoundaryID);
        // serviceOrderSelectionByElements set value add list
        serviceOrderSelectionByElements.getSelectionByID().add(selectionByIDItem);


        // ProcessingConditions
        QueryProcessingConditions processingConditions = new QueryProcessingConditions();
        processingConditions.setQueryHitsMaximumNumberValue(30);
        processingConditions.setQueryHitsUnlimitedIndicator(false);



        // req set value
        request.setServiceOrderSelectionByElements(serviceOrderSelectionByElements);
        request.setProcessingConditions(processingConditions);


        ServiceOrderByElementsResponseMessageSynC result =  queryServiceOrderIn.findByElements(request);
        String jsonStr = gson.toJson(result);
        System.out.println(jsonStr);

//        String jsonStr = JSONObject.toJSONString(result, SerializerFeature.IgnoreNonFieldGetter);
//        String formatStr = JSONObject.toJSONString(jsonStr,true);
//        System.out.println(formatStr);




    }
}
