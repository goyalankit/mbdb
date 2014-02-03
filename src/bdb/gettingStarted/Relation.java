package bdb.gettingStarted;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by ankit on 2/2/14.
 */
public class Relation {

    /*Table related attriu*/
    String name;
    int numberOfFieldsInTuple;
    HashMap<Integer, String> fieldTypes;
    HashMap<Integer, String> fieldsIndex;

    public Relation(){

    }

    public void create(Field[] fields) {
        fieldTypes = new HashMap<Integer, String>();
        fieldsIndex= new HashMap<Integer, String>();

        name = fields[0].getDeclaringClass().getSimpleName();
        numberOfFieldsInTuple = fields.length;

        for(int i=0;i<fields.length;i++){
            fieldTypes.put(i, getTypeAsString(fields[i].getType().toString()));
            fieldsIndex.put(i, fields[i].getName().toString());
        }
    }

    private String getTypeAsString(String type){
        if(type.equals("int"))
            return Constants.INT;

        return Constants.STRING;
    }

    public String toString(){
        String str = "\n------Relation Info: "+name+"---------\n";
        str += "------Number of fields "+numberOfFieldsInTuple+"---------\n";
        for(Integer i : fieldsIndex.keySet())
            str += fieldsIndex.get(i) + " : " + fieldTypes.get(i) + "\n";

        str += "------------------------------\n";
        return str;
    }

    public void insert(){

    }

    public int getNumberOfFieldsInTuple(){
            return numberOfFieldsInTuple;
    }
}
