package com.bdb;

import java.util.List;

/**
 * Created by ankit on 2/16/14.
 */
public class Predicate {
    PredType type;

    /*For all type of predicates*/
    private Relation lhsRelation;
    private Column lhsColumn;

    /* In case of a join predicate */
    private Relation rhsRelation;
    private Column rhsColumn;

    /* In case of a local predicate */
    private DbValue rhsValue;

    private OpType operator;

    private List<Relation> relations;

    public Predicate(PredType type, List<Relation> relations,
                     String lhsRelation, String lhsColumn, String rhsRelation, String rhsColumn, OpType op) throws MyDatabaseException{

        if(type != PredType.JOIN) throw new IllegalArgumentException("Invalid type of predicate");

        for(Relation rel : relations)
        {
            if(null != lhsRelation && rel.getName().equals(lhsRelation))
            {
                this.lhsRelation = rel;
                this.lhsColumn = rel.getColumn(lhsColumn);
            }else if((null == lhsRelation) && (null != rel.getColumn(lhsColumn)))
            {
                this.lhsRelation = rel;
                this.lhsColumn = rel.getColumn(lhsColumn);
            }

            if(null != rhsRelation && rel.getName().equals(rhsRelation)){
                this.rhsRelation = rel;
                this.rhsColumn = rel.getColumn(rhsColumn);
            }else if((null == rhsRelation) && (null != rel.getColumn(rhsColumn)))
            {
                this.rhsRelation = rel;
                this.rhsColumn = rel.getColumn(rhsColumn);
            }
        }

        if(null == this.rhsColumn || null == this.lhsColumn)
            throw new MyDatabaseException("Join predicate is malformed. Columns not present in the mentioned relations.");

        this.type = type;
        this.operator = op;
    }

    public Predicate(PredType type, List<Relation> relations, String lhsRelation,
                     String lhsColumn, String rhsValue, OpType op) throws MyDatabaseException{

        if(type != PredType.SIMPLE) throw new IllegalArgumentException("Invalid type of predicate");

        this.type = type;

        boolean alreadyNamed = false;

        for(Relation rel : relations){
            Column cl = null;
            if(null != lhsRelation && rel.getName().equals(lhsRelation)){
                this.lhsRelation = rel;
                this.lhsColumn = rel.getColumn(lhsColumn);
                break;
            }else if((cl = rel.getColumn(lhsColumn))!= null){
                if(alreadyNamed)
                    throw new MyDatabaseException("Ambigous Column Definition : " + lhsColumn);
                this.lhsRelation = rel;
                this.lhsColumn = cl;
                alreadyNamed = true;
            }
        }

        if(null == this.lhsColumn){
            throw new MyDatabaseException("Column not present in the given relation");
            //System.err.println("Column not present in the given relation");
        }

        if(this.lhsColumn.getType().equals("int"))
            this.rhsValue = new DbInt(Integer.parseInt(rhsValue), this.lhsColumn.getName());
        else if(this.lhsColumn.getType().equals("str"))
            this.rhsValue = new DbString(rhsValue, this.lhsColumn.getName());

        this.operator = op;
    }

    public boolean applyLocal(Tuple t){

        if(t.getRelationName().equals(lhsRelation.getName())){
            DbValue dbValue = t.getDbValues()[lhsColumn.getIndex()];
            if(dbValue instanceof DbInt)
            {
                return ((DbInt)dbValue).operator(rhsValue, operator);
            }
            else if(dbValue instanceof DbString)
            {
                return ((DbString)dbValue).operator(rhsValue, operator);
            }
        }
        return false;
    }

    public Tuple applyJoin(Tuple t1, Tuple t2){
        DbValue dbValue1, dbValue2;

        String relationName1, relationName2;

        if(t1 instanceof JoinedTuple){
            dbValue1 = ((JoinedTuple) t1).getColumnValue(lhsRelation.getName() + "." + lhsColumn.getName());
            relationName1 = lhsRelation.getName();

        }else{
            dbValue1 = t1.getDbValues()[lhsColumn.getIndex()];
            relationName1 = t1.getRelationName();
        }

        if(t2 instanceof JoinedTuple){
            dbValue2 = ((JoinedTuple) t2).getColumnValue(rhsRelation.getName() + "." + rhsColumn.getName());
            relationName2 = rhsRelation.getName();
        }else{
            dbValue2 = t2.getDbValues()[rhsColumn.getIndex()];
            relationName2 = t2.getRelationName();
        }

        JoinedTuple joinedTuple = null;

        if(relationName1.equals(relationName2)){
            if(t1 instanceof JoinedTuple)
                dbValue1 = ((JoinedTuple) t1).getColumnValue(lhsRelation.getName() + "." + lhsColumn.getName());
            else
                dbValue1 = t1.getDbValues()[lhsColumn.getIndex()];

            if(t2 instanceof JoinedTuple)
                dbValue2 = ((JoinedTuple) t1).getColumnValue(rhsRelation.getName() + "." + rhsColumn.getName());
            else
                dbValue2 = t1.getDbValues()[rhsColumn.getIndex()];

            if(dbValue1.equals(dbValue2)){
                return t1;
            }
        }


        if(dbValue1.equals(dbValue2)){
            joinedTuple = new JoinedTuple(t1, t2, lhsColumn, rhsColumn);
        }

        return joinedTuple;
    }

    public PredType getType() {
        return type;
    }

    public Relation getLhsRelation() {
        return lhsRelation;
    }

    public Relation getRhsRelation() {
        return rhsRelation;
    }

    public OpType getOperator() {
        return operator;
    }
}
