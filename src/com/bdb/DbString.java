package com.bdb;

/**
 * Created by ankit on 2/3/14.
 */
public class DbString extends DbValue {
    String value;

    public DbString(String value) {

        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
