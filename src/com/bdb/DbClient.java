package com.bdb;


import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.*;

import java.io.File;

/**
 * Created by ankit on 2/3/14.
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

    public void createNewRelation(Relation relation){

        TupleBinding relationBinding = new MyRelationBinding();

        /* Initializing transaction. Multiple commands can be executed in same transaction. */
        Transaction txn = myDbEnv.getEnv().beginTransaction(null, null);

        /* converting key to database entry object */
        EntryBinding mykeybinding = TupleBinding.getPrimitiveBinding(Integer.class);

        Integer myIntegerKey = new Integer(numRecordsInARelation());
        try {
            mykeybinding.objectToEntry(myIntegerKey, theKey);
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
    }

    public int numRecordsInARelation(){
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
            //System.out.println("Cursor closed");
            cursor.close();
        }

        return numRecords;
    }

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
            System.out.println("Cursor closed");
            cursor.close();
        }
        myDbEnv.close();
        return foundRelation;
    }
}
