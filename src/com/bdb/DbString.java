package com.bdb;

/**
 * Created by ankit on 2/3/14.
 */
public class DbString extends DbValue {
    String value;


    public DbString(String value, String columnName){
        this.value = value;
        this.columnName = columnName;
    }

    public String getValue() {
        return value;
    }

}
