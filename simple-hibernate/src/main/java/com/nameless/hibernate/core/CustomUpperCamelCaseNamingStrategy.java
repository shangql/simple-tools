/**
 * Copyright (c) 2015-2018 ehsure
 */
package com.nameless.hibernate.core;

import org.hibernate.cfg.DefaultNamingStrategy;

/**
 * 自定义表字段名称为CamelCase方式
 * Created by jason on 2015/8/20.
 */
public class CustomUpperCamelCaseNamingStrategy extends DefaultNamingStrategy {

    @Override
    public String propertyToColumnName(String propertyName) {
        return setFirstLetterUppercase(super.propertyToColumnName(propertyName));
    }

    @Override
    public String columnName(String columnName) {
        return setFirstLetterUppercase(columnName);
    }

    protected static String setFirstLetterUppercase(String name){
        if(name.equals(""))
            return name;
        return name.substring(0,1).toUpperCase()+name.substring(1);
    }
}
