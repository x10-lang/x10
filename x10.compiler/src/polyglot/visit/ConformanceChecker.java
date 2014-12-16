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
