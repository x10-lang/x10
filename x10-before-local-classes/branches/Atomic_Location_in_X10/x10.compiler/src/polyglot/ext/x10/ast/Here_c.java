/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.ArrayAccess_c;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.util.Position;
import polyglot.util.CodeWriter;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Type;

import polyglot.visit.CFGBuilder;


/**
 *
 */
public class Here_c extends Expr_c 
    implements Here {

    public Here_c(Position p) {
        super(p);
    }
    
    /**
     * Return the first (sub)term performed when evaluating this
     * term.
     */
    public Term entry() {
        return this;
    }

    /**
     * Visit this term in evaluation order.
     */
    public List acceptCFG(CFGBuilder v, List succs) {
        return succs;
    }
    
    public String toString() {
    	return "here";
    }
    /* (non-Javadoc)
     * @see polyglot.ext.x10.ast.TranslateWhenDumpedNode#getArgument(int)
     */
    public Node getArgument(int id) {
        assert (false);
        return null;
    }
    
    /** Type check the expression. */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		return type(ts.place());
	}
    public String translate(Resolver c) {
      return "x10.lang.Runtime.here()";
    }
    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    	w.write(" here ");
    }
}
