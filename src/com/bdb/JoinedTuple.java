package com.bdb;

/**
 * Created by ankit on 2/18/14.
 *
 * Note:
 *
 * Column name is in this class is of the format: relation_name + column_name; use cautiously
 *
 *
 *
 */
public class JoinedTuple extends Tuple {
    private DbValue [] dbValues;

    private Tuple tuple1;
    private Tuple tuple2;

    private String relationName1;
    private String relationName2;

    private Column joinedColumn;
    boolean skipJoined = false;

    public JoinedTuple(Tuple tuple1, Tuple tuple2, Column joinedColumn1, Column joinedColumn2) {
        this.tuple1 = tuple1;
        this.tuple2 = tuple2;
        this.joinedColumn = joinedColumn1;

        this.relationName1 = tuple1.getRelationName();
        this.relationName2 = tuple2.getRelationName();

        int tupleLength = tuple1.getDbValues().length + tuple2.getDbValues().length;

        //for now let's add joining field twice.
//        if(joinedColumn1.getName().equals(joinedColumn2.getName()))
//            skipJoined = true;


        tupleLength = skipJoined ? tupleLength - 1 : tupleLength;

        this.dbValues = new DbValue[tupleLength];

        build();
    }

    public void build(){

        int index = 0;

        //TODO: change the column name in value to relation.columnname this will help in long run.
        //shouldn't be a very hard change.
        for(DbValue dbValue1 : tuple1.getDbValues())
        {
            dbValues[index] = dbValue1;
            if(!dbValues[index].columnName.contains("."))
                dbValues[index].columnName = relationName1 + "." + dbValue1.columnName;

            ++index;
        }

        for(DbValue dbValue2 : tuple2.getDbValues()){

            /* For now let's add the joining field twice. This way relations can be tracked. */
            //common field must have been added above.
            //if(skipJoined && dbValue2.columnName.equals(joinedColumn.getName())) continue;
            dbValues[index] = dbValue2;
            if(!dbValues[index].columnName.contains("."))
                dbValues[index].columnName = relationName2 + "." + dbValue2.columnName;
            ++index;
        }
    }

    public String getHeader(){
        String str = "";
        for (DbValue dbValue : dbValues) {
            str += dbValue.columnName + ",";
        }
        str = str.substring(0, str.lastIndexOf(","));
        str += "\n";
        return str;
    }

    public void printHeader(){
        System.out.println(getHeader());
    }

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

    public DbValue getColumnValue(String columnName){
        for(DbValue dbValue : dbValues){
            if(dbValue.columnName.equals(columnName)){
                return dbValue;
            }
        }
        return null;
    }

    public DbValue[] getDbValues() {
        return dbValues;
    }
}
