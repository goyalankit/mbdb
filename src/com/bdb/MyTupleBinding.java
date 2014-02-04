package com.bdb;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Created by ankit on 2/3/14.
 */
public class MyTupleBinding extends TupleBinding{

    public Object entryToObject(TupleInput ti) {

        String name = ti.readString();
        DbClient dbClient = new DbClient("mydbenv", name);
        Relation relation = dbClient.getRelation(name);

        Tuple tuple = new Tuple(relation);
        DbValue[] dbValues = new DbValue[relation.getNumFields()];
        Column [] columns = relation.getColumns();

        for (int i = 0; i < columns.length; i++) {
            if(columns[0].type.equals("int"))
                dbValues[i] = new DbInt(ti.readInt());
            else if(columns[0].type.equals("str"))
                dbValues[i] = new DbString(ti.readString());
        }

        tuple.setDbValues(dbValues);

        return tuple;
    }

    // Implement this abstract method. Used to convert a
    // Inventory object to a DatabaseEntry object.
    public void objectToEntry(Object object, TupleOutput to) {
        Tuple tuple = (Tuple)object;
        to.writeString(tuple.getRelationName());

        DbValue [] dbValues = tuple.getDbValues();

        for (int i = 0; i < dbValues.length; i++) {
            if(dbValues[i] instanceof DbString)
                to.writeString(((DbString)dbValues[i]).getValue());
            else if(dbValues[i] instanceof DbInt)
                to.writeInt(((DbInt) dbValues[i]).getValue());
        } 
    }

}
