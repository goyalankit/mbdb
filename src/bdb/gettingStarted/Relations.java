package bdb.gettingStarted;

/**
 * Created by ankit on 2/2/14.
 */
public enum Relations {

    EMP("The employee table"),
    DEPT("The Department table"),
    PROF("The professor table");



    private String description;

    Relations(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

}
