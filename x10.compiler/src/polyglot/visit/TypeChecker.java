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

package polyglot.visit;

import java.util.Map;

import polyglot.ast.*;
import polyglot.frontend.*;
import polyglot.main.Reporter;
import polyglot.types.*;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;

/** Visitor which performs type checking on the AST. */
public class TypeChecker extends ContextVisitor
{
    protected Map<Node,Node> memo;
	
    public TypeChecker(Job job, TypeSystem ts, NodeFactory nf, Map<Node,Node> memo) {
        super(job, ts, nf);
        this.memo = memo;
    }
    
    @Override
    public Node override(Node parent, Node n) {
    	Node n_ = memo.get(n);
		if (n_ != null) {
	        this.addDecls(n_);
	        return n_;
		}
    	
		if (reporter.should_report(Reporter.visit, 2))
		    reporter.report(2, ">> " + this + "::override " + n);
		
		Node m = n.del().typeCheckOverride(parent, this);
		
		if (m != null) {
//		    memo.put(n, m);
//		    memo.put(m, m);
		}
		
		return m;
    }

    protected NodeVisitor enterCall(Node n) {
        TypeChecker v = (TypeChecker) n.del().typeCheckEnter(this);
        return v;
    }
    
    protected Node leaveCall(Node old, final Node n, NodeVisitor v) {
        final TypeChecker tc = (TypeChecker) v;

        // Check for expressions with unknown type.  This avoids reporting too many type errors.
        class AmbChecker extends NodeVisitor {
        	boolean amb;
            @Override
        	public Node override(Node n) {
        		if (n instanceof Expr) {
        			Expr e = (Expr) n;
        			if (e.type() == null || e.type() instanceof UnknownType) {
        				amb = true;
        			}
        		}
        		return n;
        	}
        }
        
        AmbChecker ac = new AmbChecker();
        n.visitChildren(ac);
        if (ac.amb) {
        	return n;
        }
        
        Node m = n;
      
        m = m.del().disambiguate(tc);
        m = m.del().typeCheck(tc);
        m = m.del().checkConstants(tc);

        // Record the new node in the memo table.
//        memo.put(old, m);
//        memo.put(n, m);
//        memo.put(m, m);

        return m;
    }   
}
