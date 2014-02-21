package com.bdb;

/**
 * Created by ankit on 2/3/14.
 */
public class DbString extends DbValue {
    String value;


    public DbString(String value, String columnName){
        this.value = value;
        this.columnName = columnName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbString)) return false;

        DbString dbString = (DbString) o;

        if (value != null ? !value.equals(dbString.value) : dbString.value != null) return false;

        return true;
    }

    public boolean operator(Object o, OpType opType){
        if(!(o instanceof DbString)) return false;

        DbString dbInt = (DbString) o;

        switch (opType){
            case EQUALS:
                return this.equals(o);
            case NOT_EQUALS:
                return (!this.equals(o));
            case GTE:
                return (this.value.compareTo(((DbString) o).value) >= 0);
            case GT:
                return (this.value.compareTo(((DbString) o).value) > 0);
            case LTE:
                return (this.value.compareTo(((DbString) o).value) <= 0);
            case LT:
                return (this.value.compareTo(((DbString) o).value) < 0);
            default:
                return false;
        }
    }


    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
