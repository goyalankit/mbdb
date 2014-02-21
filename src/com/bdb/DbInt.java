package com.bdb;

/**
 * Created by ankit on 2/3/14.
 */
public class DbInt extends DbValue {
    Integer value;

    private DbInt(Integer value) {
        this.value = value;
    }

    public DbInt(Integer value, String columnName){
        this(value);
        this.columnName = columnName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbInt)) return false;

        DbInt dbInt = (DbInt) o;

        if (!value.equals(dbInt.value)) return false;

        return true;
    }

    public boolean operator(Object o, OpType opType){
        if(!(o instanceof DbInt)) return false;

        DbInt dbInt = (DbInt) o;

        switch (opType){
            case EQUALS:
                return this.equals(o);
            case NOT_EQUALS:
                return (!this.equals(o));
            case GT:
                return (this.value > dbInt.value);
            case GTE:
                return (this.value >= dbInt.value);
            case LT:
                return (this.value < dbInt.value);
            case LTE:
                return (this.value <= dbInt.value);
            default:
                return true;
        }
    }




    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public Integer getValue() {
        return value;
    }
}
