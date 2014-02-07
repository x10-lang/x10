/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.visit;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

/**
 * A visitor that applies the given {@link NodeTransformer}
 * to every node in the AST.
 */
public class NodeTransformingVisitor extends ContextVisitor {
    protected final NodeTransformer xform;

    public NodeTransformingVisitor(Job job, TypeSystem ts, NodeFactory nf, NodeTransformer xform) {
        super(job, ts, nf);
        this.xform = xform;
    }

    @Override
    protected Node leaveCall(Node old, Node n, NodeVisitor v) {
        return xform.transform(n, old, this);
    }
}
