// Automatically generated code.  Edit at your own risk!
// Generated by bali2jak v2002.09.03.

package mdb;
import Jakarta.util.*;
import java.io.*;
import java.util.*;

public class RelDecl extends Decl_rel {

    final public static int ARG_LENGTH = 2 ;
    final public static int TOK_LENGTH = 5 ;

    public void execute () {
        
        super.execute();
    }

    public AstToken getCREATE () {
        
        return (AstToken) tok [0] ;
    }

    public Fld_decl_list getFld_decl_list () {
        
        return (Fld_decl_list) arg [1] ;
    }

    public AstToken getLP () {
        
        return (AstToken) tok [2] ;
    }

    public AstToken getRP () {
        
        return (AstToken) tok [3] ;
    }

    public Rel_name getRel_name () {
        
        return (Rel_name) arg [0] ;
    }

    public AstToken getSEMI () {
        
        return (AstToken) tok [4] ;
    }

    public AstToken getTABLE () {
        
        return (AstToken) tok [1] ;
    }

    public boolean[] printorder () {
        
        return new boolean[] {true, true, false, true, false, true, true} ;
    }

    public RelDecl setParms
    (AstToken tok0, AstToken tok1, Rel_name arg0, AstToken tok2, Fld_decl_list arg1, AstToken tok3, AstToken tok4)
    {
        
        arg = new AstNode [ARG_LENGTH] ;
        tok = new AstTokenInterface [TOK_LENGTH] ;
        
        tok [0] = tok0 ;            /* CREATE */
        tok [1] = tok1 ;            /* TABLE */
        arg [0] = arg0 ;            /* Rel_name */
        tok [2] = tok2 ;            /* LP */
        arg [1] = arg1 ;            /* Fld_decl_list */
        tok [3] = tok3 ;            /* RP */
        tok [4] = tok4 ;            /* SEMI */
        
        InitChildren () ;
        return (RelDecl) this ;
    }

}
