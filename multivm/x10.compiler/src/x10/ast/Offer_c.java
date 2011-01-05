/**
 * 
 */
package x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Return;
import polyglot.ast.Return_c;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import x10.errors.Errors;
import polyglot.types.Context;
import x10.visit.X10TypeChecker;

/**
 * An implementation of the offer e; construct in X10.
 * Patterned after return e. However does not have the control flow
 * connotations of return e. e.g. does not interact with finish blocks.
 * Control flow continues from an offer statement just as it would from
 * a normal statement.
 * @author vj
 *
 */
public class Offer_c extends Stmt_c implements Offer {

	Expr expr;
	/**
	 * @param pos
	 */
	public Offer_c(Position pos, Expr e) {
		super(pos);
		this.expr = e;
	}

    /** Get the expression to return, or null. */
    public Expr expr() {
	return this.expr;
    }

    /** Set the expression to return, or null. */
    public Offer expr(Expr expr) {
	Offer_c n = (Offer_c) copy();
	n.expr = expr;
	return n;
    }

    /** Reconstruct the statement. */
    protected Offer_c reconstruct(Expr expr) {
	if (expr != this.expr) {
	    Offer_c n = (Offer_c) copy();
	    n.expr = expr;
	    return n;
	}

	return this;
    }
    
    /** Type check the statement. */
    public Node typeCheck(ContextVisitor tc) {
    	// Find a T such that t is an instance of Reducer[T].
    	// check that the type of the expression e is a subtype of T.
    	Type rType = ((Context) tc.context()).collectingFinishType();
    	if (rType != null) {
    		Type eType = expr().type();
    		// rType will already be T, not Reducible[T]
    		if (rType != null && ! tc.typeSystem().isSubtype(eType, rType, tc.context()))
    			Errors.issue(tc.job(),
    			        new Errors.OfferDoesNotMatchCollectingFinishType(eType, rType, position()));
    		if (rType != null)
    			return this;
    	}
    	Errors.issue(tc.job(), new Errors.NoCollectingFinishFound(this.toString(), position()));
    	return this;
    }
    
    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
	Expr expr = (Expr) visitChild(this.expr, v);
	return reconstruct(expr);
    }

	/* (non-Javadoc)
	 * @see polyglot.ast.Term_c#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	@Override
	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		v.visitCFG(expr, this, EXIT);
		return succs;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#firstChild()
	 */
	public Term firstChild() {
		  if (expr != null) return expr;
	        return null;
	}
	
	public String toString() {
		return "offer" + (expr != null ? " " + expr : "") + ";";
	}

	/** Write the statement to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("offer") ;
		if (expr != null) {
			w.write(" ");
			print(expr, w, tr);
		}
		w.write(";");
	}


	    

}
