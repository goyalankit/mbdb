package bdb.gettingStarted;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Created by ankit on 2/2/14.
 */
public class EmpBinding extends TupleBinding {

    // Implement this abstract method. Used to convert
    // a DatabaseEntry to an Inventory object.
    public Object entryToObject(TupleInput ti) {

        int empno = ti.readInt();
        int age = ti.readInt();

        String dept_name = ti.readString();
        String name = ti.readString();

        Emp emp= new Emp();
        emp.setEmpno(empno);
        emp.setAge(age);
        emp.setDept_name(dept_name);
        emp.setName(name);

        return emp;
    }

    // Implement this abstract method. Used to convert a
    // Inventory object to a DatabaseEntry object.
    public void objectToEntry(Object object, TupleOutput to) {

        Emp emp = (Emp)object;

        to.writeInt(emp.getEmpno());
        to.writeInt(emp.getAge());
        to.writeString(emp.getDept_name());
        to.writeString(emp.getName());

    }

}
