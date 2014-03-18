package com.bdb;


import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ankit on 2/3/14.
 * This class is meant to handle interactions between Berkley DB and MDB
 */

public class DbClient {
    private static File myDbEnvPath = new File("/tmp/JEDB");

    private static DatabaseEntry theKey = new DatabaseEntry();
    private static DatabaseEntry theData = new DatabaseEntry();

    private DbEnv myDbEnv = new DbEnv();
    public static String dbEnvFilename = "mydbenv";
    private static Map<String, Relation> relationsCache = new HashMap<String, Relation>();

    public DbClient(String dbEnvFilename, String relation) {
        if (this.dbEnvFilename == null || this.dbEnvFilename == "mydbenv") {
            File envDir = new File("mydbenv");
            if (!envDir.exists()) {
                System.out.println("Database doesn't exist. Creating a default one by name mydbenv.");
                envDir.mkdir(); //make directory if it doesn't exist yet
                DbClient.dbEnvFilename = envDir.getName();
            }
        }
        myDbEnvPath = new File(this.dbEnvFilename);
        myDbEnv.setup(myDbEnvPath, // path to the environment home
                false, relation);      // is this environment read-only?
    }

    //begin new transaction

    public static void commit() {
        DbEnv.getUserTxn().commit();
        System.out.println("** Transaction Committed **");
        DbEnv.endTransaction();
    }

    public static void abort() {
        DbEnv.getUserTxn().abort();
        invalidateCahe();
        System.out.println("** Transaction Aborted **");
        DbEnv.endTransaction();
    }

    public DbEnv getMyDbEnv() {
        return myDbEnv;
    }

    /**
     * createNewRelation: creates a new relation(Table) in the metadata.
     * This is used to avoid hardcoded java classes as required by BDB.
     */

    public boolean createNewRelation(Relation relation) {

        TupleBinding relationBinding = new MyRelationBinding();

        /* converting key to database entry object */
        EntryBinding mykeybinding = TupleBinding.getPrimitiveBinding(String.class);

        String myStringKey = new String(relation.getName().trim());
        try {
            mykeybinding.objectToEntry(myStringKey, theKey);
        } catch (Exception e) {
            System.out.println("Key could not be serialized.");
        }

        relationBinding.objectToEntry(relation, theData);

        try {
            myDbEnv.getDB().put(myDbEnv.getUserTxn(), theKey, theData);
        } catch (DatabaseException dbe) {
            try {
                System.out.println("Error putting entry ");
            } catch (Exception e) {
                e.printStackTrace();
            }

            abort();
            throw dbe;
        }

        //txn.commit();
        System.out.println("**DBClient: Table " + relation.getName() + " created.**");

        return true;
    }

    /**
     * insert a tuple in a table.
     */

    public boolean insertTupleInRelation(Tuple tuple) {

        TupleBinding myTupleBinding = new MyTupleBinding();

        /* converting key to database entry object */
        EntryBinding keybinding = TupleBinding.getPrimitiveBinding(Long.class);
        Long myKey = myDbEnv.getRelationKey(myDbEnv.getUserTxn());

        try {
            keybinding.objectToEntry(myKey, theKey);
        } catch (Exception e) {

        }

        myTupleBinding.objectToEntry(tuple, theData);

        try {
            myDbEnv.getDB().put(myDbEnv.getUserTxn(), theKey, theData);
        } catch (DatabaseException dbe) {
            try {
                System.out.println("Error putting entry ");
            } catch (Exception e) {
                e.printStackTrace();
            }

            abort();
            throw dbe;
        }

        System.out.println("**DBClient: Tuple inserted. Not commited yet**");
        return true;
    }

    /**
     * select * from relation.
     * Avoid calling this method directly. This method gets called from Relation class.
     */

