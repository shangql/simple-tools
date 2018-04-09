/**
 * Copyright (c) 2015-2018 ehsure
 */
package com.nameless.hibernate.base;

import com.nameless.hibernate.dto.ConditionItem;
import com.nameless.hibernate.enums.ConditionFieldType;
import com.nameless.hibernate.enums.ConditionOperator;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 分页sql构造器
 */
public class PageConditionSqlBuilder {
    private List<ConditionItem> conditionItems;
    private String originSql;
    private StringBuilder sbSql;
    private Map<String, Object> conditionValueMap = new HashMap<>();

    public PageConditionSqlBuilder(List<ConditionItem> conditionItems, String originSql, Map<String, Object> conditionValueMap) {
        this.conditionItems = conditionItems;
        this.originSql = originSql;
        this.conditionValueMap = conditionValueMap;
    }

    public Map<String, Object> getConditionValueMap() {
        return this.conditionValueMap;
    }

    public String getSql() {
        return sbSql.toString();
    }

    public void build() {
        if (this.originSql == null) {
            throw new RuntimeException("orginSql can not be null");
        }
        String noOrderBySql;
        String orderBy = "";
        Pattern pattern = Pattern.compile("\\s*order\\s+by\\s*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(originSql);
        if (matcher.find()) {
            int orderByPos = matcher.start();
            noOrderBySql = StringUtils.substring(originSql, 0, orderByPos);
            orderBy = StringUtils.substring(originSql, orderByPos);
        } else {
            noOrderBySql = originSql;
        }
        noOrderBySql = applyTemplate(noOrderBySql);
        this.sbSql = new StringBuilder(noOrderBySql.replace("\n", " "));//包含换行符时，用原生sql时，sqlserver分页时会报错

        if (conditionItems != null) {
            int pos = 0;
            for (ConditionItem item : conditionItems) {
                if (ConditionOperator.UN_KNOWN.toString().equals(item.getOperator().getOperator())) {
                    continue;
                }

                if (StringUtils.isEmpty(item.getValue())
                        && !((ConditionOperator.IS_NOT_NULL.toString().equals(item.getOperator().getOperator()))
                        || (ConditionOperator.IS_NULL.toString().equals(item.getOperator().getOperator()))
                )) {
                    continue;
                }
                pos++;
                buildItem(item, pos);
            }
        }

        if (StringUtils.isNotEmpty(orderBy)) {
            this.sbSql.append(" ").append(orderBy.replace("\n", " "));
        }
    }

    /***
     * 根据传递的查询条件的字段，匹配对应的模板条件，
     *  {{# AND ebdMaterial.shortCode like {{: field :}} #}}
     * 如果未传递查询条件值，模板条件删除，
     * 否则给conditionValueMap put 条件参数值
     * @param tmpl
     * @return
     */
    private String applyTemplate(String tmpl) {
        String result = tmpl;
        Pattern conditionSentence = Pattern.compile("\\{\\{#.*?#\\}\\}");
        Pattern field = Pattern.compile("\\{\\{:.*?:\\}\\}");
        Matcher matcher = conditionSentence.matcher(tmpl);
        while (matcher.find()) {
            String originSentence = matcher.group().substring("{{#".length(), matcher.group().length() - "#}}".length()).trim();
            Matcher fieldMatcher = field.matcher(originSentence);
            ConditionItem item = null;
            String fieldStr;
            boolean isSentenceNeeded = false;
            if (fieldMatcher.find()) {
                fieldStr = fieldMatcher.group();
                String propName = fieldStr.substring("{{:".length(), fieldStr.length() - ":}}".length()).trim();
                item = this.conditionItems.stream().filter(x -> x.getField().equals(propName)).findFirst().orElse(null);
                if (item != null) {
                    isSentenceNeeded = true;
                    this.conditionItems.remove(item);
                }
            }
            if (!isSentenceNeeded) {
                result = result.replace(matcher.group(), " ");
            } else {
                if (item.getOperator() == ConditionOperator.IS_NULL || item.getOperator() == ConditionOperator.IS_NOT_NULL) {
                    String sentence = originSentence.replace(fieldMatcher.group(), item.getOperator().getOperator());
                    result = result.replace(matcher.group(), sentence);
                } else {
                    String sentence = originSentence.replace(fieldMatcher.group(), ":" + item.getField());
                    result = result.replace(matcher.group(), sentence);
                    Object val = parseValue(item);
                    this.conditionValueMap.put(item.getField(), val);
                }
            }
            matcher = conditionSentence.matcher(result);
        }
        return result;
    }

    /**
     * 解析条件值值
     *
     * @param conditionItem 条件对象
     * @return 条件值
     */
    private Object parseValue(ConditionItem conditionItem) {
        if (conditionItem.getOperator().equals(ConditionOperator.UN_KNOWN)) {
            return null;
        }
        Object val = conditionItem.getValue();
        if (!conditionItem.getOperator().equals(ConditionOperator.IN) && !conditionItem.getOperator().equals(ConditionOperator.NOT_IN)) {
            if ((conditionItem.getType().equals(ConditionFieldType.DATE)
                    || conditionItem.getType().equals(ConditionFieldType.DATETIME))) {
                String fmtStr = conditionItem.getType().equals(ConditionFieldType.DATETIME) ? "yyyy-MM-dd HH:mm:ss" : "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(fmtStr);
                try {
                    val = sdf.parse(conditionItem.getValue());
                    //如果是小于日期则需要日期自动加一天
                    if (conditionItem.getOperator().equals(ConditionOperator.LESS) && conditionItem.getType().equals(ConditionFieldType.DATE)) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime((Date) val);
                        calendar.add(Calendar.DATE, 1);
                        val = calendar.getTime();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    val = null;
                }
            } else if (conditionItem.getType() == ConditionFieldType.BOOL) {
                switch (conditionItem.getValue()) {
                    case "-1":
                        val = null;
                        break;
                    case "1":
                        val = true;
                        break;
                    default:
                        val = Boolean.parseBoolean(conditionItem.getValue().toLowerCase());
                        break;
                }
            } else if (conditionItem.getType().equals(ConditionFieldType.INT)) {
                val = Integer.parseInt(conditionItem.getValue());
            } else if (conditionItem.getType().equals(ConditionFieldType.DECIMAL)) {
                val = new BigDecimal(conditionItem.getValue());
            } else if (conditionItem.getType().equals(ConditionFieldType.STRING)) {
                if (conditionItem.getOperator().equals(ConditionOperator.LIKE)) {
                    val = "%" + conditionItem.getValue() + "%";
                }
            }
        } else {//处理 in 和not in的情况，将值转换为数组
            if (conditionItem.getType().equals(ConditionFieldType.INT)) {
                val = stringToIntList(val.toString());
            } else if (conditionItem.getType().equals(ConditionFieldType.DECIMAL)) {
                val = stringToDecimalList(val.toString());
            } else if (conditionItem.getType().equals(ConditionFieldType.DATE)
                    || conditionItem.getType().equals(ConditionFieldType.DATETIME)) {
                val = stringToDateList(val.toString());
            } else {
                val = stringToList(val.toString());
            }
        }
        return val;
    }

    /**
     * 处理参数名称
     *
     * @param orginParamName 原始参数名称
     * @return 处理后的参数名称
     */
    private String toQualifiedParamName(String orginParamName) {
        return orginParamName.replace(".", "_");
    }

    /**
     * 将查询条件转换为sql
     *
     * @param conditionItem 查询条件对象
     * @param pos           参数位置
     */
    private void buildItem(ConditionItem conditionItem, int pos) {
        if (conditionItem.getOperator().equals(ConditionOperator.IS_NULL)) {
            this.sbSql.append(" and ").append(conditionItem.getField()).append(" IS NULL ");
        } else if (conditionItem.getOperator().equals(ConditionOperator.IS_NOT_NULL)) {
            this.sbSql.append(" and ").append(conditionItem.getField()).append(" IS NOT NULL ");
        } else {
            String paramName = toQualifiedParamName(conditionItem.getField());
            if (this.conditionValueMap.containsKey(paramName)) {
                paramName += "_" + pos;
            }
            Object val = parseValue(conditionItem);
            if (val == null) {
                return;
            }
            this.conditionValueMap.put(paramName, val);
            switch (conditionItem.getOperator()) {
                case IN:
                case NOT_IN:
                    this.sbSql
                            .append(" and ")
                            .append(conditionItem.getField())
                            .append(" ")
                            .append(conditionItem.getOperator().getOperator())
                            .append("(:")
                            .append(paramName)
                            .append(")");
                    break;
                case MAYBE:
                    this.sbSql.append(" and ").append(val);
                    break;
                case UN_KNOWN:
                    break;
                default:
                    this.sbSql
                            .append(" and ")
                            .append(conditionItem.getField())
                            .append(" ")
                            .append(conditionItem.getOperator().getOperator())
                            .append(":")
                            .append(paramName);
            }
        }
    }

    /**
     * 字符串转为字符串集合
     *
     * @param value 字符串，多个的时候以逗号分隔
     * @return 字符串集合
     */
    private static List<String> stringToList(String value) {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, value.split(","));
        return list;
    }

