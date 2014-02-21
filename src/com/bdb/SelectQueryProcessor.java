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
    private Set<Tuple> joinedTuples;

    public SelectQueryProcessor(QueryType type, List<Relation> relations, List<Predicate> predicates) {
        this.type = type;
        this.relations = relations;
        this.predicates = predicates;
        this.localPredicates = new HashMap<Relation, List<Predicate>>();
        this.joinPredicates = new ArrayList<Predicate>();
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
            }else if(p.getType().equals(PredType.JOIN)){
                joinPredicates.add(p);
            }
        }
    }
    /**
    *
    * pass the proper predicates.
    *
    * */
    public void process(){
        HashMap<Relation, Set<Tuple>> tuples = new HashMap<Relation, Set<Tuple>>();

        /* First apply the local predicates */

        for (Relation rel : relations){
            DbClient dbClient = new DbClient("mydbenv", rel.getName());
            tuples.put(rel, dbClient.getTuplesWithPredicate(localPredicates.get(rel)));

            //TODO this condition should be handled in cross product
            if(joinPredicates.size()==0)
                printResults(tuples.get(rel));

        }

        /* call to apply join predicates */
        if(joinPredicates.size()!=0)
                join(tuples);
        //cross();
    }

    /**
     *
     * @param relationTuples
     * perform join.
     */
    public void join(HashMap<Relation, Set<Tuple>> relationTuples){
        //keep track of tuples from a join.
        relationJoined = new HashMap<Relation, Boolean>();
        joinedTuples = new HashSet<Tuple>();

        for(Predicate p : joinPredicates){

            /* get the relations in join predicate */
            Relation r1 = p.getLhsRelation();
            Relation r2 = p.getRhsRelation();

            Set<Tuple> tuples1 = relationJoined.containsKey(r1) ? joinedTuples : relationTuples.get(r1);
            Set<Tuple> tuples2 = relationJoined.containsKey(r2) ? joinedTuples : relationTuples.get(r2);

            Set<Tuple> filteredTuples = new HashSet<Tuple>();

            for(Tuple t1 : tuples1)
            {
                for(Tuple t2 : tuples2)
                {
                    Tuple ntuple = p.applyJoin(t1, t2);
                    if(null != ntuple)
                    {
                        filteredTuples.add(ntuple);

                    }
                }
            }
            if(joinedTuples.size() != filteredTuples.size()){
                relationJoined.put(r1, true);
                relationJoined.put(r2, false);
            }

            joinedTuples = filteredTuples;
        }

        printJoinTuples(joinedTuples);
    }

    //TODO: Take the cross product for the tables that didn't participate in join.
    public void cross(){
        for(Relation rel : relations){
           if(!relationJoined.containsKey(rel)){
               for(Tuple joinedTuple : joinedTuples){
               //joinedTuple = new JoinedTuple(joinedTuple, t2, lhsColumn, rhsColumn);
               }
           }
        }

    }

    public void printJoinTuples(Set<Tuple> tuples){

        JoinedTuple jt = null;

        for(Tuple t : tuples){
            if(null == jt) ((JoinedTuple) t).printHeader();
            jt = (JoinedTuple) t;
            System.out.println(jt+"\n");
        }

    }


    public void printResults(Set<Tuple> tuples){
        for(Tuple t : tuples)
            System.out.println(t);
    }

}

