package com.bdb;

import java.util.List;

/**
 * Created by ankit on 2/3/14.
 */
public class Tuple {
    private String relationName;
    private DbValue [] dbValues;

    public Tuple(Relation relation) {
        this.relationName = relation.getName();
        this.dbValues = new DbValue[relation.getNumFields()];
    }

    public void createTuple(DbValue[] dbValues){
        this.dbValues = dbValues;
    }

    /* Don't use this directly. It is used to create joined tuples. */
    protected Tuple(){
    }

    @Override
    public String toString() {
        String str = "";
        for (DbValue dbValue : dbValues) {
            if(dbValue instanceof DbInt)
                str += ((DbInt)dbValue).getValue() + ",";
            else
                str += ((DbString)dbValue).getValue() + ",";
        }
        str = str.substring(0, str.lastIndexOf(","));
        return str;
    }

    public static void printResults(List<Tuple> tuples) {
        for (Tuple t : tuples)
            System.out.println(t);
    }

    public DbValue[] getDbValues() {
        return dbValues;
    }

    public void setDbValues(DbValue[] dbValues) {
        this.dbValues = dbValues;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

}
