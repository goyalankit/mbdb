// Automatically generated code.  Edit at your own risk!
// Generated by bali2jak v2002.09.03.

package mdb;

import com.bdb.DbClient;

public class CloseCmd extends Close {

    final public static int ARG_LENGTH = 1 /* Kludge! */ ;
    final public static int TOK_LENGTH = 2 ;

    public void execute () {
        
        super.execute();
        DbClient.dbEnvFilename = "mydbenv";
        DbClient.invalidateCahe();
    }

    public AstToken getCLOSE () {
        
        return (AstToken) tok [0] ;
    }

    public AstToken getSEMI () {
        
        return (AstToken) tok [1] ;
    }

    public boolean[] printorder () {
        
        return new boolean[] {true, true} ;
    }

    public CloseCmd setParms (AstToken tok0, AstToken tok1) {
        
        arg = new AstNode [ARG_LENGTH] ;
        tok = new AstTokenInterface [TOK_LENGTH] ;
        
        tok [0] = tok0 ;            /* CLOSE */
        tok [1] = tok1 ;            /* SEMI */
        
        InitChildren () ;
        return (CloseCmd) this ;
    }

}
