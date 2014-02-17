package com.bdb;

import java.util.*;

/**
 * Created by ankit on 2/16/14.
 */
public class SelectQueryProcessor {
    private List<Relation> relations;

    private List<Predicate> predicates;
    private HashMap<Relation, List<Predicate>> localPredicates;
    private QueryType type;

    public SelectQueryProcessor(QueryType type, List<Relation> relations, List<Predicate> predicates) {
        this.type = type;
        this.relations = relations;
        this.predicates = predicates;
        this.localPredicates = new HashMap<Relation, List<Predicate>>();
    }

    public void build(){
        /* Get the relation objects */

        for(Predicate p : predicates)
        {
            if(p.getType().equals(PredType.SIMPLE)){
                Relation r = p.getLhsRelation();
                if(null != localPredicates.get(r))
                {
                    localPredicates.get(r).add(p);
                }
                else
                {
                    localPredicates.put(r, new ArrayList<Predicate>());
                    localPredicates.get(r).add(p);
                }
            }
        }
    }

    public void process(){
        HashMap<Relation, Set<Tuple>> tuples = new HashMap<Relation, Set<Tuple>>();

        for (Relation rel : localPredicates.keySet()){
            DbClient dbClient = new DbClient("mydbenv", rel.getName());
            tuples.put(rel, dbClient.getTuplesWithPredicate(localPredicates.get(rel)));
            printResults(tuples.get(rel));
        }
    }

    public boolean applyLocal(Relation r, Predicate p, Tuple t){

        return false;
    }

    public void printResults(Set<Tuple> tuples){
        for(Tuple t : tuples)
            System.out.println(t);
    }

}

