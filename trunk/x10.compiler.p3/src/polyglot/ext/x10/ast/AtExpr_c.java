/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * @author Philippe Charles
 * @author vj
 */
package polyglot.ext.x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.ClosureDef;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.visit.ExprFlattener;
import polyglot.ext.x10.visit.ExprFlattener.Flattener;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.ReachChecker;


/** A <code>AtExp </code> is a representation of the X10 at construct:
 * <code>at (place) { expression }<code>
 * stmts are used to represent the fully exploded version of the expression
 * as might be needed in order to inline array expressions.
 */
public class AtExpr_c extends Closure_c
    implements AtExpr {
    
    public Expr place; 

    public AtExpr_c(Position p, Expr place, TypeNode returnType, Block body) {
	    super(p, Collections.EMPTY_LIST, Collections.EMPTY_LIST, returnType, null, Collections.EMPTY_LIST, body);
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
    	AtExpr_c n = (AtExpr_c) super.visitChildren(v);
    	if (n.place != place) {
    		if (n == this) n = (AtExpr_c) copy();
    		n.place = place;
    	}
    	return n;
    }

    /** Type check the expression. */
    public Node typeCheck( ContextVisitor tc ) throws SemanticException {
    	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
    	X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();

    	Type placeType = place.type();
    	Expr newPlace = place;
    	boolean placeIsPlace = ts.isImplicitCastValid(placeType, ts.Place(), tc.context());
    	if ( ! placeIsPlace ) {
            if (! placeIsPlace) {
                throw new SemanticException(
                     "Place expression of at must be of type \"" +
                     ts.Place() + "\", not \"" + place.type() + "\".",
                     place.position());
            }
    	}
    	AtExpr_c n = (AtExpr_c) place(newPlace);
    	n = (AtExpr_c) super.typeCheck(tc);
    	
    	Type t = n.returnType().type();
    	
    	return n.type(t);
    }
    public Type childExpectedType(Expr child, AscriptionVisitor av) {
    	X10TypeSystem ts = (X10TypeSystem) av.typeSystem();
    	if ( child == place ) {
    		return ts.Place();
    	}
    	return child.type();
    }

    public String toString() {
    	return  " at[" + returnType + "](" + place + ") " + body;
    }
   
    /** Write the expression to an output file. */

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    	w.write("at[");
    	printBlock(returnType, w, tr);
    	w.write("](");
    	printSubExpr(place, false, w, tr);
    	w.write(") ");
    	printBlock(body, w, tr);
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
        
}
