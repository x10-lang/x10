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

import java.util.HashSet;
import java.util.Set;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.LocalDef;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

/**
 * This visitor converts non-final local variables into final local variables.
 * This improves the precision of some analyses.
 *
 * @author nystrom
 */
public class FinalLocalExtractor extends NodeVisitor {

    /** Set of LocalInstances declared final; these should not be made non-final. */
    protected Set<LocalDef> isFinal;
    
    /**
     * @param job
     * @param ts
     * @param nf
     */
    public FinalLocalExtractor(Job job, TypeSystem ts, NodeFactory nf) {
        super();
    }

    @Override
    public NodeVisitor begin() {
        isFinal = CollectionFactory.newHashSet();
        return super.begin();
    }
    
    @Override
    public void finish() {
        isFinal = null;
    }
    
    // TODO: handle locals that are not initialized when declared
    //
    // TODO: handle anonymous classes: this visitor assumes all LocalInstances
    // are set correctly, which is true after disambiguation, except for anonymous
    // classes.
    //
    // TODO: convert to pseudo-SSA form: generate a new local decl when a local
    // is assigned, rather than marking the original as final.  If a local
    // requires a phi-function, just mark it non-final rather than generating
    // the phi.
    @Override
    public NodeVisitor enter(Node parent, Node n) {
        if (n instanceof Formal) {
            Formal d = (Formal) n;
            LocalDef li = d.localDef();
            if (! li.flags().isFinal()) {
                li.setFlags(li.flags().Final());
            }
            else {
                isFinal.add(li);
            }
        }
        if (n instanceof LocalDecl) {
            LocalDecl d = (LocalDecl) n;
            LocalDef li = d.localDef();
            if (! li.flags().isFinal()) {
                li.setFlags(li.flags().Final());
            }
            else {
                isFinal.add(li);
            }
        }
        if (n instanceof Unary) {
            Unary u = (Unary) n;
            if (u.expr() instanceof Local) {
                Local l = (Local) u.expr();
                LocalDef li = l.localInstance().def();
                if (u.operator() == Unary.PRE_DEC || u.operator() == Unary.POST_DEC ||
                    u.operator() == Unary.PRE_INC || u.operator() == Unary.POST_INC) {
                    if (! isFinal.contains(li)) {
                        li.setFlags(li.flags().clearFinal());
                    }
                }
            }
        }
        if (n instanceof LocalAssign) {
            LocalAssign a = (LocalAssign) n;
            if (a.local() instanceof Local) {
                LocalDef li = ((Local) a.local()).localInstance().def();
                if (! isFinal.contains(li)) {
                    li.setFlags(li.flags().clearFinal());
                }
            }
        }
        return super.enter(parent, n);
    }
    
    protected static class LocalDeclFixer extends NodeVisitor {
        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (n instanceof Formal) {
                Formal d = (Formal) n;
                FlagsNode f = d.flags();
                f = f.flags(d.localDef().flags());
            }
            if (n instanceof LocalDecl) {
                LocalDecl d = (LocalDecl) n;
                FlagsNode f = d.flags();
                f = f.flags(d.localDef().flags());
                return d.flags(f);
            }
            return n;
        }
    }
    
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        // Revisit everything to ensure the local decls' flags agree with
        // their local instance's.
        if (n instanceof SourceFile) {
            return n.visit(new LocalDeclFixer());
        }
        return n;
    }
}
