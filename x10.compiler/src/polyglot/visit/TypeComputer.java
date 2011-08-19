/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
