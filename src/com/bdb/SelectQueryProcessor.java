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

    private static Integer HASH_BLOCK_SIZE = 1000; // blocks of 1000 records.

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

        //TODO: decide policy on when to use hash join.
        boolean useHashJoin = true;

        for (Predicate p : joinPredicates) {

            /* get the relations in join predicate */
            Relation r1 = p.getLhsRelation();
            Relation r2 = p.getRhsRelation();

            boolean isTuple1Joined = relationJoined.containsKey(r1);
            boolean isTuple2Joined = relationJoined.containsKey(r2);

            List<Tuple> tuples1 = isTuple1Joined ? joinedTuples : relationTuples.get(r1);
            List<Tuple> tuples2 = isTuple2Joined ? joinedTuples : relationTuples.get(r2);

            List<Tuple> filteredTuples = new ArrayList<Tuple>();


            //If both the relations have already been joined. We need to filter out the results from the
            //joined relation. It's similar to self-join.
            boolean both_joined = (isTuple1Joined && isTuple2Joined);

            //self join.
            if(r1.equals(r2) || both_joined ){
                for (Tuple t1 : tuples1) {
                    Tuple ntuple = p.applyJoin(t1, t1);
                    if(null!=ntuple)
                        filteredTuples.add(ntuple);
                }
            }else{

                if(useHashJoin)
                    filteredTuples = hashJoin(tuples1, tuples2, isTuple1Joined, isTuple2Joined, p);
                else
                    filteredTuples = naturalJoin(tuples1, tuples2, p);
            }

            relationJoined.put(r1, true);
            relationJoined.put(r2, true);

            joinedTuples = filteredTuples;
        }
    }

    public List<Tuple> naturalJoin(List<Tuple> tuples1, List<Tuple> tuples2, Predicate p){

        List<Tuple> filteredTuples = new ArrayList<Tuple>();

        for (Tuple t1 : tuples1) {
            for (Tuple t2 : tuples2) {
                Tuple ntuple = p.applyJoin(t1, t2);
                if (null != ntuple) {
                    filteredTuples.add(ntuple);
                }
            }
        }

        return filteredTuples;
    }

    public List<Tuple> hashJoin(List<Tuple> tuples1, List<Tuple> tuples2, boolean isTuple1Joined, boolean isTuple2Joined,  Predicate p){

        boolean outerIsLeft = tuples1.size() < tuples2.size();

        List<Tuple> innerRelationTuples;
        HashMap<DbValue, Set<Integer>> tuple1HashMap;

        // create hash from tuple 1.
        if(outerIsLeft){
            tuple1HashMap = createTableHash(tuples1, p, true, isTuple1Joined);
            innerRelationTuples = tuples2;
            DbClient.LOGGER.info("Using hash join with "+p.getLhsRelation().getName()+" as outer relation.");
        }
        else{
            tuple1HashMap = createTableHash(tuples2, p, false, isTuple2Joined);
            innerRelationTuples = tuples1;
            DbClient.LOGGER.info("Using hash join with "+p.getRhsRelation().getName()+" as outer relation.");
        }

        List<Tuple> filteredTuples = new ArrayList<Tuple>();

        for (Tuple t2 : innerRelationTuples) {
            DbValue dbValue2;
            if(outerIsLeft)
                dbValue2 = getInnertTableDbValueForHashJoin(t2, p, outerIsLeft, isTuple2Joined);
            else
                dbValue2 = getInnertTableDbValueForHashJoin(t2, p, outerIsLeft, isTuple1Joined);

            if(tuple1HashMap.containsKey(dbValue2)){
                for (Integer i : tuple1HashMap.get(dbValue2)) {

                    if (outerIsLeft)
                        filteredTuples.add(new JoinedTuple(tuples1.get(i), t2, p.getLhsColumn(), p.getRhsColumn()));
                    else
                        filteredTuples.add(new JoinedTuple(t2, tuples2.get(i), p.getLhsColumn(), p.getRhsColumn()));
                }
            }
        }

        return filteredTuples;
    }

    private DbValue getInnertTableDbValueForHashJoin(Tuple t2, Predicate p, boolean outerIsLeft, boolean isTupleJoined){

        DbValue dbValue2;

        if(isTupleJoined)
            if(outerIsLeft)
                dbValue2 = ((JoinedTuple) t2).getColumnValue(p.getRhsRelation().getName() + "." + p.getRhsColumn().getName());
            else
                dbValue2 = ((JoinedTuple) t2).getColumnValue(p.getLhsRelation().getName() + "." + p.getLhsColumn().getName());

        else
            if(outerIsLeft)
                dbValue2 = t2.getDbValues()[p.getRhsColumn().index];
            else
                dbValue2 = t2.getDbValues()[p.getLhsColumn().index];


        return dbValue2;
    }

    private HashMap<DbValue, Set<Integer>> createTableHash(List<Tuple> tuples, Predicate p, boolean outerIsLeft, boolean isTupleJoined){
        HashMap<DbValue, Set<Integer>> tuple1HashMap = new HashMap<DbValue, Set<Integer>>();

        for (int i = 0; i < tuples.size(); i++) {
            DbValue dbValue1;
            if(isTupleJoined)
                if(outerIsLeft)
                    dbValue1 = ((JoinedTuple) tuples.get(i)).getColumnValue(p.getLhsRelation().getName() + "." + p.getLhsColumn().getName());
                else
                    dbValue1 = ((JoinedTuple) tuples.get(i)).getColumnValue(p.getRhsRelation().getName() + "." + p.getRhsColumn().getName());
            else
                if(outerIsLeft)
                    dbValue1 = tuples.get(i).getDbValues()[p.getLhsColumn().index];
                else
                    dbValue1 = tuples.get(i).getDbValues()[p.getRhsColumn().index];

            if(tuple1HashMap.containsKey(dbValue1))
                tuple1HashMap.get(dbValue1).add(i);
            else{
                Set<Integer> tupleValues = new HashSet<Integer>();
                tupleValues.add(i);
                tuple1HashMap.put(dbValue1, tupleValues);
            }
        }

        return tuple1HashMap;

    }


    public void cross(HashMap<Relation, List<Tuple>> relationTuples) {

        if(relationJoined !=null && relationJoined.size() == relations.size())
            return;

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

