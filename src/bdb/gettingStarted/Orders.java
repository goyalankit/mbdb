package bdb.gettingStarted;

/**
 * Created by ankit on 2/2/14.
 */
public class Orders {

    private int ono;
    private int cno;
    private int eno;
    private String received;
    private String shipped;

    public int getOno() {
        return ono;
    }

    public void setOno(int ono) {
        this.ono = ono;
    }

    public int getCno() {
        return cno;
    }

    public void setCno(int cno) {
        this.cno = cno;
    }

    public int getEno() {
        return eno;
    }

    public void setEno(int eno) {
        this.eno = eno;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getShipped() {
        return shipped;
    }

    public void setShipped(String shipped) {
        this.shipped = shipped;
    }
}
