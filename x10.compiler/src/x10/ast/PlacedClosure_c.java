/**
 * 
 */
package x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PruningVisitor;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstrainedTerm;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.types.ClosureDef;
import x10.types.X10Context;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;

/**
 * A common abstraction for a closure that may execute at a given place,
 * viz future (p) expr and at (p) expr.
 * 
 * @author vj
 *
 */
public class PlacedClosure_c extends Closure_c implements PlacedClosure {


	protected Expr place;

	public PlacedClosure_c(Position p, Expr place, TypeNode returnType, Block body) {
		super(p, Collections.EMPTY_LIST, returnType, null, 
				Collections.EMPTY_LIST, body);
		this.place = place;
	}

	/** Get the RemoteActivity's place. */
	public Expr place() {
		return place;
	}

	/** Set the RemoteActivity's place. */
	public RemoteActivityInvocation place(Expr place) {
		this.place = place;
		return this;
	}
	
	  /** Visit the children of the expression. 
     * vj: TODO: I use a hack below to bypass 
     * visiting the embedded stmt if the visitor is a ReachChecker.
     * Otherwise a reach error is generated that is in fact spurious.
     * There must be a way to convince the ReachChecker legitimately that this statement
     * is reachable if the future is reachable.
     * */
    public Node visitChildren( NodeVisitor v ) {
    	Expr place = (Expr) visitChild( this.place, v );
    	PlacedClosure_c n = (PlacedClosure_c) super.visitChildren(v);
    	if (n.place != place) {
    		if (n == this) n = (PlacedClosure_c) copy();
    		n.place = place;
    	}
    	return n;
    }
    
  /*  @Override
    public NodeVisitor typeCheckEnter(TypeChecker v) throws SemanticException {
    	if (placeTerm != null) {
    		v = (TypeChecker) v.context(pushPlaceTerm((X10Context) v.context()));
    	}
    	return v;
    	
}
*/
    //XTerm placeTerm;
    /**
     * The type of the place term. May be Ref or Place. May contain a newly generated
     * var, equated to self. The associated constraint must be considered to be in scope
     * when examining the body of this PlacedClosure.
     */
   // Type placeType;
    
    public static XConstrainedTerm computePlaceTerm( Expr place, X10Context xc, 
    		X10TypeSystem  ts
    		) throws SemanticException {
    	//System.err.println("PlacedClosure_c: Golden! evaluating placeterm for " + this);
		Type placeType = place.type();
		XConstraint d = X10TypeMixin.xclause(placeType);
		d = (d==null) ? new XConstraint_c() : d.copy();
		XConstraint pc = null;
		XTerm term = null;
		XConstrainedTerm pt = null;
    	boolean placeIsPlace = ts.isImplicitCastValid(placeType, ts.Place(), xc);
    	if (placeIsPlace)  {
    		term = ts.xtypeTranslator().trans(pc, place, xc);
    		if (term == null) {
    			term = XConstraint_c.genUQV();
    		}
    		try {
    			pt = XConstrainedTerm.instantiate(d, term);
			} catch (XFailure z) {

				throw new InternalCompilerError("Cannot construct placeTerm from " + 
						term + " and constraint " + d + ".");
			}
    	} else {
    		boolean placeIsRef = ts.isImplicitCastValid(placeType, ts.Object(), xc);
    		if (placeIsRef) {
    			XTerm src = ts.xtypeTranslator().trans(pc, place, xc);
    			if (src == null) {
    				src = XConstraint_c.genUQV();
    			}
    			try {
    				d= d.substitute(src, d.self());
    				pt = XConstrainedTerm.make(ts.locVar(src,xc), d);
    			} catch (XFailure z) {
    				assert false;
    				throw new InternalCompilerError("Cannot construct placeTerm from " + 
    					 place + " and constraint " + d + ".");
    			}
    		} else 
    			throw new SemanticException(
    					"Place expression |" + place + "| must be of type \"" +
    					ts.Place() + "\", or " + ts.Object() + ", not \"" + place.type() + "\".",
    					place.position());
    	}
    
    	return pt;
    }
    @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
    	
    	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
    	NodeVisitor v = tc.enter(parent, this);
    	
    	if (v instanceof PruningVisitor) {
    		return this;
    	}
    	ClosureDef def = (ClosureDef) this.codeDef();
    	if (def.placeTerm() == null) {
    		Expr e = (Expr) visitChild(place, v);
        	def.setPlaceTerm(computePlaceTerm(e, (X10Context) tc.context(), ts));
    	}
    	// now that placeTerm is set in this node, continue visiting children
    	// enterScope will ensure that placeTerm is installed in the context.
    	
    	return null;
    }
    

    public Node typeCheck(ContextVisitor tc) throws SemanticException {
    	X10Context xc = (X10Context) tc.context();
    	if (xc.inSequentialCode())
			throw new SemanticException("at may not be invoked in sequential code.", position());
    	Node n = super.typeCheck(tc);
    	return n;
    }
    
    protected X10Context pushPlaceTerm(X10Context xc) {
    	ClosureDef def = (ClosureDef) codeDef();
    	XConstrainedTerm pt = def.placeTerm();
    	if (pt != null) {
    		xc = (X10Context) xc.pushPlace(pt);
    	}
    	return xc;
    }
    @Override
    public Context enterChildScope(Node child, Context c) {
    	X10Context xc = (X10Context) super.enterChildScope(child, c);
    	if (child == body) {
    		xc = pushPlaceTerm(xc);
    		addDecls(xc);
    	}
    	return xc;
    }
    
    /**
     * Return the first (sub)term performed when evaluating this
     * term.
     */
    public Term firstChild() {
        return returnType;
    }

    /**
     * Visit this term in evaluation order.
     */
    public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
    	v.visitCFG(returnType, place, ENTRY);

    	// If building the CFG for the enclosing code, don't thread
    	// in the closure body.  Otherwise, we're building the CFG
    	// for the closure itself.
    	if (! succs.isEmpty()) {
    		v.visitCFG(place, this, EXIT);
    	}
    	else {
    		v.visitCFG(place, body, ENTRY);
    		v.visitCFG(body, this, EXIT);
    	}

        /*
        v.visitCFG(returnType, FlowGraph.EDGE_KEY_TRUE, body, ENTRY,
                   FlowGraph.EDGE_KEY_FALSE, this, EXIT);
                   */
        return succs;
    }
    
    public Type childExpectedType(Expr child, AscriptionVisitor av) {
    	X10TypeSystem ts = (X10TypeSystem) av.typeSystem();
    	if ( child == place ) {
    		return ts.Place();
    	}
    	return child.type();
    }

}