    public List<Tuple> selectStarFromRelation() {
        Cursor cursor = myDbEnv.getDB().openCursor(myDbEnv.getUserTxn(), null);

        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();
        TupleBinding myTupleBinding = new MyTupleBinding();
        List<Tuple> tuples = new ArrayList<Tuple>();
        try { // always want to make sure the cursor gets closed
            while (cursor.getNext(foundKey, foundData,
                    LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                tuples.add((Tuple) myTupleBinding.entryToObject(foundData));
            }

        } catch (Exception e) {
            System.err.println("Error on inventory cursor:");
            System.err.println(e.toString());
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return tuples;
    }

    public List<Relation> showRelationsInADatabase() {
        Cursor cursor = myDbEnv.getDB().openCursor(myDbEnv.getUserTxn(), null);

        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();

        MyRelationBinding myRelationBinding = new MyRelationBinding();

        List<Relation> relations = new ArrayList<Relation>();

        try {
            while (cursor.getNext(foundKey, foundData,
                    LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                relations.add((Relation) myRelationBinding.entryToObject(foundData));
            }

        } catch (Exception e) {
            System.err.println("Error on inventory cursor:");
            System.err.println(e.toString());
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return relations;
    }


    public void updateTuplesWithPredicate(List<Predicate> predicates, Map<Column, DbValue> updates) {
        Cursor cursor = myDbEnv.getDB().openCursor(myDbEnv.getUserTxn(), null);

        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();
        TupleBinding myTupleBinding = new MyTupleBinding();

        try { // always want to make sure the cursor gets closed
            while (cursor.getNext(foundKey, foundData,
                    LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                Tuple t = (Tuple) myTupleBinding.entryToObject(foundData);

                boolean includeTuple = true;

                for (Predicate p : predicates) {
                    if (!p.applyLocal(t))
                        includeTuple = false;
                }

                if (includeTuple) {
                    for (Column column : updates.keySet())
                        t.getDbValues()[column.getIndex()] = updates.get(column);

                    myTupleBinding.objectToEntry(t, theData);
                    myDbEnv.getDB().put(myDbEnv.getUserTxn(), foundKey, theData);
                }

            }

        } catch (Exception e) {
            System.err.println("Error on inventory cursor:");
            System.err.println(e.toString());
            abort();
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public void deleteTuplesWithPredicate(List<Predicate> predicates) {

        Cursor cursor = myDbEnv.getDB().openCursor(myDbEnv.getUserTxn(), null);

        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();
        TupleBinding myTupleBinding = new MyTupleBinding();

        try { // always want to make sure the cursor gets closed
            while (cursor.getNext(foundKey, foundData,
                    LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                Tuple t = (Tuple) myTupleBinding.entryToObject(foundData);

                boolean includeTuple = true;

                for (Predicate p : predicates) {
                    if (!p.applyLocal(t))
                        includeTuple = false;
                }

                if (includeTuple) {
                    myDbEnv.getDB().delete(myDbEnv.getUserTxn(), foundKey);
                }
            }

        } catch (Exception e) {
            System.err.println("Error on inventory cursor:");
            System.err.println(e.toString());
            abort();
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }


    // get tuples using index method below.
    public List<Tuple> getTuplesWithPredicate(List<Predicate> predicates) {
        Cursor cursor = myDbEnv.getDB().openCursor(myDbEnv.getUserTxn(), null);

        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();
        TupleBinding myTupleBinding = new MyTupleBinding();
        List<Tuple> tuples = new ArrayList<Tuple>();

        boolean selectAll = false;

        if (null == predicates || predicates.size() == 0)
            selectAll = true;


        // TODO: handle predicates empty
        Predicate indexedPredicate = IndexManager.useIndex(predicates);

        if(null == indexedPredicate)
        { // Don't use any index.
            try { // always want to make sure the cursor gets closed

                System.out.println("Not using any index..");

                while (cursor.getNext(foundKey, foundData,
                        LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                    Tuple t = (Tuple) myTupleBinding.entryToObject(foundData);
                    boolean includeTuple = true;

                    if (!selectAll) {
                        for (Predicate p : predicates) {
                            if (!p.applyLocal(t))
                                includeTuple = false;
                        }
                    }

                    if (includeTuple) tuples.add(t);

                }
            } catch (Exception e) {
                System.err.println("Error on inventory cursor:");
                System.err.println(e.toString());
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        else
        { // Use index on predicate indexedPredicate

            System.out.println("Using index on "+indexedPredicate.getLhsColumn().getName()+" ...");
            List<Tuple> prunedTuples =  getTuplesUsingIndex(indexedPredicate);
            for(Tuple t : prunedTuples){
                boolean includeTuple = true;
                for(Predicate p : predicates){
                    if(!p.equals(indexedPredicate)){
                        if (!p.applyLocal(t))
                            includeTuple = false;
                    }
                }

                if(includeTuple) tuples.add(t);
            }

        }


        return tuples;
    }

    /**
     * get the number of records in a given relation.
     * Note that this method doesn't close the environment.
     * Incorrect use may lead to weird locking errors.
     */

    private int numRecordsInARelation() {
        Cursor cursor = myDbEnv.getDB().openCursor(null, null);

        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();

        int numRecords = 0;
        try { // always want to make sure the cursor gets closed
            while (cursor.getNext(foundKey, foundData,
                    LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                numRecords += 1;
            }

        } catch (Exception e) {
            System.err.println("Error on inventory cursor:");
            System.err.println(e.toString());
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return numRecords;
    }

    /**
     * @param rel_name getRelation : get a relation object from BDB based on the name.
     */

    public Relation getRelation(String rel_name) {

        /* return if present in cache. */
        //TODO: Fix relation cache in case of transactional create database: FIXED....NEEDS TESTING
        if (relationsCache.get(rel_name) != null)
            return relationsCache.get(rel_name);

        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();


        TupleBinding relationBinding = new MyRelationBinding();
        Relation foundRelation = null;

        try {


            String myStringKey = new String(rel_name.trim());
            EntryBinding mykeybinding = TupleBinding.getPrimitiveBinding(String.class);
            mykeybinding.objectToEntry(myStringKey, foundKey);


            if (myDbEnv.getDB().get(DbEnv.getUserTxn(), foundKey, foundData, LockMode.DEFAULT)
                    == OperationStatus.SUCCESS) {

                Relation relation = (Relation) relationBinding.entryToObject(foundData);
                foundRelation = relation;
            }

/*

            OLD LOGIC TO FIND KEY BY ITERATING THROUGH ALL THE RECORDS.

            if(false){
                while (cursor.getNext(foundKey, foundData,
                        LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                    Relation relation =
                            (Relation)relationBinding.entryToObject(foundData);
                    if(relation.getName().equals(rel_name)){

                        foundRelation = relation;
                        break;
                    }
                }
            }
*/

        } catch (Exception e) {
            System.err.println("Error on inventory cursor:");
            System.err.println(e.toString());
            e.printStackTrace();
        } finally {

        }

        //update the cache.
        if (null != foundRelation) relationsCache.put(rel_name, foundRelation);

        return foundRelation;
    }

    /**
     * invalidate the relations cache. Done when database is changed.
     * Currently no delete table is present.
     */
    public static void invalidateCahe() {
        relationsCache = new HashMap<String, Relation>();
    }


    /*-----------------------------------------------------------------------------------------------------------------*/
    /**
     *
     *
     * Index Related Methods
     *
     *
     * */


    /**
     * Insert index metadata.
     */
    public boolean insertIndexMetadata(IndexMetadata indexMetadata) {
        TupleBinding indexMetadataBinding = new IndexMetadataBinding();

        /* converting key to database entry object */
        EntryBinding mykeybinding = TupleBinding.getPrimitiveBinding(String.class);

        String myStringKey = new String(indexMetadata.getRelationName().trim());

        try {
            mykeybinding.objectToEntry(myStringKey, theKey);
            indexMetadataBinding.objectToEntry(indexMetadata, theData);
            myDbEnv.getDB().put(myDbEnv.getUserTxn(), theKey, theData);

        } catch (DatabaseException dbe) {
            System.out.println("Error putting entry ");
            abort();
            throw dbe;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Key could not be serialized.");
        }

        System.out.println("**DBClient: Index Metadata for " + indexMetadata.getRelationName() + " created.**");

        return true;
    }


    /**
     *
     * get Metadata for index.
     *
     */
    public IndexMetadata getIndexMetadata(String relation) {
        TupleBinding indexMetadataBinding = new IndexMetadataBinding();

        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();

        IndexMetadata indexMetadata = null;

        try {

            String myStringKey = new String(relation.trim());
            EntryBinding mykeybinding = TupleBinding.getPrimitiveBinding(String.class);
            mykeybinding.objectToEntry(myStringKey, foundKey);

            if (myDbEnv.getDB().get(DbEnv.getUserTxn(), foundKey, foundData, LockMode.DEFAULT)
                    == OperationStatus.SUCCESS) {
                indexMetadata = (IndexMetadata) indexMetadataBinding.entryToObject(foundData);
            }

        } catch (DatabaseException dbe) {
            System.out.println("Error putting entry ");
            dbe.printStackTrace();
            abort();
            throw dbe;
        } catch (Exception e) {
            System.out.println("Key could not be serialized.");
            e.printStackTrace();
        }

        return indexMetadata;
    }

    /**
     *
     * create indexes for the given table and column
     *
     * */

    public void createIndex(DbEnv relDbEnv, Relation relation, String colName){
        Cursor relCursor = relDbEnv.getDB().openCursor(DbEnv.getUserTxn(), null);

        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();

        String relName = relation.getName();
        int colIndex = relation.getColumn(colName).index;

        Map<DbValue, IndexTuple> stringListMap = new HashMap<DbValue, IndexTuple>();
        TupleBinding myTupleBinding = new MyTupleBinding();


        try {

            /**
             *
             * First get the hash of all records.
             * There are two options: Insert asap or get the hash.
             *
             * */

            IndexTuple indexTuple;
            List<IndexTuple> temp;

            while (relCursor.getNext(foundKey, foundData,
                    LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                // get the tuple
                Tuple tuple = (Tuple) myTupleBinding.entryToObject(foundData);

                // check get the new indexed key value
                DbValue currentValue = tuple.getDbValues()[colIndex];

                if(stringListMap.containsKey(currentValue))
                {
                    stringListMap.get(currentValue).addForeignKey(foundKey);
                }
                else
                {
                    indexTuple = new IndexTuple(relName);
                    indexTuple.addForeignKey(foundKey);
                    stringListMap.put(currentValue, indexTuple);
                }
            }

            /**
             *
             * Hash[column value] = [key1, key2, key3]
             * Add this to index tuple.
             *
             * */

            EntryBinding myStrKeyBinding = TupleBinding.getPrimitiveBinding(String.class);
            EntryBinding myIntKeyBinding = TupleBinding.getPrimitiveBinding(Integer.class);

            TupleBinding indexTupleBinding  = new IndexTupleBinding();

            DatabaseEntry newKey = new DatabaseEntry();
            DatabaseEntry newValue = new DatabaseEntry();

            // create a new table with all the indexes.
            for(DbValue str : stringListMap.keySet()){
                if(str instanceof DbString){
                    myStrKeyBinding.objectToEntry(((DbString)str).value, newKey);
                }
                else{
                    myIntKeyBinding.objectToEntry(((DbInt)str).value, newKey);
                }

                indexTupleBinding.objectToEntry(stringListMap.get(str), newValue);

                myDbEnv.getDB().put(DbEnv.getUserTxn(), newKey, newValue);
            }

        } catch (Exception e) {
            System.err.println("Error on inventory cursor:");
            System.err.println(e.toString());
            e.printStackTrace();
        } finally {
            System.out.println("Index Created");
            relCursor.close();
        }
    }

    public List<Tuple> getTuplesUsingIndex(Predicate predicate){


        List<Tuple> tuples = new ArrayList<Tuple>();
        DbClient dbClient = new DbClient("mydbenv",
                "index_"+predicate.getLhsRelation().getName()+"_"+predicate.getLhsColumn().getName());


        DatabaseEntry searchKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();

        DbValue searchValue = predicate.getRhsValue();

        EntryBinding myBinding;
        if(searchValue instanceof DbString){
            myBinding = TupleBinding.getPrimitiveBinding(String.class);
            myBinding.objectToEntry(((DbString)searchValue).value, searchKey);
        }
        else{
            myBinding = TupleBinding.getPrimitiveBinding(Integer.class);
            myBinding.objectToEntry(((DbInt)searchValue).value, searchKey);
        }


        TupleBinding indexTupleBinding  = new IndexTupleBinding();

        TupleBinding myTupleBinding = new MyTupleBinding();

        if (dbClient.getMyDbEnv().getDB().get(DbEnv.getUserTxn(), searchKey, foundData, LockMode.DEFAULT)
                == OperationStatus.SUCCESS) {

            IndexTuple indexTuple = (IndexTuple) indexTupleBinding.entryToObject(foundData);

            DatabaseEntry tupleInMain = new DatabaseEntry();
            for(DatabaseEntry de : indexTuple.recordKeys){
                if (myDbEnv.getDB().get(DbEnv.getUserTxn(), de, tupleInMain, LockMode.DEFAULT)
                        == OperationStatus.SUCCESS) {
                    tuples.add((Tuple) myTupleBinding.entryToObject(tupleInMain));
                }
            }
        }

        return tuples;
    }

}
