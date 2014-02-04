package com.bdb;

/**
 * Created by ankit on 2/3/14.
 */
public class DbInt extends DbValue {
    Integer value;

    public DbInt(Integer value) {
        this.value = value;
    }

    public DbInt(Integer value, String columnName){
        this(value);
        this.columnName = columnName;
    }

    public Integer getValue() {
        return value;
    }
}
