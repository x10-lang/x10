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

import java.util.HashMap;
import java.util.Map;

import polyglot.ast.Node;
import polyglot.frontend.Job;
import polyglot.util.InternalCompilerError;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

public class ReentrantVisitor extends NodeVisitor {
    protected Job job;
    protected Map<Node,Node> newSubst;
    
    public ReentrantVisitor(Job job) {
        this.job = job;
        this.newSubst = CollectionFactory.newHashMap();
    }

    public Job job() {
        return job;
    }
    
    
    @Override
    public Node visitEdge(Node parent, Node child) {
//        Map<Node,Node> subst = job.astMap();
//        Node n = subst.get(child);
//        
//        if (n != null) {
//            if (true)
//                return n;
//            child = n;
//        }
        
        Node n;
        
        try {
            n = override(parent, child);

            if (n == null) {
                n = visitEdgeNoOverride(parent, child);
            }

//            if (child != n)
//                installSubst(newSubst, child, n);
//
//            if (n == job.ast()) {
//                job.setAstMap(newSubst);
//            }
            
            return n;
        }
        catch (InternalCompilerError e) {
            if (e.position() == null && child != null)
                e.setPosition(child.position());
            throw e;
        }
    }

//    protected void installSubst(final Map<Node,Node> subst, Node old, Node n) {
//        // Remove the children from the substitution.
//        old.visitChildren(new NodeVisitor() {
//            public Node override(Node n) {
//                subst.remove(n);
//                return n;
//            }
//        });
//
//        subst.put(old, n);
//    }
}
