package com.nameless.hibernate.enums;

/**
 * 条件字段类型
 */
public enum ConditionFieldType {
    STRING("string"), INT("int"), DECIMAL("decimal"), DATETIME("datetime"), DATE("date"), BOOL("bool"), NOT_DEFINED("");

    private String text;

    ConditionFieldType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
