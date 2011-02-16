/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
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

import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.errors.Errors;
import x10.types.ClosureDef;
import polyglot.types.Context;
import polyglot.types.TypeSystem;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.XConstrainedTerm;

/**
 * A common abstraction for a closure that may execute at a given place,
 * viz future (p) expr and at (p) expr.
 * 
 * @author vj
 *
 */
public class PlacedClosure_c extends Closure_c implements PlacedClosure {


	protected Expr place;

	public PlacedClosure_c(NodeFactory nf, Position p, Expr place, TypeNode returnType, TypeNode offerType, Block body) {
		super(nf, p, Collections.<Formal>emptyList(), returnType, null, 
				offerType, body);
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
    
    boolean placeError=false;
    
    @Override
    public Node typeCheck(ContextVisitor tc)  {
    	TypeSystem ts = (TypeSystem) tc.typeSystem();
    	if (placeError) { // this means we were not able to convert this.place into a term of type Place.
    		Errors.issue(tc.job(), 
    				new Errors.AtArgMustBePlace(this.place, ts.Place(), this.position()));
    	}

    	return super.typeCheck(tc);
    }
  @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
    	
    	TypeSystem ts = (TypeSystem) tc.typeSystem();
    	NodeVisitor v = tc.enter(parent, this);
    	
    	if (v instanceof PruningVisitor) {
    		return this;
    	}
    	ClosureDef def = (ClosureDef) this.codeDef();
    	if (def.placeTerm() == null) {
    		Expr e = (Expr) visitChild(place, v);
    		XConstrainedTerm placeTerm = null;
    		try {
    		    placeTerm = PlaceChecker.computePlaceTerm(e, (Context) tc.context(), ts);
    		} catch (SemanticException se) {
    			placeError=true;
    		    CConstraint d = new CConstraint();
    		    XTerm term = PlaceChecker.makePlace();
    		    try {
    		        placeTerm = XConstrainedTerm.instantiate(d, term);
    		    } catch (XFailure z) {
    		        throw new InternalCompilerError("Cannot construct placeTerm from term  and constraint.");
    		    }
    		}
    		def.setPlaceTerm(placeTerm);
    	}
    	// now that placeTerm is set in this node, continue visiting children
    	// enterScope will ensure that placeTerm is installed in the context.
    	
    	return null;
    }
    
    protected Context pushPlaceTerm(Context xc) {
    	ClosureDef def = (ClosureDef) codeDef();
    	XConstrainedTerm pt = def.placeTerm();
    	if (pt != null) {
    		xc = (Context) xc.pushPlace(pt);
    	}
    	return xc;
    }
    @Override
    public Context enterChildScope(Node child, Context c) {
    	if (child == place) return c.pop();
    	Context xc = (Context) super.enterChildScope(child, c);
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
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
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
    	TypeSystem ts = (TypeSystem) av.typeSystem();
    	if ( child == place ) {
    		return ts.Place();
    	}
    	return child.type();
    }

}
