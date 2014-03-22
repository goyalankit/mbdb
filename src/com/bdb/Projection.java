package com.bdb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankit on 2/22/14.
 */
public class Projection {
    private Relation relation;
    private Column column;
    private boolean projectAll;

    public Projection(Relation relation, Column column) {
        this.relation = relation;
        this.column = column;
    }

    public static Projection create(List<Relation> relations, String cname) throws MyDatabaseException{

        Projection p = new Projection(null, null);

        String colName, relName = null;

        boolean isAmbigous = false;

        if(cname!=null && cname.contains(".")){
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
            throw new MyDatabaseException("Column to be projected not present in the relations");
        else if(isAmbigous)
            throw new MyDatabaseException("Column to be projected is/are ambiguous");

        return p;
    }

    public static List<Tuple> project(List<Tuple> finalTuples, List<Projection> projectionList){

        List<Tuple> projectedTuples = new ArrayList<Tuple>();

        if(null == finalTuples || finalTuples.size() == 0) {
            System.out.print("0 row(s) selected ");
            return projectedTuples;
        }


        DbValue[] dbValuesList;

        Tuple t = finalTuples.get(0);
        dbValuesList = t.getDbValues();

        List<Integer> showIndices = new ArrayList<Integer>();

        if(projectionList.size() > 0)
        {
            for(Projection p : projectionList)
            {
                for (int i = 0; i < dbValuesList.length; i++)
                {
                    if(dbValuesList[i].columnName.equals(p.column.getName())
                            || dbValuesList[i].columnName.equals(p.relation.getName() + "." + p.column.getName()))
                    {
                        showIndices.add(i);
                    } else
                    {
                        continue;
                    }
                }
            }
        }else
        {
            for (int i = 0; i < dbValuesList.length; i++){ showIndices.add(i); }
        }



        for (int i : showIndices){
            DbValue dbValue = finalTuples.get(0).getDbValues()[i];
            if(dbValue instanceof DbInt)
                System.out.print(((DbInt)dbValue).columnName + ",");
            else
                System.out.print(((DbString)dbValue).columnName + ",");
        }
        System.out.println("\n"); //add a new line.

        for(Tuple t1 : finalTuples){
            for (int i : showIndices){
                DbValue dbValue = t1.getDbValues()[i];
                if(dbValue instanceof DbInt)
                    System.out.print(((DbInt)dbValue).value + ",");
                else
                    System.out.print(((DbString)dbValue).value + ",");
            }
            System.out.print("\n");
        }

        System.out.println();
        System.out.print(finalTuples.size()+" row(s) selected.");
        return projectedTuples;
    }
}
