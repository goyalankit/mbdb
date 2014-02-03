package bdb.gettingStarted;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by ankit on 2/2/14.
 */
public class RelationBinding extends TupleBinding {

    Relation rel;

    public RelationBinding(Relation rel) {
        this.rel = rel;
    }

    public Object entryToObject(TupleInput ti) {
        HashMap<Integer, String> fieldTypes = rel.fieldTypes;
        Object object = null;
        try {
            Class<?> c = Class.forName(rel.name);
            object = c.newInstance();
            Method method;

            String s = "set" + "dept_name".substring(0, 1).toUpperCase() + "dept_name".substring(1);

            for (Integer index : fieldTypes.keySet()) {
                if (fieldTypes.get(index) == Constants.INT) {
                    method = object.getClass().getMethod("setDeptno", Integer.class);
                    method.invoke(object, ti.readInt());
                } else {
                    method = object.getClass().getMethod("setDept_name", String.class);
                    method.invoke(object, ti.readString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return object;
    }

    // Implement this abstract method. Used to convert a
    // Inventory object to a DatabaseEntry object.
    public void objectToEntry(Object object, TupleOutput to) {
        HashMap<Integer, String> fieldTypes = rel.fieldTypes;

        try {
            Class<?> c = Class.forName(rel.name);
            object = c.newInstance();
            Method method;

            String s = "set" + "dept_name".substring(0, 1).toUpperCase() + "dept_name".substring(1);

        }catch (Exception e){
             e.printStackTrace();

        }

            Dept dept = (Dept) object;

        to.writeInt(dept.getDeptno());
        to.writeString(dept.getDept_name());
        to.writeString(dept.getChairman());

    }


}
