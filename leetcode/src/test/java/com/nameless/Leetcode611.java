package com.nameless;

import org.junit.Test;

/**
 * https://www.youtube.com/watch?v=yRpp-D7NlOQ
 *
 * Created by boysz on 2018/9/29.
 * 611.Valid Triangle Number
 * Given an array consists of non-negative integers. your task is to count the number of triplets chosen from the array that can make
 * triangles if we take them as side lengths of a triangle.
 *
 *
 * 三角形a+b>c
 */
public class Leetcode611 {

    int[] ints = new int[]{2,2,3,4};

    /**
     * 最初的实现，如何优化？
     */
    @Test
    public void test1() {
        for(int i=0;i<ints.length;i++){
            int a = ints[i];
            for(int j=0;j<ints.length;j++){
                int b = ints[j];
                for(int k=0;k<ints.length;k++){
                    int c = ints[k];
                    if( (a+b) > c ) {
                        System.out.println( String.format("%s %s %s",a,b,c) );
                    }
                }
            }
        }
    }
}
