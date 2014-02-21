package com.bdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ankit on 2/20/14.
 */
public class UpdateQueryProcessor {
    private Relation relation;
    private List<Predicate> predicates;
    private Map<String, DbValue> updates;
    private Map<Column, DbValue> columnUpdateMap;

    public UpdateQueryProcessor(Relation relation, List<Predicate> predicates, Map<String, DbValue> updates) {
        this.relation = relation;
        this.predicates = predicates;
        this.updates = updates;
        this.columnUpdateMap = new HashMap<Column, DbValue>();
    }

    public void build() throws MyDatabaseException{
        for(String field: updates.keySet())
        {
            Column cl = relation.getColumn(field);

            if(cl == null) throw new MyDatabaseException("Column to be updated not present");

            columnUpdateMap.put(cl, updates.get(field));
        }
    }

    public void process(){
        DbClient dbClient = new DbClient("mydbenv", relation.getName());
        dbClient.updateTuplesWithPredicate(predicates, columnUpdateMap);
    }
}
