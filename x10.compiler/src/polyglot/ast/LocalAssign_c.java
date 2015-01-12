/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.*;

/**
 * A <code>LocalAssign_c</code> represents a Java assignment expression
 * to a local variable.  For instance, <code>x = e</code>.
 * 
 * The class of the <code>Expr</code> returned by
 * <code>LocalAssign_c.left()</code>is guaranteed to be an <code>Local</code>.
 */
public abstract class LocalAssign_c extends Assign_c implements LocalAssign
{
    Local local;

    public LocalAssign_c(NodeFactory nf, Position pos, Local left, Operator op, Expr right) {
	super(nf, pos, op, right);
	local = left;
    }

    @Override
    public Assign typeCheckLeft(ContextVisitor tc) {
	return this;
    }

    @Override
    public Assign visitLeft(NodeVisitor v) {
	Local local = (Local) visitChild(this.local, v);
	if (local != this.local) {
	    LocalAssign_c n = (LocalAssign_c) copy();
	    n.local = local;
	    return n;
	}
	return this;
    }
    public Expr left() {
	return local;
    }

    public abstract Type leftType();

    public Local local() {
	return local;
    }

    public LocalAssign local(Local local) {
	LocalAssign_c n = (LocalAssign_c) copy();
	n.local = local;
	return n;
    }

    public Term firstChild() {
	if (op == ASSIGN)
	    return right();
	return local;
    }

    protected void acceptCFGAssign(CFGBuilder v) {
	// do not visit left()
	// l = e: visit e -> (l = e)    
	//      v.visitCFG(local(), right(), ENTRY);
	v.visitCFG(right(), this, EXIT);
    }

    protected void acceptCFGOpAssign(CFGBuilder v) {
	/*
      Local l = (Local)left();

      // l OP= e: visit l -> e -> (l OP= e)
      v.visitThrow(l);
      v.edge(l, right().entry());
      v.visitCFG(right(), this);
	 */

	v.visitCFG(local(), right(), ENTRY);
	v.visitCFG(right(), this, EXIT);
    }
    public String toString() {
  	  return local + " " + op + " " + right;
    }
}
