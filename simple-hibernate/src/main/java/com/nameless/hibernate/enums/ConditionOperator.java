package com.nameless.hibernate.enums;

/**
 * 查询条件操作符
 */
public enum ConditionOperator {

    EQUAL(0){
        public String getOperator(){
            return "=";
        }
    },
    GREATER(1){
        public String getOperator(){
            return ">";
        }
    },
    GREATER_OR_EQUAL(2){
        public String getOperator(){
            return ">=";
        }
    },
    LESS(3){
        public String getOperator(){
            return "<";
        }
    },
    LESS_OR_EQUAL(4){
        public String getOperator(){
            return "<=";
        }
    },
    LIKE(5){
        public String getOperator(){
            return "like";
        }
    },
    NOT_EQUAL(6){
        public String getOperator(){
            return "!=";
        }
    },
    IN(7){
        public String getOperator(){
            return "in";
        }
    },
    NOT_IN(8){
        public String getOperator(){
            return "not in";
        }
    },
    IS_NULL(9){
        public String getOperator(){
            return "is null";
        }
    },
    IS_NOT_NULL(10){
        public String getOperator(){
            return "is not null";
        }
    },
    MAYBE(11){
        public String getOperator(){
            return "";
        }
    },
    UN_KNOWN(99){
        public String getOperator(){
            return "unKnow";
        }
    };

    private final int index;

    ConditionOperator(int index){
        this.index=index;
    }

    public static ConditionOperator getEnum(int index){
        for(ConditionOperator conditionOperator : ConditionOperator.values()){
            if(conditionOperator.index==index){
                return conditionOperator;
            }
        }
        return ConditionOperator.UN_KNOWN;
    }

    public abstract String getOperator();
}
