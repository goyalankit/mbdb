package bdb.gettingStarted;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Transaction;

import java.io.File;
import java.io.IOException;

/**
 * Created by ankit on 2/1/14.
 */
public class DatabasePut {
    //Database ENV
    private static File myDbEnvPath = new File("/tmp/JEDB");

    // DatabaseEntries used for loading records
    private static DatabaseEntry theKey = new DatabaseEntry();
    private static DatabaseEntry theData = new DatabaseEntry();

    // Encapsulates the environment and databases.
    private static MyDbEnv myDbEnv = new MyDbEnv();

    public DatabasePut(String dbEnvFilename) {
        myDbEnvPath = new File(dbEnvFilename);
        myDbEnv.setup(myDbEnvPath, // path to the environment home
                false);      // is this environment read-only?
    }

    public boolean insertDept(int deptno, String dept_name, String chairman) {
        /* Instantiating custom binding for data in dept object */
        TupleBinding deptBinding = new DeptBinding();

        /* Initializing transaction. Multiple commands can be executed in same transaction. */
        Transaction txn = myDbEnv.getEnv().beginTransaction(null, null);

        /* converting key to database entry object */
        EntryBinding mykeybinding = TupleBinding.getPrimitiveBinding(Integer.class);
        Integer myIntegerKey = new Integer(deptno);
        try {
            mykeybinding.objectToEntry(myIntegerKey, theKey);
        } catch (Exception e) {
            System.out.println("Key could not be serialized.");
        }

        /* creating dept object for data */
        Dept theDept = new Dept();
        theDept.setDept_name(dept_name);
        theDept.setChairman(chairman);
        theDept.setDeptno(deptno);


        /* converting data to database entry object */
        deptBinding.objectToEntry(theDept, theData);

        /* Put it in the database. Note that this causes our secondary database
         to be automatically updated for us. */
        try {
            myDbEnv.getDeptDB().put(txn, theKey, theData);
        } catch (DatabaseException dbe) {
            try {
                System.out.println("Error putting entry " +
                        dept_name.getBytes("UTF-8"));
            } catch (IOException willNeverOccur) {
            }
            txn.abort();
            throw dbe;
        }

        /* Commit the transaction. The data is now safely written to the db*/
        txn.commit();

        return true;
    }

    public boolean insertEmp(int empno, int age, String dept_name, String name) {
        /* Instantiating custom binding for data in emp object */
        TupleBinding empBinding = new EmpBinding();

        /* Initializing transaction. Multiple commands can be executed in same transaction. */
        Transaction txn = myDbEnv.getEnv().beginTransaction(null, null);

        /* converting key to database entry object */
        EntryBinding mykeybinding = TupleBinding.getPrimitiveBinding(Integer.class);
        Integer myIntegerKey = new Integer(empno);
        try {
            mykeybinding.objectToEntry(myIntegerKey, theKey);
        } catch (Exception e) {
            System.out.println("Key could not be serialized.");
        }

        /* creating dept object for data */
        Emp emp = new Emp();
        emp.setEmpno(empno);
        emp.setAge(age);
        emp.setDept_name(dept_name);
        emp.setName(name);


        /* converting data to database entry object */
        empBinding.objectToEntry(emp, theData);

        /* Put it in the database. Note that this causes our secondary database
         to be automatically updated for us. */
        try {
            myDbEnv.getEmpDb().put(txn, theKey, theData);
        } catch (DatabaseException dbe) {
            try {
                System.out.println("Error putting entry " +
                        name.getBytes("UTF-8"));
            } catch (IOException willNeverOccur) {
            }
            txn.abort();
            throw dbe;
        }

        /* Commit the transaction. The data is now safely written to the db*/
        txn.commit();

        return true;
    }

    public boolean insertProf(int profno, String prof_name, String department) {
        /* Instantiating custom binding for data in emp object */
        TupleBinding profBinding = new ProfBinding();

        /* Initializing transaction. Multiple commands can be executed in same transaction. */
        Transaction txn = myDbEnv.getEnv().beginTransaction(null, null);

        /* converting key to database entry object */
        EntryBinding mykeybinding = TupleBinding.getPrimitiveBinding(Integer.class);
        Integer myIntegerKey = new Integer(profno);
        try {
            mykeybinding.objectToEntry(myIntegerKey, theKey);
        } catch (Exception e) {
            System.out.println("Key could not be serialized.");
        }

        /* creating dept object for data */
        Prof prof = new Prof();

        /* converting data to database entry object */
        profBinding.objectToEntry(prof, theData);

        /* Put it in the database. Note that this causes our secondary database
         to be automatically updated for us. */
        try {
            myDbEnv.getProfDb().put(txn, theKey, theData);
        } catch (DatabaseException dbe) {
            try {
                System.out.println("Error putting entry " +
                        prof_name.getBytes("UTF-8"));
            } catch (IOException willNeverOccur) {
            }
            txn.abort();
            throw dbe;
        }

        /* Commit the transaction. The data is now safely written to the db*/
        txn.commit();

        return true;
    }


}
