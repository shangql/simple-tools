/**
 * Copyright (c) 2015-2018 ehsure
 */
package com.nameless.hibernate.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
public class PageResult<E> implements Serializable {
    private long total;
    private List<E> rows;

    /**
     * 数据集合
     */
    public List<E> getRows() {
        return rows;
    }

    public void setRows(List<E> rows) {
        this.rows = rows;
    }

    /**
     * 总行数
     */
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
