package com.nameless;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Created by boysz on 2018/12/6.
 */
public class HttpPostClient {


    public static void main(String[] args) throws Exception {

        String username = System.getProperty("username");
        String password = System.getProperty("password");
        System.out.println(String.format("username=%s,password=%s.",username,password));

        String queryxml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:glob=\"http://sap.com/xi/SAPGlobal20/Global\" xmlns:y62=\"http://0001092235-one-off.sap.com/Y628OLNKY_\" xmlns:yni=\"http://0001092235-one-off.sap.com/YNIIVJHSY_\"> <soapenv:Header/> <soapenv:Body><n0:ServiceOrderByElementsQuery_SYNC xmlns:n0=\"http://sap.com/xi/SAPGlobal20/Global\"> <ServiceOrderSelectionByElements> <SelectionByID> <InclusionExclusionCode>I</InclusionExclusionCode> <IntervalBoundaryTypeCode>1</IntervalBoundaryTypeCode> <LowerBoundaryID>4358</LowerBoundaryID> </SelectionByID> </ServiceOrderSelectionByElements></n0:ServiceOrderByElementsQuery_SYNC> </soapenv:Body></soapenv:Envelope>";
        doPostQuery(queryxml,String.format("%s:%s",username,password));

        String insertxml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:glob=\"http://sap.com/xi/SAPGlobal20/Global\"> <soapenv:Header/> <soapenv:Body> <glob:ServiceOrderBundleMaintainRequest_sync> <BasicMessageHeader/> <ServiceOrder> <Name languageCode=\"EN\">VK135马达上海20181205主题-muller</Name> <BuyerID>RPB181205CC0072</BuyerID> <SalesUnitParty> <PartyID>A-KS-R6-SH0303CD11</PartyID> </SalesUnitParty> <DateTime>2018-12-05T16:14:20Z</DateTime> <BuyerParty> <PartyID>SV009</PartyID> </BuyerParty> <InvoiceTerms actionCode=\"01\"> <ProposedInvoiceDate>2018-12-05</ProposedInvoiceDate> <InvoicingBlockingReasonCode>01</InvoicingBlockingReasonCode> </InvoiceTerms> <Item> <ID>10</ID> <ItemProduct> <ProductID>60631</ProductID> </ItemProduct> <Quantity unitCode=\"HUR\">5.0</Quantity> </Item> <Item> <ID>20</ID> <ItemProduct> <ProductID>60631</ProductID> </ItemProduct> <Quantity unitCode=\"HUR\">1.0</Quantity> </Item> </ServiceOrder> </glob:ServiceOrderBundleMaintainRequest_sync> </soapenv:Body></soapenv:Envelope>";
        doPostInsert(insertxml,String.format("%s:%s",username,password));

    }

    public static void doPostInsert(String ...args){

        String xml = args[0];
        String host = "my337109.sapbydesign.com";
        String endPoint = "https://%s/sap/bc/srt/scs/sap/manageserviceorderin4?sap-vhost=my337109.sapbydesign.com";
        String soapAction = "http://sap.com/xi/AP/CRM/Global/ManageServiceOrderIn/MaintainBundleRequest";
        String authEncode = null;
        try {
            authEncode = Base64.getEncoder().encodeToString(args[1].getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(
//                new AuthScope("https://my337109.sapbydesign.com", 80),
//                new UsernamePasswordCredentials("${username}", "${password}"));
//        CloseableHttpClient httpclient = HttpClients.custom()
//                .setDefaultCredentialsProvider(credsProvider)
//                .build();

        CloseableHttpClient httpclient = HttpClients.custom().build();

        try {

            HttpPost post = new HttpPost(String.format(endPoint,host));

            /**
             * 构建SOAP头
             */
            post.setHeader("Accept-Encoding","gzip,deflate");
            post.setHeader("Content-Type","text/xml;charset=UTF-8");
            post.setHeader("SOAPAction",soapAction);
            post.setHeader("Authorization",String.format("Basic %s",authEncode));
            post.setHeader("Host",host);
            post.setHeader("Connection","Keep-Alive");
            post.setHeader("User-Agent","Apache-HttpClient/4.1.1 (java 1.5)");

            /**
             * 构建content
             */
            post.setEntity(new StringEntity(xml,"UTF-8"));
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity()));
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void doPostQuery(String ...args){
        String xml = args[0];
        String host = "my337109.sapbydesign.com";
        String endPoint = "https://%s/sap/bc/srt/scs/sap/queryserviceorderin1?sap-vhost=my337109.sapbydesign.com";
        String soapAction = "http://sap.com/xi/A1S/Global/QueryServiceOrderIn/FindByElementsRequest";
        String authEncode = null;
        try {
            authEncode = Base64.getEncoder().encodeToString(args[1].getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(
//                new AuthScope("https://my337109.sapbydesign.com", 80),
//                new UsernamePasswordCredentials("${username}", "${password}"));
//        CloseableHttpClient httpclient = HttpClients.custom()
//                .setDefaultCredentialsProvider(credsProvider)
//                .build();

        CloseableHttpClient httpclient = HttpClients.custom().build();

        try {

            HttpPost post = new HttpPost(String.format(endPoint,host));

            /**
             * 构建SOAP头
             */
            post.setHeader("Accept-Encoding","gzip,deflate");
            post.setHeader("Content-Type","text/xml;charset=UTF-8");
            post.setHeader("SOAPAction",soapAction);
            post.setHeader("Authorization",String.format("Basic %s",authEncode));
            post.setHeader("Host",host);
            post.setHeader("Connection","Keep-Alive");
            post.setHeader("User-Agent","Apache-HttpClient/4.1.1 (java 1.5)");

            /**
             * 构建content
             */
            post.setEntity(new StringEntity(xml,"UTF-8"));
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity()));
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
