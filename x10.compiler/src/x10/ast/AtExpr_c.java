/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Term;
import polyglot.ast.Formal;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.InternalCompilerError;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.PruningVisitor;
import polyglot.visit.ReachChecker;
import polyglot.visit.TypeBuilder;
import x10.constraint.XTerm;
import x10.constraint.XFailure;
import x10.types.ClosureDef;
import x10.types.constants.ConstantValue;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.XConstrainedTerm;
import x10.types.constraints.CConstraint;
import polyglot.types.Context;

import polyglot.types.TypeSystem;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.errors.Errors;
import x10.extension.X10Del;
import x10.extension.X10Ext;


/** A <code>AtExpr</code> is a representation of the X10 at construct:
 * <code>at (place) { expression }<code>
 * stmts are used to represent the fully exploded version of the expression
 * as might be needed in order to inline array expressions.
 * @author ??
 * @author vj 08/30/09 - Refactored out PlacedClosure.
 */
public class AtExpr_c extends Closure_c implements AtExpr {

	protected Expr place;
	protected List<Node> captures;

	public AtExpr_c(NodeFactory nf, Position p, Expr place, Block body) {
	    this(nf, p, place, null, body);
	}

	public AtExpr_c(NodeFactory nf, Position p, Expr place, List<Node> captures, Block body) {
	    super(nf, p, Collections.<Formal>emptyList(), nf.UnknownTypeNode(Position.COMPILER_GENERATED), null, null, body);
		this.place = place;
		this.captures = captures;
	}

	public List<Node> captures() {
	    return captures;
	}

	public AtExpr_c captures(List<Node> captures) {
	    AtExpr_c n = (AtExpr_c) copy();
	    n.captures = captures;
	    return n;
	}

	/** Get the RemoteActivity's place. */
	public Expr place() {
		return place;
	}

	/** Set the RemoteActivity's place. */
	public AtExpr_c place(Expr place) {
	    if (place == this.place) return this;
	    assert place!=null;
	    AtExpr_c n = (AtExpr_c) copy();
		n.place = place;
		return n;
	}

    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
    	Expr place = (Expr) visitChild(this.place, v);
    	AtExpr_c n = (AtExpr_c) superVisitChildren(v);
    	if (n.place != place) {
    		if (n == this) n = (AtExpr_c) copy();
    		n.place = place;
    	}
    	return n;
    }

    private Node superVisitChildren(NodeVisitor v) {
        return super.visitChildren(v);
    }

    @Override
    public Node buildTypesOverride(TypeBuilder tb) {
        AtExpr_c n = (AtExpr_c) super.buildTypesOverride(tb);
        ClosureDef cd = n.closureDef();
        cd.setPlaceTerm(null); // will be overwritten during typechecking
        return n;
    }

    @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
    	TypeSystem ts = (TypeSystem) tc.typeSystem();
    	NodeVisitor v = tc.enter(parent, this);

    	if (v instanceof PruningVisitor) {
    		return this;
    	}
    	ContextVisitor childtc = (ContextVisitor) v;

    	Expr place = (Expr) visitChild(this.place, childtc);

    	place = Converter.attemptCoercion(tc, place, ts.Place());
    	if (place == null) {
    	    Errors.issue(tc.job(),
    	            new Errors.AtArgMustBePlace(this.place, ts.Place(), this.place.position()));
            place = tc.nodeFactory().Here(this.place.position()).type(ts.Place());
    	}

    	Context c = tc.context();
    	ClosureDef def = (ClosureDef) this.codeDef();
    	if (def.placeTerm() == null) {
    	    XConstrainedTerm placeTerm;
    	    CConstraint d = ConstraintManager.getConstraintSystem().makeCConstraint();
    	    XTerm term = PlaceChecker.makePlace();
    	    try {
    	        placeTerm = XConstrainedTerm.instantiate(d, term);
    	    } catch (XFailure z) {
    	        throw new InternalCompilerError("Cannot construct placeTerm from term and constraint.");
    	    }
    	    try {
    	        XConstrainedTerm realPlaceTerm = PlaceChecker.computePlaceTerm(place, tc.context(), ts);
    	        d.addBinding(placeTerm, realPlaceTerm);
    	    } catch (SemanticException se) { }
    	    def.setPlaceTerm(placeTerm);
    	}

    	// now that placeTerm is computed for this node, install it in the context
    	// and continue visiting children

    	Context oldC = c;
    	c = super.enterChildScope(this.body, childtc.context());
    	XConstrainedTerm pt = def.placeTerm();
    	if (pt != null) {
    		if (c == oldC)
        		c = c.pushBlock();
    		c.setPlace(pt);
    	}

    	AtExpr_c n = this.place(place);
    	n = (AtExpr_c) n.superVisitChildren(childtc.context(c));
    	

		List<AnnotationNode> oldAnnotations = ((X10Ext)n.ext()).annotations();
		if (oldAnnotations != null && !oldAnnotations.isEmpty()) {
			List<AnnotationNode> newAnnotations = visitList(oldAnnotations, v);
			if (! CollectionUtil.allEqual(oldAnnotations, newAnnotations)) {
				n = (AtExpr_c) ((X10Del) n.del()).annotations(newAnnotations);
			}
		}
		
    	return tc.leave(parent, this, n, childtc);
    }

    @Override
    public Node typeCheck(ContextVisitor tc) {
        AtExpr_c n = (AtExpr_c) super.typeCheck(tc);
        Type t = n.returnType().type();
        Context c = super.enterChildScope(body, tc.context());
        ClosureDef def = (ClosureDef) codeDef();
        XConstrainedTerm pt = def.placeTerm();
        t = PlaceChecker.ReplaceHereByPlaceTerm(t,  pt);
        return n.type(t);
    }

    @Override
    public Context enterScope(Context c) {
       // c = c.pushBlock();
        c = c.pushAt(closureDef);
        c.x10Kind = Context.X10Kind.At;
        return c;
    }

    @Override
    public Context enterChildScope(Node child, Context c) {
        if (child == this.place) return c.pop();
        return super.enterChildScope(child, c);
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

    public String toString() {
    	return  "(#" + hashCode() + // todo: using hashCode leads to non-determinism in the output of the compiler
                ") at[" + returnType + "](" + place + ") " + body;
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

  
    public boolean isConstant() {
        return false;
    }

    public ConstantValue constantValue() {
        return null;
    }
        
}
