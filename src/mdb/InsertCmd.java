// Automatically generated code.  Edit at your own risk!
// Generated by bali2jak v2002.09.03.

package mdb;


import com.bdb.*;

public class InsertCmd extends Insert {

    final public static int ARG_LENGTH = 2 ;
    final public static int TOK_LENGTH = 6 ;

    public void execute () {
        super.execute();

        Relation relation = Relation.getRelation(getRel_name().toString().trim());
        Tuple tuple = null;
        //System.out.println(relation);
        if(null == relation)
            System.err.println("Table doesn't exist. Create the table first");
        else{
            tuple = createTuple(relation);
        }

        if(null != tuple)
            relation.insert(tuple);
    }

    public Tuple createTuple(Relation relation){
        Tuple tuple = new Tuple(relation);
        Column [] columns = relation.getColumns();
        DbValue [] dbValues = tuple.getDbValues();
        AstCursor c = new AstCursor();
        int index = 0;
        try{
        for (c.FirstElement(getLiteral_list()); c.MoreElement(); c.NextElement() ) {
            if(columns[index].getType().equals("str")){
                dbValues[index] = new DbString(c.node.tok[0].getTokenName().toString(), columns[index].getName());
            }
            else
                dbValues[index] = new DbInt(Integer.parseInt(c.node.tok[0].getTokenName().toString()), columns[index].getName());
            index++;
        }
        }catch (Exception e){
            System.err.println("Type conversion error");
            return null;
        }

        tuple.setDbValues(dbValues);
        return tuple;
    }

    public AstToken getINSERT () {
        return (AstToken) tok [0] ;
    }

    public AstToken getINTO () {
        
        return (AstToken) tok [1] ;
    }

    public AstToken getLP () {
        
        return (AstToken) tok [3] ;
    }

    public Literal_list getLiteral_list () {
        
        return (Literal_list) arg [1] ;
    }

    public AstToken getRP () {
        
        return (AstToken) tok [4] ;
    }

    public Rel_name getRel_name () {
        
        return (Rel_name) arg [0] ;
    }

    public AstToken getSEMI () {
        return (AstToken) tok [5] ;
    }

    public AstToken getVALUES () {
        
        return (AstToken) tok [2] ;
    }

    public boolean[] printorder () {
        
        return new boolean[] {true, true, false, true, true, false, true, true} ;
    }

    public InsertCmd setParms
    (AstToken tok0, AstToken tok1, Rel_name arg0, AstToken tok2, AstToken tok3, Literal_list arg1, AstToken tok4, AstToken tok5)
    {
        
        arg = new AstNode [ARG_LENGTH] ;
        tok = new AstTokenInterface [TOK_LENGTH] ;
        
        tok [0] = tok0 ;            /* INSERT */
        tok [1] = tok1 ;            /* INTO */
        arg [0] = arg0 ;            /* Rel_name */
        tok [2] = tok2 ;            /* VALUES */
        tok [3] = tok3 ;            /* LP */
        arg [1] = arg1 ;            /* Literal_list */
        tok [4] = tok4 ;            /* RP */
        tok [5] = tok5 ;            /* SEMI */
        
        InitChildren () ;
        return (InsertCmd) this ;
    }

}
