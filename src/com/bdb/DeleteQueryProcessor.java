package com.bdb;

import java.util.List;

/**
 * Created by ankit on 2/21/14.
 */
public class DeleteQueryProcessor {
    private Relation relation;
    private List<Predicate> predicates;

    public DeleteQueryProcessor(Relation relation, List<Predicate> predicates) {
        this.relation = relation;
        this.predicates = predicates;
    }

    public void build(){
        //nothing to build. don't worry it will be taken care of by compiler.
    }

    public void process(){
        DbClient dbClient = new DbClient("mydbenv", relation.getName());
        dbClient.deleteTuplesWithPredicate(predicates);
    }
}
