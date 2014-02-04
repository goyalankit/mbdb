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

    @Override
    public String toString() {
        String str = "\n";
        str +=  "Relation: " + relationName + "\n";
        for (DbValue dbValue : dbValues) {
            if(dbValue instanceof DbInt)
                str += dbValue.columnName + " " + ((DbInt)dbValue).getValue() + "\n";
            else
                str += dbValue.columnName + " " + ((DbString)dbValue).getValue() + "\n";
        }
        return str;
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
