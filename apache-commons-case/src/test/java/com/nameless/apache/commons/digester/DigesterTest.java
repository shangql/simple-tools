package com.nameless.apache.commons.digester;

import com.alibaba.fastjson.JSON;
import org.apache.commons.digester.Digester;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * Created by boysz on 2018/11/9.
 */
public class DigesterTest {


    @Test
    public void test(){

        Digester digester = new Digester();
        digester.setValidating(false);
        //digester.setRulesValidation(true);

        //匹配department节点时，创建Department对象
        digester.addObjectCreate("department",Department.class);

        //匹配department节点时，设置对象属性
        digester.addSetProperties("department");

        //匹配department/user节点，创建User对象
        digester.addObjectCreate("department/user",User.class);

        //匹配department/user节点时，设置对象属性
        digester.addSetProperties("department/user");

        //匹配department/user节点时，调用Department对象的addUser方法
        digester.addSetNext("department/user","addUser");

        //匹配department/extension节点时，调用Department对象的addUser方法
        digester.addCallMethod("department/extension","putExtension",2);

        //调用方法的第一个参数为节点department/extension/property-name的内容
        digester.addCallParam("department/extension/property-name",0);

        //调用方法的第二个参数为节点department/extension/property-value的内容
        digester.addCallParam("department/extension/property-value",1);

        try {
            File config = new File("./src/test/resources/apache.commons.digester/config.xml");
            Department department = (Department) digester.parse(config);
            print(department);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void print(Department dept){
        String json = JSON.toJSONString(dept,true);
        System.out.println(json);
    }
}
