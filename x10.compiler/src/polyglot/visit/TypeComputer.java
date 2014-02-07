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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.visit;

import polyglot.ast.NodeFactory;
import polyglot.frontend.*;
import polyglot.types.TypeSystem;

/** Visitor which performs type checking on the AST. */
public class TypeComputer extends ContextVisitor
{
    public TypeComputer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
//    public Node override(Node parent, Node n) {
//        try {
//            if (Report.should_report(Report.visit, 2))
//                Report.report(2, ">> " + this + "::override " + n);
//            
//            Node m = n.del().typeCheckOverride(parent, this);
//            
//            return m;
//        }
//        catch (SemanticException e) {
//            if (e.getMessage() != null) {
//                Position position = e.position();
//                
//                if (position == null) {
//                    position = n.position();
//                }
//                
//                this.errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
//                                     e.getMessage(), position);
//            }
//            else {
//                // silent error; these should be thrown only
//                // when the error has already been reported 
//            }
//            
//            return n;
//        }
//    }

//    protected NodeVisitor enterCall(Node n) throws SemanticException {
//        TypeComputer v = (TypeComputer) n.del().typeCheckEnter(this);
//        return v;
//    }
//    
//    protected Node leaveCall(Node old, final Node n, NodeVisitor v) throws SemanticException {
//        final TypeComputer tc = (TypeComputer) v;
//
//        Node m = n;
//        try {
//        	m = m.del().typeCheck(tc);
//        	m = m.del().checkConstants(tc);
//        }
//        catch (SemanticException e) {
//        	if (e.getMessage() != null) {
//        		Position position = e.position();
//
//        		if (position == null) {
//        			position = n.position();
//        		}
//
//        		tc.errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
//        				e.getMessage(), position);
//        	}
//        	else {
//        		// silent error; these should be thrown only
//        		// when the error has already been reported 
//        	}
//        }
//
//        return n;
//    }   
}
