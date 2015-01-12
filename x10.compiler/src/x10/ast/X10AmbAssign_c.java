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

import polyglot.ast.AmbAssign_c;
import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Term;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.types.Context;

public class X10AmbAssign_c extends AmbAssign_c {

	protected Expr left;
	
    public X10AmbAssign_c(NodeFactory nf, Position pos, Expr left, Operator op, Expr right) {
    	super(nf, pos, left, op, right);
    	this.left = left;
    }
    
    @Override
    public Assign visitLeft(NodeVisitor v) {

    	if (v instanceof ContextVisitor) {
    		ContextVisitor cv = (ContextVisitor) v;
    		// Do not update context if within a deptype. 
    		// This is an illegal user program -- assignments are not allowed in dep types --
    		// and the error will be reported separately to the user.
    		if (! ((Context) cv.context()).inDepType())
    			v = cv.context(((Context) cv.context()).pushAssignment());
    	}
    	return superVisitLeft(v);
    }
    
    private Assign superVisitLeft(NodeVisitor v) {
  	  Expr left = (Expr) visitChild(this.left, v);
        if (left != this.left) {
  	  X10AmbAssign_c n = (X10AmbAssign_c) copy();
  	  n.left = left;
  	  return n;
        }
        return this;
    }

    
    @Override
    public Node disambiguate(ContextVisitor ar) {
	if (left instanceof Call) {
	    Call c = (Call) left;
	    if (c.target() instanceof Expr) {
		NodeFactory nf = (NodeFactory) ar.nodeFactory();
		return nf.SettableAssign(position(), (Expr) c.target(), c.arguments(), operator(), right());
	    }
	}
	
	return superDisambiguate(ar);
    }
    
    private Node superDisambiguate(ContextVisitor ar) {
        AmbAssign_c n = this;
        
        if (left instanceof Local) {
            LocalAssign a = ar.nodeFactory().LocalAssign(n.position(), (Local)left, operator(), right());
            return a;
        }
        else if (left instanceof Field) {
            FieldAssign a = ar.nodeFactory().FieldAssign(n.position(), ((Field)left).target(), ((Field)left).name(), operator(), right());
            a = a.targetImplicit(((Field) left).isTargetImplicit());
            a = a.fieldInstance(((Field) left).fieldInstance());
            return a;
        } 
        else if (left instanceof ArrayAccess) {
            ArrayAccessAssign a = ar.nodeFactory().ArrayAccessAssign(n.position(), ((ArrayAccess)left).array(), ((ArrayAccess)left).index(), operator(), right());
            return a;
        }

        // LHS is still ambiguous.  The pass should get rerun later.
        return this;
        // throw new SemanticException("Could not disambiguate left side of assignment!", n.position());
    }

    
    public Type leftType() {
        return left.type();
    }
    
    @Override
    public Expr left() {
        return left;
    }

    public Term firstChild() {
        return left;
    }
    
    protected void acceptCFGAssign(CFGBuilder v) {
        v.visitCFG(left, right(), ENTRY);
        v.visitCFG(right(), this, EXIT);
    }
    
    protected void acceptCFGOpAssign(CFGBuilder v) {
        v.visitCFG(left, right(), ENTRY);
        v.visitCFG(right(), this, EXIT);
    }
    
    public Assign typeCheckLeft(ContextVisitor tc) {
        // Didn't finish disambiguation; just return.
        return this;
    }
    public Node typeCheck(ContextVisitor tc) {
        // Didn't finish disambiguation; just return.
        return this;
    }
    public String toString() {
  	    return left + " " + op + " " + right;
  	   }
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
  	    printSubExpr(left, true, w, tr);
  	    w.write(" ");
  	    w.write(op.toString());
  	    w.allowBreak(2, 2, " ", 1); // miser mode
  	    w.begin(0);
  	    printSubExpr(right, false, w, tr);
  	    w.end();
    }
}
