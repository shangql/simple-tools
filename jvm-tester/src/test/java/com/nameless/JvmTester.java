package com.nameless;

import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 测试直接内存
     * -ea -Xms128m -Xmx128m -XX:MaxDirectMemorySize=128m java.lang.OutOfMemoryError: Direct buffer memory
     *-ea -Xms128m -Xmx128m -XX:MaxDirectMemorySize=64m
     * 任何GC都会造成direct的GC，至于是否释放DrictMemory取决于引用变量的作用于，例如，循环内的引用比循环外的引用回收要快的多。
     */
    @Test
    public void testDirectMemory() throws InterruptedException {
        //Map<String,ByteBuffer> bmap = new HashMap<>();
        long idx = 0L;
        do {
            Map<String,ByteBuffer> bmap = new HashMap<>();
            ByteBuffer buffer = ByteBuffer.allocateDirect(10240*4);
            bmap.put(String.valueOf(idx++),buffer);
            Thread.sleep(1L);
        }
        while(true);
    }


}
