package com.nameless;

import org.junit.Test;

/**
 * Created by boysz on 2018/9/28.
 */
public class JvmTester {

    /**
     * inner & outter
     * 堆内存 无明显区别
     * PS Old Gen 无明显区别
     * PS Eden Space 无明显区别
     * PS Survivor Space 无明显区别
     * Metaspace 无明显区别
     * Code Cache 无明显区别
     * Compressed Class Space 无明显区别
     */


    /**
     * 观察循环内变量对JVM的影响 Inner
     */
    @Test
    public void testVarInner() throws InterruptedException {
        long index = 0L;
        while(true) {
            String msg = new String(""+index++);
            if(index % 999999 == 0)
                Thread.sleep(100);
        }
    }

    /**
     * 观察循环内变量对JVM的影响 Outter
     */
    @Test
    public void testVarOutter() throws InterruptedException {
        long index = 0L;
        String msg = "";
        while(true) {
            msg = new String(""+index++);
            if(index % 999999 == 0)
                Thread.sleep(100);
        }
    }
}
