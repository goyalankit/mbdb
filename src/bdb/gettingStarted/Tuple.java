package bdb.gettingStarted;


/**
 * Created by ankit on 2/2/14.
 */
public class Tuple {
    Object[] array;
    Relation rel;

    Tuple(Relation rel) {
        this.rel = rel;
        int n = rel.getNumberOfFieldsInTuple();
        array = new Object[n];
    }

    Object getField(int index) {
        return array[index];
    }

    Object setField(int index, Object value) {
        array[index] = value;
        return array[index];
    }

    public void create(){
        System.out.println("Hardcoded in MyDbEnv. Hence table is created already");
    }

    public void insert(){

    }
}
