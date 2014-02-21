package com.bdb;


import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by ankit on 2/3/14.
 * This class is meant to handle interactions between Berkley DB and MDB
 *
 */

public class DbClient {
    private static File myDbEnvPath = new File("/tmp/JEDB");

    private static DatabaseEntry theKey = new DatabaseEntry();
    private static DatabaseEntry theData = new DatabaseEntry();

    private DbEnv myDbEnv = new DbEnv();
    private static int a = 0;

    public DbClient(String dbEnvFilename, String relation) {
        dbEnvFilename = "mydbenv";
        myDbEnvPath = new File(dbEnvFilename);
        myDbEnv.setup(myDbEnvPath, // path to the environment home
                false, relation);      // is this environment read-only?
    }

    /**
     *
     * createNewRelation: creates a new relation(Table) in the metadata.
     * This is used to avoid hardcoded java classes as required by BDB.
     *
     * */

    public boolean createNewRelation(Relation relation){

        TupleBinding relationBinding = new MyRelationBinding();

        /* Initializing transaction. Multiple commands can be executed in same transaction. */
        Transaction txn = myDbEnv.getEnv().beginTransaction(null, null);

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
            myDbEnv.getDB().put(txn, theKey, theData);
        } catch (DatabaseException dbe) {
            try {
                System.out.println("Error putting entry ");
            } catch (Exception e) {
                e.printStackTrace();
            }

            txn.abort();
            throw dbe;
        }
        txn.commit();
        System.out.println("**DBClient: Table "+relation.getName() + "created.**");
        myDbEnv.close();
        return true;
    }

    /**
     *
     * insert a tuple in a table.
     *
     * */

    public boolean insertTupleInRelation(Tuple tuple){

        TupleBinding myTupleBinding = new MyTupleBinding();

        /* Initializing transaction. Multiple commands can be executed in same transaction. */
        Transaction txn = myDbEnv.getEnv().beginTransaction(null, null);

        /* converting key to database entry object */
        EntryBinding keybinding = TupleBinding.getPrimitiveBinding(Long.class);
        Long myKey = myDbEnv.getRelationKey(txn);

        try{
            keybinding.objectToEntry(myKey, theKey);
        }   catch (Exception e){

        }

        myTupleBinding.objectToEntry(tuple, theData);

        try {
            myDbEnv.getDB().put(txn, theKey, theData);
        } catch (DatabaseException dbe) {
            try {
                System.out.println("Error putting entry ");
            } catch (Exception e) {
                e.printStackTrace();
            }

            txn.abort();
            throw dbe;
        }
        txn.commit();
        System.out.println("**DBClient: Tuple inserted.**");
        myDbEnv.close();

        return true;
    }

    /**
    *
    *  select * from relation.
    *  Avoid calling this method directly. This method gets called from Relation class.
    *
    * */

    public List<Tuple> selectStarFromRelation(){
        Cursor cursor = myDbEnv.getDB().openCursor(null, null);

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
        myDbEnv.close();
        return tuples;
    }

    public Set<Tuple> getTuplesWithPredicate(List<Predicate> predicates){
        Cursor cursor = myDbEnv.getDB().openCursor(null, null);

        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();
        TupleBinding myTupleBinding = new MyTupleBinding();
        Set<Tuple> tuples = new HashSet<Tuple>();

        boolean selectAll = false;

        if(null == predicates || predicates.size() == 0)
            selectAll = true;



        try { // always want to make sure the cursor gets closed
            while (cursor.getNext(foundKey, foundData,
                    LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                Tuple t = (Tuple) myTupleBinding.entryToObject(foundData);
                boolean includeTuple = true;

                if(!selectAll){
                    for(Predicate p : predicates){
                        if(!p.applyLocal(t))
                            includeTuple = false;
                    }
                }

                if(includeTuple) tuples.add(t);

            }
        } catch (Exception e) {
            System.err.println("Error on inventory cursor:");
            System.err.println(e.toString());
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        myDbEnv.close();
        return tuples;
    }

    /**
     *
     * get the number of records in a given relation.
     * Note that this method doesn't close the environment.
     * Incorrect use may lead to weird locking errors.
     *
     * */

    private int numRecordsInARelation(){
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
     *
     * getRelation : get a relation object from BDB based on the name.
     *
     * */

    public Relation getRelation(String rel_name){
        Cursor cursor = myDbEnv.getDB().openCursor(null, null);

        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();
        TupleBinding relationBinding = new MyRelationBinding();
        Relation foundRelation = null;
        try { // always want to make sure the cursor gets closed
            while (cursor.getNext(foundKey, foundData,
                    LockMode.DEFAULT) == OperationStatus.SUCCESS) { 
                Relation relation =
                        (Relation)relationBinding.entryToObject(foundData);
                if(relation.getName().equals(rel_name)){
                    foundRelation = relation;
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Error on inventory cursor:");
            System.err.println(e.toString());
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        myDbEnv.close();
        return foundRelation;
    }
}
