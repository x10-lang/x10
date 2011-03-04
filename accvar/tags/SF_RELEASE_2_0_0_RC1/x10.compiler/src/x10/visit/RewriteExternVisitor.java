/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.visit;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.extension.X10ClassBodyExt_c;
import x10.extension.X10Ext;
import x10.types.X10TypeSystem;

public class RewriteExternVisitor extends ContextVisitor {

    public RewriteExternVisitor(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        n = super.leaveCall(old, n, v);

        if (n.ext() instanceof X10ClassBodyExt_c) {
            return ((X10ClassBodyExt_c) n.ext()).rewrite((X10TypeSystem) typeSystem(), nodeFactory(), job.extensionInfo());
        }

        return n;
    }

}
