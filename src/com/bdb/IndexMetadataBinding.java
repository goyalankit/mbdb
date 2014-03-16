package com.bdb;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Created by ankit on 3/15/14.
 */
public class IndexMetadataBinding extends TupleBinding {

    public Object entryToObject(TupleInput ti) {

        String rel_name = ti.readString();
        int numIndexedFields = ti.readInt();

        String [] indexedColNames =new String[numIndexedFields];

        String cl_type, cl_name;
        int cl_index;
        for (int i = 0; i < numIndexedFields; i++) {
            indexedColNames[i] = ti.readString();
        }

        IndexMetadata indexMetadata = new IndexMetadata(rel_name, numIndexedFields, indexedColNames);

        return indexMetadata;
    }

    public void objectToEntry(Object object, TupleOutput to) {

        IndexMetadata indexMetadata = (IndexMetadata)object;
        to.writeString(indexMetadata.getRelationName());
        to.writeInt(indexMetadata.getNumColsIndexed());
        for (int i = 0; i < indexMetadata.getNumColsIndexed(); i++) {
            to.writeString(indexMetadata.getIndexedColumnNames()[i]);
        }
    }

}
