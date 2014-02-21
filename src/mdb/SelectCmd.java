// Automatically generated code.  Edit at your own risk!
// Generated by bali2jak v2002.09.03.

package mdb;

import com.bdb.*;

import java.util.ArrayList;
import java.util.List;

public class SelectCmd extends Select {

    final public static int ARG_LENGTH = 3 ;
    final public static int TOK_LENGTH = 3 ;

    public void execute () {
        super.execute();
        if(getWherePred() == null)
            selectAllFromARelation();
        else
            parseSelectQuery();

    }

    public void parseSelectQuery(){
        AstCursor c = new AstCursor();
        List<Relation> relations = new ArrayList<Relation>();
        List<Predicate> predicates = new ArrayList<Predicate>();

        for (c.FirstElement(getRel_list()); c.MoreElement(); c.NextElement() ) {
            AstNode node = c.node ;
            relations.add(Relation.getRelation(node.toString().trim()));

        }


        try {
            AstNode m = getWherePred();
            Predicate p = null;
            for (c.FirstElement(m.arg[0]); c.MoreElement(); c.NextElement() ) {
                if(c.node instanceof SimpleClause)
                {
                    p = getSimplePredicate(c, relations);
                }
                else if(c.node instanceof JoinClause)
                {
                    p = getJoinPredicate(c, relations);

                }else
                {
                    System.err.println("Predicate not allowed");
                }

                if(null != p)
                    predicates.add(p);

                System.out.println();
            }
        } catch (MyDatabaseException e) {
            e.printStackTrace();
            return;
        }

        SelectQueryProcessor sqp = new SelectQueryProcessor(QueryType.SELECT, relations, predicates);
        sqp.build();
        sqp.process();
    }

    private Predicate getSimplePredicate(AstCursor c, List<Relation> relations) throws MyDatabaseException{
        Predicate p;
        SimpleClause simpleClause = (SimpleClause)c.node;
        String lhs = simpleClause.getField_spec().toString();
        String []s;
        String lhs_relation = null;
        String lhs_column = null;

        if(lhs.contains(".")){
            s = lhs.split("\\.");
            lhs_relation = s[0].trim();
            lhs_column = s[1].trim();
        }else{
            lhs_relation = null;
            lhs_column = lhs.trim();
        }

        p = new Predicate(PredType.SIMPLE, relations, lhs_relation, lhs_column,
                                        simpleClause.getLiteral().toString().trim(),
                                        OpType.getOp(simpleClause.getRel().toString().trim()));
        return p;
    }

    private Predicate getJoinPredicate(AstCursor c, List<Relation> relations) throws MyDatabaseException {
        Predicate p;
        JoinClause joinClause = (JoinClause)c.node;
        String lhs_column, lhs_relation, rhs_column, rhs_relation;
        lhs_column = lhs_relation = rhs_relation = rhs_column = null;

        String lhs = joinClause.arg[0].toString();
        String[] s;
        if(lhs.contains(".")){
            s = lhs.split("\\.");
            lhs_relation = s[0].trim();
            lhs_column = s[1].trim();
        }else{
            lhs_column = lhs.trim();
        }

        String rhs = joinClause.arg[1].toString();
        s = null;
        if(lhs.contains(".")){
            s = rhs.split("\\.");
            rhs_relation = s[0].trim();
            rhs_column = s[1].trim();
        }else{
            rhs_column = lhs.trim();
        }


        p = new Predicate(PredType.JOIN, relations, lhs_relation, lhs_column,
                                        rhs_relation, rhs_column,
                                        OpType.getOp(joinClause.getEQ().getTokenName().trim()));
        return p;
    }

    public void selectAllFromARelation(){
        System.out.println("\nRelation Name : " + getRel_list().arg[0].arg[0].toString().trim());
        System.out.println("--------------------------------------------------");
        Relation relation = Relation.getRelation(getRel_list().arg[0].arg[0].toString().trim());
        if(null != relation){
            String str = relation.getName() +" (";
            for(Column cl : relation.getColumns())
                str += cl.getName() + ",";
            str = str.substring(0, str.lastIndexOf(",")) + ")";
            System.out.println(str);
            relation.selectStar();
        }
        else{
            System.err.println("Relation doesn't exists");
        }
    }

    public AstToken getFROM () {
        
        return (AstToken) tok [1] ;
    }

    public Proj_list getProj_list () {
        
        return (Proj_list) arg [0] ;
    }

    public Rel_list getRel_list () {
        
        return (Rel_list) arg [1] ;
    }

    public AstToken getSELECT () {
        
        return (AstToken) tok [0] ;
    }

    public AstToken getSEMI () {
        
        return (AstToken) tok [2] ;
    }

    public WherePred getWherePred () {
        
        AstNode node = arg[2].arg [0] ;
        return (node != null) ? (WherePred) node : null ;
    }

    public boolean[] printorder () {
        
        return new boolean[] {true, false, true, false, false, true} ;
    }

    public SelectCmd setParms
    (AstToken tok0, Proj_list arg0, AstToken tok1, Rel_list arg1, AstOptNode arg2, AstToken tok2)
    {
        
        arg = new AstNode [ARG_LENGTH] ;
        tok = new AstTokenInterface [TOK_LENGTH] ;
        
        tok [0] = tok0 ;            /* SELECT */
        arg [0] = arg0 ;            /* Proj_list */
        tok [1] = tok1 ;            /* FROM */
        arg [1] = arg1 ;            /* Rel_list */
        arg [2] = arg2 ;            /* [WherePred] */
        tok [2] = tok2 ;            /* SEMI */
        
        InitChildren () ;
        return (SelectCmd) this ;
    }

}
