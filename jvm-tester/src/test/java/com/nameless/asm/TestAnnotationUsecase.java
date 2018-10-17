package com.nameless.asm;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.junit.Test;

/**
 * Created by boysz on 2018/10/17.
 */
public class TestAnnotationUsecase {



    public Object invoke(Object obj,String method,Object[] params) throws ClassNotFoundException {
        MethodAccess methodAccess = MethodAccess.get(obj.getClass());
        Class[] paramType = new Class[params.length];
        for(int i=0;i<params.length;i++){
            paramType[i] = params[i].getClass();
        }
        int methodIndex = methodAccess.getIndex(method,paramType);
        Class returnType = methodAccess.getReturnTypes()[methodIndex];
        Object r = methodAccess.invoke(obj,methodIndex,params);
        return r;
    }

    @Test
    public void test1() throws ClassNotFoundException {
        TestService ts = new TestServiceImpl();
        Object r = invoke(ts,"say",new Object[]{"111"});
        System.out.println("Re:"+r);
    }

    @Test
    public void test2() throws ClassNotFoundException {
        TestService ts = new TestServiceImpl();
        Object r = invoke(ts,"say",new Object[]{"stanley","what's up ?"});
        System.out.println("Re:"+r);
    }


}
