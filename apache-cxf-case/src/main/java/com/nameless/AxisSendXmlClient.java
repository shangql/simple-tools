package com.nameless;


import org.apache.axis.Constants;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * Created by boysz on 2018/12/6.
 */
public class AxisSendXmlClient {

    /**
     * TODO 不可使用，未完成。
     * @param args
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws RemoteException
     */

    public static void main(String[] args) throws ServiceException, MalformedURLException, RemoteException {

        //File wsdl = new File("apache-cxf-case/src/main/resources/wsdl/template/query.wsdl");
        //String wsdl = "http://sap.com/xi/A1S/Global";
        String namespace = "http://sap.com/xi/A1S/Global/QueryServiceOrderIn/";



        Service service = new Service();
        Call call = (Call) service.createCall();

        //call.setTargetEndpointAddress(new URL(wsdl));
        call.setTargetEndpointAddress("https://my337109.sapbydesign.com/sap/bc/srt/scs/sap/queryserviceorderin1?sap-vhost=my337109.sapbydesign.com");

        call.setOperationName(new QName(namespace,"FindByElements"));
        //call.setOperationName("FindByElements");


        //call.addParameter(new QName(namespace,"value"), Constants.XSD_STRING, ParameterMode.IN);
        //call.addParameter("LowerBoundaryID",Constants.XSD_STRING, ParameterMode.IN);
        //call.addParameter("InclusionExclusionCode",Constants.XSD_STRING, ParameterMode.IN);
        //call.addParameter("IntervalBoundaryTypeCode",Constants.XSD_STRING, ParameterMode.IN);

        //call.setReturnType(Constants.XSD_STRING);

        //call.setUseSOAPAction(true);
        //call.setSOAPActionURI("http://sap.com/xi/A1S/Global/QueryServiceOrderIn/FindByElementsRequest");


        call.setUsername("${username}");
        call.setPassword("${password}");

        String postdata = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:glob=\"http://sap.com/xi/SAPGlobal20/Global\" xmlns:y62=\"http://0001092235-one-off.sap.com/Y628OLNKY_\" xmlns:yni=\"http://0001092235-one-off.sap.com/YNIIVJHSY_\"> <soapenv:Header/> <soapenv:Body><n0:ServiceOrderByElementsQuery_SYNC xmlns:n0=\"http://sap.com/xi/SAPGlobal20/Global\"> <ServiceOrderSelectionByElements> <SelectionByID> <InclusionExclusionCode>I</InclusionExclusionCode> <IntervalBoundaryTypeCode>1</IntervalBoundaryTypeCode> <LowerBoundaryID>4358</LowerBoundaryID> </SelectionByID> </ServiceOrderSelectionByElements></n0:ServiceOrderByElementsQuery_SYNC> </soapenv:Body></soapenv:Envelope>";

        String result = String.valueOf(call.invoke(new Object[]{postdata}));

        System.out.println(result);

    }
}
