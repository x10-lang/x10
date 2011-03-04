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

import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.types.SemanticException;
import x10.types.Context;

public class X10AmbAssign_c extends AmbAssign_c {

    public X10AmbAssign_c(NodeFactory nf, Position pos, Expr left, Operator op, Expr right) {
	super(nf, pos, left, op, right);
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
    	return super.visitLeft(v);
    }

    
    @Override
    public Node disambiguate(ContextVisitor ar) throws SemanticException {
	if (left instanceof Call) {
	    Call c = (Call) left;
	    if (c.target() instanceof Expr) {
		NodeFactory nf = (NodeFactory) ar.nodeFactory();
		return nf.SettableAssign(position(), (Expr) c.target(), c.arguments(), operator(), right());
	    }
	}
	
	return super.disambiguate(ar);
    }

}
