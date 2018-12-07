package com.nameless;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Base64;

/**
 * Created by boysz on 2018/12/6.
 */
public class HttpPostClient {


    public static void main(String[] args) throws Exception {

        HttpPostClient httpPostClient = new HttpPostClient();
        ;

        String username = System.getProperty("username");
        String password = System.getProperty("password");
        System.out.println(String.format("username=%s,password=%s.",username,password));

        String queryxml = httpPostClient.file2xml("xmls/queryserviceorderin1.request.xml");
        String queryrsxml = httpPostClient.doPostQuery(queryxml, String.format("%s:%s",username,password));
        httpPostClient.xml2file("xmls/queryserviceorderin1.response.xml", queryrsxml);

        String insertxml = httpPostClient.file2xml("xmls/manageserviceorderin4.request.xml");
        String insertrsxml = httpPostClient.doPostInsert(insertxml,String.format("%s:%s",username,password));
        httpPostClient.xml2file("xmls/manageserviceorderin4.response.xml", insertrsxml);

    }

    public String file2xml(String resource) throws IOException {
        return FileUtils.readFileToString(new File(getClass().getClassLoader().getResource(resource).getFile()), "UTF-8");
    }

    public void xml2file(String resource, String content) throws IOException {
        FileUtils.writeStringToFile(new File(getClass().getClassLoader().getResource(resource).getFile()) ,content,"UTF-8");
    }

    public String doPostInsert(String ...args){
        StringBuilder result = new StringBuilder();
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
                //System.out.println(EntityUtils.toString(response.getEntity()));
                result.append(EntityUtils.toString(response.getEntity()));
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

            return result.toString();
        }
    }

    public String doPostQuery(String ...args){
        StringBuilder result = new StringBuilder();
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
                //System.out.println(EntityUtils.toString(response.getEntity()));
                result.append(EntityUtils.toString(response.getEntity()));
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

            return result.toString();
        }
    }
}
