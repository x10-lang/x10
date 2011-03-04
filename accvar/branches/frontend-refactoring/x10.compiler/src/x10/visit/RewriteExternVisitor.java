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

import polyglot.frontend.Job;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Node;
import x10.ast.NodeFactory;
import x10.extension.X10ClassBodyExt_c;
import x10.extension.X10Ext;
import x10.types.SemanticException;
import x10.types.TypeSystem;
import x10.types.TypeSystem;

public class RewriteExternVisitor extends ContextVisitor {

    public RewriteExternVisitor(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        n = super.leaveCall(old, n, v);

        if (n.ext() instanceof X10ClassBodyExt_c) {
            return ((X10ClassBodyExt_c) n.ext()).rewrite((TypeSystem) typeSystem(), nodeFactory(), job.extensionInfo());
        }

        return n;
    }

}
