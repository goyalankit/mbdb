package com.bdb;

import java.util.List;

/**
 * Created by ankit on 2/3/14.
 */
public class Relation {
    private String name;
    private int numFields;
    private Column [] columns;

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
        DbClient dbClient = new DbClient("mydbenv", name);
        dbClient.insertTupleInRelation(tuple);
        return true;
    }


    /**
     *
     * Usage: getRelation("emp") : get relation object from metadata.
     *
     * */
    public static Relation getRelation(String relation){
        DbClient dbClient = new DbClient("mydbenv","metadata");
        return dbClient.getRelation(relation);
    }

    /**
     *
     * Usage: selectStar() : select * from relation.
     *
     * */

    public void selectStar(){
        DbClient dbClient = new DbClient("mydbenv", name);
        List<Tuple> tupleList = dbClient.selectStarFromRelation();
        System.out.println(tupleList.size() + "records found. \n");
        for(Tuple tuple : tupleList)
            System.out.println(tuple +"\n" );
    }

    public String toString(){
        String str = "";
        str += "Name: "+name+"\n";
        str += "NumFields: "+numFields+"\n";
        for (int i = 0; i < columns.length; i++) {
            str += "Columns: "+columns[i].name+" - "+ columns[i].type +"\n";
        }
        return str;
    }


    /* Getters and Setters */

    public String getName() { return name; }

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

    public void setColumns(Column[] columns) {
        this.columns = columns;
    }
}
