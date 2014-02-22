package com.bdb;

import java.util.*;

/**
 * Created by ankit on 2/16/14.
 */
public class SelectQueryProcessor {
    private List<Relation> relations;

    private List<Predicate> predicates;
    private HashMap<Relation, List<Predicate>> localPredicates;
    private List<Predicate> joinPredicates;
    private QueryType type;

    private HashMap<Relation, Boolean> relationJoined;
    private List<Tuple> joinedTuples;

    public SelectQueryProcessor(QueryType type, List<Relation> relations, List<Predicate> predicates) {
        this.type = type;
        this.relations = relations;
        this.predicates = predicates;
        this.localPredicates = new HashMap<Relation, List<Predicate>>();
        this.joinPredicates = new ArrayList<Predicate>();
    }

    public void build() {
        /* Get the relation objects */
        for (Predicate p : predicates) {
            if (p.getType().equals(PredType.SIMPLE)) {
                Relation r = p.getLhsRelation();
                //add to the list of local predicates.
                if (null != localPredicates.get(r)) {
                    localPredicates.get(r).add(p);
                } else {
                    localPredicates.put(r, new ArrayList<Predicate>());
                    localPredicates.get(r).add(p);
                }
            } else if (p.getType().equals(PredType.JOIN)) {
                //add to the list of join predicates.
                joinPredicates.add(p);
            }
        }
    }

    /**
     * pass the proper predicates.
     */
    public List<Tuple> process() {
        HashMap<Relation, List<Tuple>> tuples = new HashMap<Relation, List<Tuple>>();

        /* First apply the local predicates */

        for (Relation rel : relations) {
            DbClient dbClient = new DbClient("mydbenv", rel.getName());

            /*
                Get the tuples for ALL the relations.
                Return all the tuples based on predicates. otherwise all tuples
                if no predicate is present.
            */
            tuples.put(rel, dbClient.getTuplesWithPredicate(localPredicates.get(rel)));
        }

        /* call to apply join predicates */
        if (joinPredicates.size() != 0)
            join(tuples);

        cross(tuples);

        //printJoinTuples(joinedTuples);
        return joinedTuples;
    }

    /**
     * @param relationTuples perform join.
     */
    public void join(HashMap<Relation, List<Tuple>> relationTuples) {
        //keep track of tuples from a join.
        relationJoined = new HashMap<Relation, Boolean>();
        joinedTuples = new ArrayList<Tuple>();

        for (Predicate p : joinPredicates) {

            /* get the relations in join predicate */
            Relation r1 = p.getLhsRelation();
            Relation r2 = p.getRhsRelation();

            List<Tuple> tuples1 = relationJoined.containsKey(r1) ? joinedTuples : relationTuples.get(r1);
            List<Tuple> tuples2 = relationJoined.containsKey(r2) ? joinedTuples : relationTuples.get(r2);

            List<Tuple> filteredTuples = new ArrayList<Tuple>();

            for (Tuple t1 : tuples1) {
                for (Tuple t2 : tuples2) {
                    Tuple ntuple = p.applyJoin(t1, t2);
                    if (null != ntuple) {
                        filteredTuples.add(ntuple);
                    }
                }
            }

            relationJoined.put(r1, true);
            relationJoined.put(r2, true);

            joinedTuples = filteredTuples;
        }
    }

    public void cross(HashMap<Relation, List<Tuple>> relationTuples) {

        List<Tuple> crossedTuplesResult = new ArrayList<Tuple>();

        List<Tuple> tupleSet1;

        //if some tuples have been joined
        if (joinedTuples != null && joinedTuples.size() > 0)
            tupleSet1 = joinedTuples; //use joined tuple.
        else
            tupleSet1 = relationTuples.get(relations.get(0)); //use tuples from first relation.

        //tupleSet1 should never be empty.

        for (int i = 1; i < relations.size(); i++) {
            Relation rel = relations.get(i);

            //Relation in the list that has not been joined.
            if ((relationJoined == null) || !relationJoined.containsKey(rel)) {

                if (i > 2 && crossedTuplesResult.size() > 0)
                    tupleSet1 = crossedTuplesResult; //TODO: may be there are no tuples

                for (Tuple joinedTuple : tupleSet1) {
                    for (Tuple t2 : relationTuples.get(rel)) {
                        crossedTuplesResult.add(new JoinedTuple(joinedTuple, t2, null, null));
                    }
                }
            }
        }

        /* If no tuple was added above then don't change the joined tuple */
        joinedTuples = crossedTuplesResult.size() > 0 ? crossedTuplesResult : tupleSet1;
    }

}

