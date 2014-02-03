package bdb.gettingStarted;

import java.lang.reflect.Field;

/**
 * Created by ankit on 2/1/14.
 */
public class Dept extends Relation{

    private int deptno;
    private String dept_name;
    private String chairman;

    public Dept(){}

    public Dept(int deptno, String dept_name, String chairman){
        this.deptno = deptno;
        this.dept_name = dept_name;
        this.chairman = chairman;
    }

    public void createDb(){
        /* Define department relation */

        Field[] fields =  Dept.class.getDeclaredFields();
        this.create(fields);

        System.out.println("INFO:" + this);
    }

    public void insert(){
        DatabasePut db = new DatabasePut("myDbEnv");
        db.insert(this);
    }

    public int getDeptno() { return deptno; }

    public void setDeptno(int deptno) { this.deptno = deptno; }

    public String getDept_name() { return dept_name; }

    public void setDept_name(String dept_name) { this.dept_name = dept_name; }

    public String getChairman() { return chairman; }

    public void setChairman(String chairman) { this.chairman = chairman; }

}