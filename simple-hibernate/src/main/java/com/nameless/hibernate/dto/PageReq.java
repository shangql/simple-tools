/**
 * Copyright (c) 2015-2018 ehsure
 */
package com.nameless.hibernate.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页请求类
 */
public class PageReq implements Serializable {
    private int page=1;
    private int rows;
    private Map<String,Object> conditions=new HashMap<>();
    private List<ConditionItem> conditionItems=new ArrayList<>();

    /**
     * 查询条件键值对map
     */
    public Map<String, Object> getConditions() {
        return conditions;
    }

    public void setConditions(Map<String, Object> conditions) {
        this.conditions = conditions;
    }

    /**
     * 查询条件集合
     */
    public List<ConditionItem> getConditionItems() {
        return conditionItems;
    }

    public void setConditionItems(List<ConditionItem> conditionItems) {
        this.conditionItems = conditionItems;
    }

    /**
     * 页码，基于1
     */
    public int getPage() {
        if(page<=0){
            return 1;
        }
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    /**
     * 每页行数
     */
    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
}
