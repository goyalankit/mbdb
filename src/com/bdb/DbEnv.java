package com.bdb;

import com.sleepycat.je.*;

import java.io.File;

/**
 * Created by ankit on 2/3/14.
 */
public class DbEnv {
    private Environment myEnv;
    private Database myDatabase;
    private Database mySeqDatabase;
    private Sequence seq;

    private static Transaction userTxn = null;

    public DbEnv() {}

    public void setup(File envHome, boolean readOnly, String relation)
            throws DatabaseException {

        EnvironmentConfig myEnvConfig = new EnvironmentConfig();
        DatabaseConfig myDbConfig = new DatabaseConfig();
        SecondaryConfig mySecConfig = new SecondaryConfig();
        SequenceConfig mySeqconfig = new SequenceConfig();

        // If the environment is read-only, then
        // make the databases read-only too.
        myEnvConfig.setReadOnly(readOnly);
        myDbConfig.setReadOnly(readOnly);
        mySecConfig.setReadOnly(readOnly);

//        myEnvConfig.setTransactional(true);
//        myDbConfig.setTransactional(true);
//        mySecConfig.setReadOnly(true);

        // If the environment is opened for write, then we want to be
        // able to create the environment and databases if
        // they do not exist.
        myEnvConfig.setAllowCreate(!readOnly);
        myDbConfig.setAllowCreate(!readOnly);
        mySecConfig.setAllowCreate(!readOnly);
        mySeqconfig.setAllowCreate(!readOnly);

        // Allow transactions if we are writing to the database
        myEnvConfig.setTransactional(!readOnly);
        myDbConfig.setTransactional(!readOnly);
        mySecConfig.setTransactional(!readOnly);

        // Open the environment
        myEnv = new Environment(envHome, myEnvConfig);


        TransactionConfig txnConfig = new TransactionConfig();
        txnConfig.setReadUncommitted(true);


        if(userTxn == null){
            userTxn = myEnv.beginTransaction(null, txnConfig);
            userTxn.setName("THE ONLY TRANSACTION");
        }

        // Now open, or create and open, our databases
        // Open the vendors and inventory databases
        myDatabase = myEnv.openDatabase(userTxn,
                relation,
                myDbConfig);

        mySeqDatabase = myEnv.openDatabase(userTxn,
                                        "sequence.db",
                                        myDbConfig);

        seq = mySeqDatabase.openSequence(userTxn,
                                         new DatabaseEntry((relation+"_ID").getBytes()),
                                            mySeqconfig);
    }

    public static Transaction getUserTxn(){
        return userTxn;
    }

    public static void endTransaction(){
        userTxn = null;
        //userTxn.setTxnNull();
    }

    private static void begin(){
        System.out.println("");
    }

    public Long getRelationKey(Transaction txn){
        return  seq.get(userTxn, 1);
    }

    public Environment getEnv() { return myEnv; }

    public Database getDB() {
        return myDatabase;
    }

    public void close() {
        if (myEnv != null) {
            try {
                /* Close the secondary before closing the primaries */
                myDatabase.close();
                seq.close();
                mySeqDatabase.close();
                /* Finally, close the environment.*/
                myEnv.close();
            } catch(DatabaseException dbe) {
                System.err.println("Error closing MyDbEnv: " +
                        dbe.toString());
                System.exit(-1);
            }
        }
    }

}