package com.nameless;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by boysz on 2018/10/8.
 */
public class Console {

    static BigDecimal count = new BigDecimal(11000);
    static BigDecimal cost = new BigDecimal(16.651);
    static MathContext mc = new MathContext(4);

    public static void main(String[] args) {
        for(int i=1;i<= 15;i++){
            downside(i);
        }
//        for(int i=1;i<= 15;i++){
//            upside(i);
//        }
    }

    public static void downside(int i){

        BigDecimal count2 = new BigDecimal(1000);
        /*
        if(i > 5){
            count2 = new BigDecimal(2000);
        }
        if(i > 8){
            count2 = new BigDecimal(4000);
        }
        if(i > 13){
            count2 = new BigDecimal(8000);
        }
        */

        BigDecimal cost2 = new BigDecimal(7.15).multiply(new BigDecimal(0.90).pow(i));


        BigDecimal a = count.multiply(cost);
        BigDecimal b = count2.multiply(cost2);

        //新成本
        BigDecimal c = a.add(b).divide(count.add(count2),4);

        count = count.add(count2);
        cost = c;

        System.out.println( String.format("%s %s %s %s",count2,cost2.round(mc),count,cost.round(mc)) );

    }

    public static void upside(int i){

        BigDecimal count2 = new BigDecimal(1000);
        /*
        if(i > 5){
            count2 = new BigDecimal(2000);
        }
        if(i > 8){
            count2 = new BigDecimal(4000);
        }
        if(i > 13){
            count2 = new BigDecimal(8000);
        }
        */

        BigDecimal cost2 = new BigDecimal(7.15).multiply(new BigDecimal(1.03).pow(i));


        BigDecimal a = count.multiply(cost);
        BigDecimal b = count2.multiply(cost2);

        //新成本
        BigDecimal c = a.add(b).divide(count.add(count2),4);

        count = count.add(count2);
        cost = c;

        System.out.println( String.format("%s %s %s %s",count2,cost2.round(mc),count,cost.round(mc)) );

    }

    //gross profit ratio 毛利率
    //毛利与销售收入（或营业收入）的百分比
    //商业企业商品销售收入（售价）减去商品原进价后的余额。净利的对称，又称商品进销差价。因其尚未减去商品流通费和税金，还不是净利，故称毛利。
    public void grossProfitRatio(){

    }

    //net profit ratio 净利率
    public void netProfitRatio(){

    }
}

