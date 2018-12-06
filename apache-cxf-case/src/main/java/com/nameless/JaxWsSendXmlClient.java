package com.nameless;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import javax.xml.namespace.QName;
import java.io.File;

/**
 * Created by boysz on 2018/12/6.
 */
public class JaxWsSendXmlClient {

    /**
     * TODO 不可使用，未完成。
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        File file = new File("apache-cxf-case/src/main/resources/wsdl/template/query.wsdl");

        String xmlStr = "";

        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient(file.toURI().toURL());

        QName name = new QName("","");
        Object[] objects = client.invoke(name,xmlStr);

        System.out.println(objects[0].toString());



    }
}

