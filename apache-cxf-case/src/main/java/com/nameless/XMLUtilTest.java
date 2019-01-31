package com.nameless;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class XMLUtilTest {

    public String file2xml(String resource) throws IOException {
        return FileUtils.readFileToString(new File(getClass().getClassLoader().getResource(resource).getFile()), "UTF-8");
    }

    public void xml2file(String resource, String content) throws IOException {
        FileUtils.writeStringToFile(new File(getClass().getClassLoader().getResource(resource).getFile()) ,content,"UTF-8");
    }

    public Map convert(String xmlStr){
        Map map = XMLUtil.xmltoMap(xmlStr);
        System.out.println(map);
        return map;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        XMLUtilTest xut = new XMLUtilTest();
        String xmlStr = xut.file2xml("xmls/testResp.xml");
        Map map = xut.convert(xmlStr);
        System.out.println(map);
    }

}
