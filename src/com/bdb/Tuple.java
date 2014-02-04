package com.bdb;

/**
 * Created by ankit on 2/3/14.
 */
public class Tuple {
    DbValue [] dbValues;
    Relation relation;

    public Tuple(Relation r) {
        this.relation = r;
        this.dbValues = new DbValue[r.getNumFields()];
    }

    public void createTuple(DbValue[] dbValues){
        this.dbValues = dbValues;
    }

}
