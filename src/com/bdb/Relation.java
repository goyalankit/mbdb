package com.bdb;

import java.util.ArrayList;
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

    /**
     *
     * insert a tuple(record) in table(Relation).
     * @param tuple
     * @return
     */

    public boolean insert(Tuple tuple){
        DbClient dbClient = new DbClient("mydbenv", name);
        dbClient.insertTupleInRelation(tuple);
        return true;
    }

    /**
     *
     * get relation object from metadata.
     * @param relation
     * @return
     */
     public static Relation getRelation(String relation){
         if(DbClient.relationsCache.containsKey(relation))
             return DbClient.relationsCache.get(relation);

         DbClient dbClient = new DbClient("mydbenv","metadata");
         return dbClient.getRelation(relation);
     }

    public static void buildCache(){
        DbClient dbClient = new DbClient("mydbenv","metadata");
        dbClient.buildRelationCache();

        for(Relation r : DbClient.relationsCache.values()){
            DbClient dbClient1 = new DbClient("mydbenv", r.getName());
            dbClient1.getTuplesWithPredicate(new ArrayList<Predicate>());
        }
    }


    /**
     *  select * from a given table(Relation)
     *  Deprecated : using general join method.
     */
    public List<Tuple> selectStar(){
        DbClient dbClient = new DbClient("mydbenv", name);
        List<Tuple> tupleList = dbClient.selectStarFromRelation();
        System.out.println(tupleList.size() + "records found. \n");
        for(Tuple tuple : tupleList)
            System.out.println(tuple +"\n" );
        return tupleList;
    }

    public Column getColumn(String name){
        for(Column c: columns){
            if(c.getName().equals(name)){
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString(){
        String str = "";
        str += "Name: "+name+"\n";
        str += "NumFields: "+numFields+"\n";
        for (int i = 0; i < columns.length; i++) {
            str += "Column: "+columns[i].name+" - "+ columns[i].type +"\n";
        }
        return str;
    }

    public void showRelation(){
        System.out.format("\n\n Relation: "+name +"%n");

        String leftAlignFormat = "| %-15s | %-6s |%n";
        System.out.format("\n+-----------------+--------+%n");
        System.out.printf("| Column name     | Type   |%n");
        System.out.format("+-----------------+--------+%n");

        for (int i = 0; i < columns.length; i++) {
            System.out.format(leftAlignFormat, columns[i].name , columns[i].type);
        }
        System.out.format("+-----------------+--------+%n");

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
