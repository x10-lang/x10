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

package x10.visit;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Finish;
import x10.ast.X10NodeFactory;
import x10.types.X10TypeSystem;
import x10.util.Synthesizer;

/**
 * Visitor that generates code for work stealing.
 * @author Haibo
 */
public class WSCodeGenerator extends ContextVisitor {
    /** 
     * @param job
     * @param ts
     * @param nf
     */
    final X10TypeSystem xts;
    final X10NodeFactory xnf;
    final Synthesizer synth;

    public WSCodeGenerator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
        synth = new Synthesizer(xnf, xts);
    }

    protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        Node result = n;
        
        if (n instanceof Finish) {
            System.out.println("Work Stealing Code Generator.");
        }
        return result;
    }
}


