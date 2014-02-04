package com.bdb;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Created by ankit on 2/3/14.
 */
public class MyRelationBinding extends TupleBinding{

    public Object entryToObject(TupleInput ti) {

        String name = ti.readString();
        int numFields = ti.readInt();

        Column [] columns =new Column[numFields];

        String cl_type, cl_name;
        int cl_index;
        for (int i = 0; i < numFields; i++) {
            cl_type = ti.readString();
            cl_name = ti.readString();
            cl_index = ti.readInt();
            columns[i] = new Column(cl_type, cl_name, cl_index);
        }

        Relation relation = new Relation(name, numFields, columns);

        return relation;
    }

    public void objectToEntry(Object object, TupleOutput to) {

        Relation relation = (Relation)object;
        to.writeString(relation.getName());
        to.writeInt(relation.getNumFields());
        for (int i = 0; i < relation.getNumFields(); i++) {
            to.writeString(relation.getColumns()[i].type);
            to.writeString(relation.getColumns()[i].name);
            to.writeInt(relation.getColumns()[i].index);
        }
    }
}
