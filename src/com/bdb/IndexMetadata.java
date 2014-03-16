package com.bdb;

/**
 * Created by ankit on 3/15/14.
 */
public class IndexMetadata {
    String relationName;
    int numColsIndexed; //number of columns indexed.
    String[] indexedColumnNames;

    public IndexMetadata(String relationName, int numColsIndexed, String[] indexedColumnNames) {
        this.relationName = relationName;
        this.numColsIndexed = numColsIndexed;
        this.indexedColumnNames = indexedColumnNames;
    }

    public void addIndexColumn(String columnName){
        numColsIndexed++;
        String [] newIndexedColumnNames = new String[numColsIndexed];
        newIndexedColumnNames[numColsIndexed-1] = columnName;
        for (int i = 0; i < numColsIndexed-1; i++) {
            newIndexedColumnNames[i] = indexedColumnNames[i];
        }
        indexedColumnNames = newIndexedColumnNames;
    }

    public void store(){
        DbClient dbClient = new DbClient("mydbenv","index_metadata");
        dbClient.insertIndexMetadata(this);
    }

    public static IndexMetadata getMetadata(String relationName){
        DbClient dbClient = new DbClient("mydbenv","index_metadata");
        return dbClient.getIndexMetadata(relationName);
    }

    public boolean hasIndexOnColumn(String col){

        for(String c : indexedColumnNames)
            if(c.equals(col))
                return true;

        return false;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public int getNumColsIndexed() {
        return numColsIndexed;
    }

    public void setNumColsIndexed(int numColsIndexed) {
        this.numColsIndexed = numColsIndexed;
    }

    public String[] getIndexedColumnNames() {
        return indexedColumnNames;
    }

    public void setIndexedColumnNames(String[] indexedColumnNames) {
        this.indexedColumnNames = indexedColumnNames;
    }
}
