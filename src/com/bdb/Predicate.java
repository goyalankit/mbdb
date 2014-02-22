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

        for(Relation rel : relations){
            if(null != lhsRelation && rel.getName().equals(lhsRelation)){
                this.lhsRelation = rel;
                this.lhsColumn = rel.getColumn(lhsColumn);
            }

            if(null != rhsRelation && rel.getName().equals(rhsRelation)){
                this.rhsRelation = rel;
                this.rhsColumn = rel.getColumn(rhsColumn);
            }
        }

        if(null == this.rhsColumn || null == this.lhsColumn)
            throw new MyDatabaseException("Join predicate is malformed.");

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
        if(t1 instanceof JoinedTuple){
            //TODO: search the column by appending relation name
             dbValue1 = ((JoinedTuple) t1).getColumnValue(lhsRelation.getName() + "." + lhsColumn.getName());
        }else{
            dbValue1 = t1.getDbValues()[lhsColumn.getIndex()];
        }

        if(t2 instanceof JoinedTuple){
            dbValue2 = ((JoinedTuple) t2).getColumnValue(rhsRelation.getName() + "." + rhsColumn.getName());
        }else{
            dbValue2 = t2.getDbValues()[rhsColumn.getIndex()];
        }

        JoinedTuple joinedTuple = null;

        if(dbValue1.equals(dbValue2))
            joinedTuple = new JoinedTuple(t1, t2, lhsColumn, rhsColumn);

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
