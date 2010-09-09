
//
// This is the grammar specification from the Final Draft of the generic spec.
////
// This is the X10 grammar specification based on the Final Draft of the Java generic spec.
//

package x10.parser;


import com.ibm.lpg.*;

public class X10Parser implements RuleAction
{
    PrsStream prsStream;
    ParseTable prs;
    BacktrackingParser btParser;

    public X10Parser(PrsStream prsStream)
    {
        this.prsStream = prsStream;
        this.prs = new X10Parserprs();
    }

    public Ast parser()
    {
        try
        {
            btParser = new BacktrackingParser((TokenStream)prsStream, prs, (RuleAction)this);
        }
        catch (NotBacktrackParseTableException e)
        {
            System.out.println("****Error: Regenerate X10Parserprs.java with -BACKTRACK option");
            System.exit(1);
        }
        catch (BadParseSymFileException e)
        {
            System.out.println("****Error: Bad Parser Symbol File -- X10Parsersym.java. Regenerate X10Parserprs.java");
            System.exit(1);
        }
	try
        {
	    return (Ast) btParser.parse();
	}
	catch (BadParseException e)
	{
    	    prsStream.reset(e.error_token); // point to error token

    	    DiagnoseParser diagnoseParser = new DiagnoseParser(prsStream, prs);
    	    diagnoseParser.diagnose(e.error_token);
	}
	return null;
    }


    public void ruleAction( int ruleNumber)
    {
        switch(ruleNumber)
        {

 
    //
    // Rule 1:  identifier ::= Identifier
    //
            case 1: 
            {
   if (prsStream.getKind(btParser.getToken(1)) != X10Parserprs.TK_Identifier)
   {
        System.out.println("Turning keyword " + prsStream.getName(btParser.getToken(1)) + " into an identifier");
        prsStream.reportError(btParser.getToken(1), "");
   }
             }
            break; 
 
    //
    // Rule 45:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
    //
            case 45: 
            {
         btParser.setSym1(new Ast());
             }
            break; 
    
            default:
	    break;
	}
	return;
    }

}

