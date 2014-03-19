package com.bdb;

import com.sleepycat.je.DatabaseEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankit on 3/15/14.
 */
public class IndexTuple {
    String relationName;
    int numRecords;

    List<DatabaseEntry> recordKeys; //couldn't come up with better name..:(
                            //TODO: think good name

    public IndexTuple(String relationName) {
        this.relationName = relationName;
        this.numRecords = 0;
        this.recordKeys = new ArrayList<DatabaseEntry>();
    }

    public IndexTuple(String relationName, int numRecords, List<DatabaseEntry> recordKeys) {
        this.relationName = relationName;
        this.numRecords = numRecords;
        this.recordKeys = recordKeys;
    }

    public void addPrimaryKeyFromMainTable(DatabaseEntry foreignKey){
        recordKeys.add(foreignKey);
        this.numRecords = recordKeys.size();
    }

    public void removePrimaryKeyFromMainTable(DatabaseEntry foreignKey){
        recordKeys.remove(foreignKey);
        this.numRecords = recordKeys.size();
    }

    public boolean primaryKeyAlreadyPresent(DatabaseEntry primaryKey){
        for(DatabaseEntry de : recordKeys)
            if (de.equals(primaryKey))
                return true;

        return false;
    }

    public String getRelationName() {
        return relationName;
    }

    public int getNumRecords() {
        return numRecords;
    }

    public List<DatabaseEntry> getRecordKeys() {
        return recordKeys;
    }
}
