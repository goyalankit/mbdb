package bdb.gettingStarted;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Created by ankit on 2/2/14.
 */
public class ProfBinding extends TupleBinding {



    /**
     * Implement this abstract method. Used to convert
     * a DatabaseEntry to an prof object.
     */
    public Object entryToObject(TupleInput ti) {
        int profno = ti.readInt();
        String prof_name = ti.readString();
        int department = ti.readInt();

        Prof prof = new Prof();
        prof.setProfno(profno);
        prof.setProf_name(prof_name);
        prof.setDepartment(department);

        return prof;
    }

    /**
     * Implement this abstract method. Used to convert a
     * Inventory object to a DatabaseEntry object.
     */
    public void objectToEntry(Object object, TupleOutput to) {
        Prof prof = (Prof)object;

        to.writeInt(prof.getProfno());
        to.writeString(prof.getProf_name());
        to.writeInt(prof.getDepartment());
    }


}
