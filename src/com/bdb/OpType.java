package com.bdb;

/**
 * Created by ankit on 2/16/14.
 */
public enum OpType {
    EQUALS("="),
    NOT_EQUALS("!=  "),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    INVALID("");

    private String description;

    OpType(String description) {
        this.description = description;
    }

    public static OpType getOp(String op) {
        if (op.equals("=")) return OpType.EQUALS;
        else if (op.equals("!=")) return OpType.NOT_EQUALS;
        else if (op.equals(">")) return OpType.GT;
        else if (op.equals(">=")) return OpType.GTE;
        else if (op.equals("<")) return OpType.LT;
        else if (op.equals("<=")) return OpType.LTE;

        else return OpType.INVALID;
    }

}
