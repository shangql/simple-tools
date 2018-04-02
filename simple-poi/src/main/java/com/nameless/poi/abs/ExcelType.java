package com.nameless.poi.abs;

/**
 * Created by Thinkpad on 2018/3/30.
 */
public enum ExcelType {
    XLS(2003),
    XLSX(2007);
    private final int code;
    private ExcelType(int code) {
        this.code = code;
    }
    public int getCode() {
        return this.code;
    }
}
