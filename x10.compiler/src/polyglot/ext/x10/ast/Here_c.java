/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.util.Position;
import polyglot.util.CodeWriter;
import polyglot.visit.PrettyPrinter;

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
        // TODO:
        return this;
    }

    /**
     * Visit this term in evaluation order.
     */
    public List acceptCFG(CFGBuilder v, List succs) {
        // TODO:
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
    
    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    	w.write(" here ");
    }
}
