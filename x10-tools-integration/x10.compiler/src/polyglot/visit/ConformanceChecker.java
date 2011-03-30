/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.visit;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;

/** Visitor which performs type checking on the AST. */
public class ConformanceChecker extends ContextVisitor
{
    public ConformanceChecker(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
    protected Node leaveCall(Node old, final Node n, NodeVisitor v) {
        ContextVisitor cc = (ContextVisitor) v;
        return n.del().conformanceCheck(cc);
    }   
}
