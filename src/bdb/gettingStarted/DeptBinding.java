package bdb.gettingStarted;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Created by ankit on 2/1/14.
 */
public class DeptBinding extends TupleBinding {

    // Implement this abstract method. Used to convert
    // a DatabaseEntry to an Inventory object.
    public Object entryToObject(TupleInput ti) {
        int deptno = ti.readInt();
        String dept_name = ti.readString();
        String chairman = ti.readString();

        Dept dept= new Dept();
        dept.setDeptno(deptno);
        dept.setDept_name(dept_name);
        dept.setChairman(chairman);

        return dept;
    }

    // Implement this abstract method. Used to convert a
    // Inventory object to a DatabaseEntry object.
    public void objectToEntry(Object object, TupleOutput to) {
        Dept dept = (Dept)object;
        to.writeInt(dept.getDeptno());
        to.writeString(dept.getDept_name());
        to.writeString(dept.getChairman());
    }

}
