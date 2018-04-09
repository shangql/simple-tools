/**
 * Copyright (c) 2015-2018 ehsure
 */
package com.nameless.hibernate.dto;


import com.nameless.hibernate.enums.ConditionFieldType;
import com.nameless.hibernate.enums.ConditionOperator;

/**
 * 查询条件项
 */
public class ConditionItem {
    private String field;
    private String value;
    private ConditionOperator operator = ConditionOperator.EQUAL;
    private ConditionFieldType type = ConditionFieldType.STRING;

    /**
     * 值
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 操作符
     */
    public ConditionOperator getOperator() {
        return operator;
    }

    public void setOperator(ConditionOperator operator) {
        this.operator = operator;
    }

    /**
     * 字段类型
     */
    public ConditionFieldType getType() {
        return type;
    }

    public void setType(ConditionFieldType type) {
        this.type = type;
    }

    /**
     * 字段名称
     */
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
