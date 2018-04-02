package com.nameless.poi.service;

import com.nameless.poi.abs.AbstractPoiMix;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.*;

/**
 * Created by Thinkpad on 2018/3/30.
 */
public class PoiMixForHSSF extends AbstractPoiMix {

    String filedir = "C:/temp";
    String outputdir = "C:/temp/output";
    @Override
    protected Map<String,Workbook> getSrcWorkbookList() {
        Map<String,Workbook> wbs = new LinkedHashMap<>() ;
        File dir = new File(filedir);
        if(dir.isDirectory()) {
            File[] files = dir.listFiles(new FileFilter() {
                public boolean accept(File f) {
                    if(f.getName().toLowerCase().endsWith(".xls")
                            || f.getName().toLowerCase().endsWith(".xlsx----")){
                        return true ;
                    }
                    return false;
                }
            });

            for(int i=0;i<files.length;i++ ) {
                try {
                    wbs.put(files[i].getName(),new HSSFWorkbook(new FileInputStream(files[i]))) ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return wbs;
    }
    @Override
    protected Workbook getDestWorkbook() {
        return new HSSFWorkbook();
    }
    @Override
    protected File getOutputFile() {
        File dir = new File(outputdir);
        if(!dir.exists() ) {
            dir.mkdirs();
        }
        File output = new File(String.format("%s/remix.xls",outputdir));
        return output;
    }
    @Override
    protected String formatFileName(String filename) {
        // 901722180#附件2-客户到达数-大众_201801 -> 客户到达数-大众_201801
        int idx = -1 ;
        if( (idx = filename.indexOf("#")) >= 0 ) {
            return filename.substring(idx+1).replaceAll(".xlsx","").replaceAll(".xls","");
        }
        return null;
    }
}
