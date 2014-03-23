package com.bdb;

import com.sleepycat.je.DatabaseEntry;

import java.util.List;

/**
 * Created by ankit on 3/15/14.
 */
public class IndexManager {

    //Cache index metadata.

    public static boolean hasIndex(Relation relation, String columnName){
        IndexMetadata metadata = IndexMetadata.getMetadata(relation.getName());

        // check if metadata on index is present.
        if(null == metadata)
            return false;

        // Check if relation has index on given column.
        return metadata.hasIndexOnColumn(columnName);
    }

    public static void createIndex(Relation relation, String columnName){
        if(IndexManager.hasIndex(relation, columnName)){
            System.out.println("Index already present. Delete and recreate.");
            return;
        }

        //create or update metadata
        createIndexMatadata(relation, columnName);

        // create index for all existing records
        createIndexForAllExistingRecords(relation, columnName);

    }

    public static void createIndexTupleForNewTuple(String relName, DatabaseEntry pKey, Tuple tuple){
        IndexMetadata metadata = IndexMetadata.getMetadata(relName);

        if(null == metadata || metadata.numColsIndexed <= 0) return;

        for (String columnName : metadata.indexedColumnNames){
            DbClient dbClient = new DbClient("mydbenv","index_"+relName+"_"+columnName);
            dbClient.addIndexForNewTuple(dbClient.getMyDbEnv(), pKey, columnName, tuple);
        }

    }

    public static void deleteIndexTupleForOldTuple(String relName, DatabaseEntry pKey, Tuple tuple){
        IndexMetadata metadata = IndexMetadata.getMetadata(relName);

        if(null == metadata || metadata.numColsIndexed <= 0) return;

        for (String columnName : metadata.indexedColumnNames){
            DbClient dbClient = new DbClient("mydbenv","index_"+relName+"_"+columnName);
            dbClient.deleteIndexForOldTupleFromColumnIndex(dbClient.getMyDbEnv(), pKey, columnName, tuple);
        }
    }

    public static void updateIndexTupleForNewTuple(String relName, DatabaseEntry pKey, Tuple tuple, QueryType queryType){
        IndexMetadata metadata = IndexMetadata.getMetadata(relName);

        if(null == metadata || metadata.numColsIndexed <= 0) return;

        for (String columnName : metadata.indexedColumnNames){
            DbClient dbClient = new DbClient("mydbenv","index_"+relName+"_"+columnName);
            if(queryType.equals(QueryType.DELETE_INDEX))
                dbClient.deleteIndexForOldTupleFromColumnIndex(dbClient.getMyDbEnv(), pKey, columnName, tuple);
            else if(queryType.equals(QueryType.ADD_INDEX))
                dbClient.addIndexForNewTuple(dbClient.getMyDbEnv(), pKey, columnName, tuple);
        }
    }

    private static void createIndexForAllExistingRecords(Relation relation, String columnName){
        DbClient.LOGGER.info("Now creating index for all records....");

        // index table name format: index_relName_colName
        DbClient dbClient = new DbClient("mydbenv","index_"+relation.getName()+"_"+columnName);

        DbClient dbClient2 = new DbClient("mydbenv", relation.getName());

        dbClient.createIndex(dbClient2.getMyDbEnv(), relation, columnName);

        //TODO: do something.
    }

    private static IndexMetadata createIndexMatadata(Relation relation, String columnName) {
        IndexMetadata metadata = IndexMetadata.getMetadata(relation.getName());

        // if metadata already exists then update
        // the existing metadata.
        if(metadata != null)
        {
            metadata.addIndexColumn(columnName);
            metadata.store();
        }
        else
        {
            //insert index metadata for the first time.
            metadata = new IndexMetadata(relation.getName(), 0, new String[0]);
            metadata.addIndexColumn(columnName);
            metadata.store();
        }

        return metadata;
    }

    public static void deleteIndex(Relation relation, String columnName){
        //TODO: implement if time permits. Spec doesn't say that.
        //delete metadata
        //delete index table
    }

    public static Predicate useIndex(List<Predicate> predicates){

        // TODO: optimize whether to use index or not.
        // For now always use index if present.

        if(null == predicates || predicates.size() == 0)
            return null;

        for (Predicate p : predicates) {

            // Only use index for equals predicate.
            if(IndexManager.hasIndex(p.getLhsRelation(), p.getLhsColumn().getName()) && (p.getOperator().equals(OpType.EQUALS)))
            {
                DbClient.LOGGER.info("WILL BE USING INDEX FOR "+p.getLhsColumn().getName());
                return p;
            }
        }
        return null;
    }




}
