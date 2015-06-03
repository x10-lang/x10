/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2015.
 */

package x10.visit;

import java.util.HashSet;
import java.util.Set;

import polyglot.ast.CodeNode;
import polyglot.ast.Expr;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Special;
import polyglot.frontend.Job;
import polyglot.types.LocalDef;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;

/**
 * A very simple minded flow-insensitive pass that looks for
 * unused local variables and eliminates their declarations.
 * The motivation for this pass is to be run immediately after
 * copy propagation (which leaves behind unused LocalDecls).
 * 
 * This is significantly simpler/faster than a true dataflow-based
 * dead code elimination pass, but of course will not find all 
 * opportunities for elimination, just the truly obvious ones.
 * 
 * In conjunction with copy propagation and CodeCleanup however,
 * this pass is effective at cleaning up much of the clutter introduced
 * by inlining.
 */
public class UnusedVariableEliminator extends ErrorHandlingVisitor {

    public UnusedVariableEliminator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
    @Override
    public Node override(Node n) {
        if (n instanceof CodeNode) {
            Set<LocalDef> uses = new HashSet<LocalDef>();
            n.visit(new UseFinder(uses));
            return n.visit(new DeclKiller(uses, nf));
        }
        return null;
    }

    protected static class UseFinder extends NodeVisitor {
        protected Set<LocalDef> uses;

        public UseFinder(Set<LocalDef> uses) {
            this.uses = uses;
        }

        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (n instanceof Local) {
                uses.add(((Local)n).localInstance().def());
            }

            return n;
        }
    }    
    
    protected static class DeclKiller extends NodeVisitor {
        protected Set<LocalDef> uses;
        protected NodeFactory nf;

        public DeclKiller(Set<LocalDef> uses, NodeFactory nf) {
            this.uses = uses;
            this.nf = nf;
        }

        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (n instanceof LocalDecl && !uses.contains(((LocalDecl)n).localDef())) {
                Expr init = ((LocalDecl) n).init();
                if (init == null || init instanceof Local || init instanceof Special) {
                    return nf.Empty(n.position());
                } else {
                    return nf.Eval(n.position(), init);
                }
            }

            return n;
        }
    }    

    
}
