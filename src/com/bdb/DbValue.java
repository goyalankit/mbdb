package com.bdb;

/**
 * Created by ankit on 2/3/14.
 */
public class DbValue {
    String columnName;

    public boolean equals(Object o) {
        if(o instanceof DbInt)
        {
            return ((DbInt)this).equals(o);
        }
        else if(o instanceof DbString)
        {
            return ((DbString)this).equals(o);
        }
        else{
            return false;
        }
    }

}
