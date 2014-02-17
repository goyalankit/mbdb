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

    public Predicate(PredType type, List<Relation> relations, String lhsRelation, String lhsColumn, String rhsRelation, String rhsColumn, OpType op) {

        if(type != PredType.JOIN) throw new IllegalArgumentException("Invalid type of predicate");

        for(Relation rel : relations){
            if(null != lhsRelation && rel.getName().equals(lhsRelation)){
                this.lhsRelation = rel;
                this.lhsColumn = rel.getColumn(lhsColumn);
            }else if(null != rhsRelation && rel.getName().equals(rhsRelation)){
                this.rhsRelation = rel;
                this.rhsColumn = rel.getColumn(rhsColumn);
            }
        }

        this.type = type;
        this.operator = op;
    }

    public Predicate(PredType type, List<Relation> relations, String lhsRelation, String lhsColumn, String rhsValue, OpType op) {

        if(type != PredType.SIMPLE) throw new IllegalArgumentException("Invalid type of predicate");

        this.type = type;

        for(Relation rel : relations){
            Column cl = null;
            if(null != lhsRelation && rel.getName().equals(lhsRelation)){
                this.lhsRelation = rel;
                this.lhsColumn = rel.getColumn(lhsColumn);
            }else if((cl = rel.getColumn(lhsColumn))!= null){
                this.lhsRelation = rel;
                this.lhsColumn = cl;
            }
        }

        if(this.lhsColumn.getType().equals("int"))
            this.rhsValue = new DbInt(Integer.parseInt(rhsValue));
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