    /**
     * 字符串转为整数集合
     *
     * @param value 字符串，多个的时候以逗号分隔
     * @return 整数集合
     */
    private static List<Integer> stringToIntList(String value) {
        List<Integer> list = new ArrayList<>();
        for (String item : value.split(",")) {
            if (StringUtils.isEmpty(item)) {
                continue;
            }
            list.add(Integer.parseInt(item));
        }
        return list;
    }

    /**
     * 字符串转为decimal集合
     *
     * @param value 字符串，多个的时候以逗号分隔
     * @return decimal集合
     */
    private static List<BigDecimal> stringToDecimalList(String value) {
        List<BigDecimal> list = new ArrayList<>();
        for (String item : value.split(",")) {
            if (StringUtils.isEmpty(item)) {
                continue;
            }
            list.add(new BigDecimal(item));
        }
        return list;
    }

    /**
     * 字符串转为日期集合
     *
     * @param value 字符串，多个值以逗号分隔
     * @return 日期集合
     */
    private static List<Date> stringToDateList(String value) {
        List<Date> list = new ArrayList<>();
        for (String item : value.split(",")) {
            try {
                String fmtStr = "yyyy-MM-dd";
                if (item.length() > 10) {
                    fmtStr = "yyyy-MM-dd HH:mm:ss";
                }
                list.add(new SimpleDateFormat(fmtStr).parse(item));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
