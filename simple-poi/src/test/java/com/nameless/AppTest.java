package com.nameless;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /*
    @Test
    public void testRead2007() throws IOException, InvalidFormatException {
        File file = new File("C:/temp/入出库历史数据同步.xlsx");
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        int rowstart = xssfSheet.getFirstRowNum();
        int rowEnd = xssfSheet.getLastRowNum();

        for (int i = rowstart; i <= rowEnd; i++) {
            XSSFRow row = xssfSheet.getRow(i);
            if (null == row) continue;
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();

            for (int k = cellStart; k <= cellEnd; k++) {
                XSSFCell cell = row.getCell(k);
                if (null == cell) continue;
                switch (cell.getCellType()) {
                    case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                        System.out.print(cell.getNumericCellValue() + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_STRING: // 字符串
                        System.out.print(cell.getStringCellValue() + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                        System.out.println(cell.getBooleanCellValue() + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_FORMULA: // 公式
                        System.out.print(cell.getCellFormula() + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_BLANK: // 空值
                        System.out.println(" ");
                        break;
                    case HSSFCell.CELL_TYPE_ERROR: // 故障
                        System.out.println(" ");
                        break;
                    default:
                        System.out.print("未知类型   ");
                        break;
                }
            }
            System.out.print("\n");
        }
    }

*/
    @Test
    public void testWrite2007() {

    }

    @Test
    public void testRead() throws IOException, InvalidFormatException {
        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(new File("C:/temp/t1.xls")));
        HSSFSheet sheet = null;

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {// 获取每个Sheet表
            sheet = workbook.getSheetAt(i);
            for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {// getLastRowNum，获取最后一行的行标
                HSSFRow row = sheet.getRow(j);
                if (row != null) {
                    for (int k = 0; k < row.getLastCellNum(); k++) {// getLastCellNum，是获取最后一个不为空的列是第几个
                        if (row.getCell(k) != null) { // getCell 获取单元格数据
                            System.out.print(row.getCell(k) + "\t");
                        } else {
                            System.out.print("\t");
                        }
                    }
                }
                System.out.println(""); // 读完一行后换行
            }
            System.out.println("读取sheet表：" + workbook.getSheetName(i) + " 完成");
        }
    }


    @Test
    public void testWrite() {

        HSSFWorkbook workbook = null;
        workbook = new HSSFWorkbook();
        //获取参数个数作为excel列数
        int columeCount = 6;
        //获取List size作为excel行数
        int rowCount = 20;
        HSSFSheet sheet = workbook.createSheet("sheet name");
        //创建第一栏
        HSSFRow headRow = sheet.createRow(0);
        String[] titleArray = {"id", "name", "age", "email", "address", "phone"};
        for(int m=0;m<=columeCount-1;m++)
        {
            HSSFCell cell = headRow.createCell(m);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            sheet.setColumnWidth(m, 6000);
            HSSFCellStyle style = workbook.createCellStyle();
            HSSFFont font = workbook.createFont();
            //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            short color = HSSFColor.RED.index;
            font.setColor(color);
            style.setFont(font);
            //填写数据
            cell.setCellStyle(style);
            cell.setCellValue(titleArray[m]);

        }
        int index = 0;
        //写入数据
        /*
        for(RowEntity entity : pRowEntityList)
        {
            //logger.info("写入一行");
            HSSFRow row = sheet.createRow(index+1);
            for(int n=0;n<=columeCount-1;n++)
                row.createCell(n);
            row.getCell(0).setCellValue(entity.getId());
            row.getCell(1).setCellValue(entity.getName());
            row.getCell(2).setCellValue(entity.getAge());
            row.getCell(3).setCellValue(entity.getEmail());
            row.getCell(4).setCellValue(entity.getAddress());
            row.getCell(5).setCellValue(entity.getPhone());
            index++;
        }
        */

        //写到磁盘上
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(""));
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
