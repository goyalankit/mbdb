package com.bdb;

/**
 * Created by ankit on 2/3/14.
 */
public class Column {
    String type;
    String name;
    int index;

    public Column(String type, String name, int index) {
        this.type = type;
        this.name = name;
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Column)) return false;

        Column column = (Column) o;

        if (index != column.index) return false;
        if (!name.equals(column.name)) return false;
        if (!type.equals(column.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + index;
        return result;
    }
}
