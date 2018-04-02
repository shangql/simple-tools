package com.nameless.poi.abs;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.*;
import java.util.*;

/**
 * Created by Thinkpad on 2018/3/30.
 */
public abstract class AbstractPoiMix {

    protected Map<String,HSSFSheet> sheetMap = new HashMap<>();

    protected abstract Map<String,Workbook> getSrcWorkbookList();

    protected abstract Workbook getDestWorkbook();

    protected abstract File getOutputFile();

    protected abstract String formatFileName(String filename);


    public void remix(ExcelType ... type){

        Map<String,Workbook> srcWorkbookMap = getSrcWorkbookList();
        Workbook destWorkbook = getDestWorkbook();

        Iterator<Map.Entry<String,Workbook>> iter = srcWorkbookMap.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String,Workbook> srcWorkbookEntry = iter.next();
            //2007
            if( type!=null && type.equals(ExcelType.XLSX)){

            }
            //2003
            else{
                remixSingleWorkbook_v2003( formatFileName(srcWorkbookEntry.getKey()) ,(HSSFWorkbook) srcWorkbookEntry.getValue(), (HSSFWorkbook) destWorkbook);
            }
        }

        try( FileOutputStream fileOutputStream = new FileOutputStream(getOutputFile()); ){
            destWorkbook.write(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String[] splitFileName(String formatFileName) {
        String[] r = new String[2];
        int idx = formatFileName.lastIndexOf("_");
        r[0] = formatFileName.substring(0,idx);
        r[1] = formatFileName.substring(idx+1);
        return r;
    }

    private void insertHeaderLine_v2003(HSSFSheet destSheet, HSSFCellStyle style, String value , int firstRow, int lastRow, int firstCol, int lastCol) {
        HSSFRow titleRow = destSheet.createRow(firstRow);
        HSSFCell titleCell = titleRow.createCell(firstCol);
        titleCell.setCellStyle(style);
        titleCell.setCellValue(value);
        destSheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    private HSSFCellStyle getHeaderStyle_v2003(HSSFWorkbook destWorkbook){
        HSSFCellStyle style = destWorkbook.createCellStyle();
        //背景颜色
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());// 设置背景色
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //边框
        style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM); //下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        //居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 上下居中

        //字体
        HSSFFont font = destWorkbook.createFont();
        //font.setFontName("黑体");
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
        //font.setFontHeightInPoints((short) 16);//设置字体大小
        style.setFont(font);//选择需要用到的字体格式

        return style ;
    }

    private HSSFCellStyle getTableStyle_v2003(HSSFWorkbook destWorkbook){
        //边框
        HSSFCellStyle cellStyle= destWorkbook.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

        return cellStyle ;
    }

    private void setCellVal(HSSFCell srcCell , HSSFCell destCell) {
        switch (srcCell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                destCell.setCellValue(srcCell.getNumericCellValue());
                break;
            case HSSFCell.CELL_TYPE_STRING: // 字符串
                destCell.setCellValue(srcCell.getStringCellValue());
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                destCell.setCellValue(srcCell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_FORMULA: // 公式
                destCell.setCellValue("");
                break;
            case HSSFCell.CELL_TYPE_BLANK: // 空值
                destCell.setCellValue("");
                break;
            case HSSFCell.CELL_TYPE_ERROR: // 故障
                destCell.setCellValue("");
                break;
            default:
                destCell.setCellValue("");
                break;
        }
    }

    private void remixSingleWorkbook_v2003(String formatFileName , HSSFWorkbook srcWorkbook , HSSFWorkbook destWorkbook){

        //上次追加后sheet的行数
        int prevpos = 0;

        String[] s = splitFileName(formatFileName);
        String fn = s[0]; //文件名称-菜单名称
        String optime = s[1]; //optime

        //表头样式
        HSSFCellStyle style = getHeaderStyle_v2003(destWorkbook);
        HSSFCellStyle cellStyle = getTableStyle_v2003(destWorkbook);

        for (int i = 0; i < srcWorkbook.getNumberOfSheets(); i++) {// 获取每个Sheet表
            HSSFSheet srcSheet = srcWorkbook.getSheetAt(i);
            HSSFSheet destSheet = null;
            if(sheetMap.containsKey(fn)) {
                destSheet = sheetMap.get(fn);
                prevpos = destSheet.getLastRowNum() + 1;
            }
            else{
                destSheet = destWorkbook.createSheet(String.format("%s.%s",fn,i));
                sheetMap.put(fn,destSheet);
            }

            insertHeaderLine_v2003(destSheet,style,optime,prevpos, prevpos, 0, srcSheet.getRow(0).getLastCellNum()-1);
            prevpos++;

            for (int j = 0; j < srcSheet.getLastRowNum() + 1; j++) {// getLastRowNum，获取最后一行的行标
                HSSFRow srcRow = srcSheet.getRow(j);
                HSSFRow destRow = destSheet.createRow(prevpos+j);
                if (srcRow != null) {
                    for (int k = 0; k < srcRow.getLastCellNum(); k++) {// getLastCellNum，是获取最后一个不为空的列是第几个
                        HSSFCell srcCell = srcRow.getCell(k);
                        destSheet.setColumnWidth(k, 4000);
                        HSSFCell destCell = destRow.createCell(k);
                        setCellVal(srcCell,destCell);
                        destCell.setCellStyle(cellStyle);
                    }
                }
            }

            //合拼单元格处理
            int cnt = srcSheet.getNumMergedRegions();
            for(int z =0;z<cnt ;z++){
                // 获取原表格合拼单元格信息
                CellRangeAddress ca = srcSheet.getMergedRegion(z);
                int firstRow = ca.getFirstRow();
                int lastRow = ca.getLastRow();
                int firstColumn = ca.getFirstColumn();
                int lastColumn = ca.getLastColumn();

                // 合并单元格颜色
                destSheet.getRow(prevpos+firstRow).getCell(firstColumn).setCellStyle(style);
                // 合并单元格
                CellRangeAddress cra =new CellRangeAddress(prevpos + firstRow, prevpos + lastRow, firstColumn, lastColumn); // 起始行, 终止行, 起始列, 终止列
                try {
                    destSheet.addMergedRegion(cra);
                } catch ( IllegalArgumentException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
