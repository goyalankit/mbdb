package com.bdb;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankit on 3/16/14.
 */
public class IndexTupleBinding extends TupleBinding {

    public Object entryToObject(TupleInput ti) {

        String rel_name = ti.readString();
        int numIndexedFields = ti.readInt();
        EntryBinding mykeybinding = TupleBinding.getPrimitiveBinding(Long.class);

        List<DatabaseEntry> recordKeys = new ArrayList<DatabaseEntry>();

        DatabaseEntry de = new DatabaseEntry();
        for (int i = 0; i < numIndexedFields; i++) {
            mykeybinding.objectToEntry(ti.readLong(), de);
            recordKeys.add(de);
        }

        return (new IndexTuple(rel_name, numIndexedFields, recordKeys));
    }

    public void objectToEntry(Object object, TupleOutput to) {

        EntryBinding mykeybinding = TupleBinding.getPrimitiveBinding(Long.class);

        IndexTuple indexTuple = (IndexTuple)object;
        to.writeString(indexTuple.getRelationName());
        to.writeInt(indexTuple.getNumRecords());

        for (int i = 0; i < indexTuple.getNumRecords(); i++) {
            Long key;
            key = (Long) mykeybinding.entryToObject(indexTuple.getRecordKeys().get(i));
            to.writeLong(key);
        }
    }
}
