package com.bdb;

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
