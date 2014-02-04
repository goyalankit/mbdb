package com.bdb;

/**
 * Created by ankit on 2/3/14.
 */
public class Relation {
    private String name;
    private int numFields;
    private Column [] columns;

    //public Relation(){}

    public Relation(String name, int numFields, Column[] columns) {
        this.name = name;
        this.numFields = numFields;
        this.columns = columns;
    }

    public void create(){
        DbClient dbClient = new DbClient("mydbenv","metadata");
        dbClient.createNewRelation(this);
    }

    public boolean insert(Tuple tuple){
        //DBClient insertTupleToRelation(tuple, relation)
        return false;
    }

    public static Relation getRelation(String relation){
        DbClient dbClient = new DbClient("mydbenv","metadata");
        return dbClient.getRelation(relation);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumFields() {
        return numFields;
    }

    public void setNumFields(int numFields) {
        this.numFields = numFields;
    }

    public Column[] getColumns() {
        return columns;
    }

    public void addColumn(Column column){

    }

    public void setColumns(Column[] columns) {
        this.columns = columns;
    }
}
