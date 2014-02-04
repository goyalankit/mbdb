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
        Relation relation = Relation.getRelation(name); /* get the corresponding relation object from metadata table */

        Tuple tuple = new Tuple(relation);
        DbValue[] dbValues = new DbValue[relation.getNumFields()];
        Column [] columns = relation.getColumns();

        for (int i = 0; i < columns.length; i++) {
            if(columns[i].type.equals("int"))
                dbValues[i] = new DbInt(ti.readInt(), columns[i].getName());
            else if(columns[i].type.equals("str"))
                dbValues[i] = new DbString(ti.readString(), columns[i].getName());
        }

        tuple.setDbValues(dbValues);
        return tuple;
    }

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
