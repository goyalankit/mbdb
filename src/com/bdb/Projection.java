package com.bdb;

import java.util.List;

/**
 * Created by ankit on 2/22/14.
 */
public class Projection {
    private Relation relation;
    private Column column;

    public Projection(Relation relation, Column column) {
        this.relation = relation;
        this.column = column;
    }

    public static Projection create(List<Relation> relations, String cname) throws MyDatabaseException{

        Projection p = new Projection(null, null);

        String colName, relName = null;

        boolean isAmbigous = false;

        if(cname!=null && cname.contains("\\.")){
            String[] s = cname.split("\\.");
            relName = s[0];
            colName = s[1];
        }else{
            colName = cname;
        }

        for(Relation rel : relations){
            if(null != relName && rel.getName().equals(relName))
            {
                if(null != rel.getColumn(colName))
                {
                    p.column = rel.getColumn(colName);
                    p.relation = rel;
                    break;
                }
            }
            else if(null == relName)
            {
                if(null != rel.getColumn(colName))
                {
                    if(null != p.column) isAmbigous = true;
                    p.column = rel.getColumn(colName);
                    p.relation =rel;
                }
            }
        }

        if(p.column == null)
            throw new MyDatabaseException("Column to be projced not present in the relations");
        else if(isAmbigous)
            throw new MyDatabaseException("Column to be projced is/are ambiguous");

        return p;
    }
}
