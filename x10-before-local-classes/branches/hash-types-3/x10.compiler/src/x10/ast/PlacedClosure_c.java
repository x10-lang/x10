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
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PruningVisitor;
import polyglot.visit.TypeChecker;

import x10.types.ClosureDef;
import x10.types.X10Context;
import x10.types.X10TypeSystem;
import x10.types.checker.PlaceChecker;
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
        	def.setPlaceTerm(PlaceChecker.computePlaceTerm(e, (X10Context) tc.context(), ts));
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
