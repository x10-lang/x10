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

import java.util.Collections;
import java.util.List;
import java.util.Iterator;

import polyglot.ast.Block;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.util.Position;
import polyglot.util.InternalCompilerError;
import x10.ast.Closure;
import x10.ast.X10ClockedLoop;
import x10.ast.X10Formal;
import x10.ast.X10Loop;

/**
 * Visitor that expands implicit declarations in formal parameters.
 */
public class X10ImplicitDeclarationExpander extends ContextVisitor {
    public X10ImplicitDeclarationExpander(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof MethodDecl)
            return visitMethodDecl((MethodDecl) n);
        if (n instanceof Closure)
            return visitClosure((Closure) n);
        if (n instanceof X10Loop)
            return visitLoop((X10Loop) n);
        return n;
    }

    private Block explodeAllArgs(List<Formal> formals, Block body) throws SemanticException {
        List<Stmt> stmts = Collections.emptyList();
        for (Iterator<Formal> i = formals.iterator(); i.hasNext();) {
            X10Formal f = (X10Formal) i.next();
            if (!f.hasExplodedVars())
                continue;
            stmts = f.explode(this, stmts, false);
        }
        if (stmts.isEmpty())
            return body;
        stmts.addAll(body.statements());
        return body.statements(stmts);

    }

    private Node visitMethodDecl(MethodDecl n) throws SemanticException {
        List<Formal> fs = n.formals();
        Block b = explodeAllArgs(fs, n.body());
        if (n.body() != b)
            return n.body(b);
        return n;
    }

    private Node visitClosure(Closure c) throws SemanticException {
        List<Formal> fs = c.formals();
        Block b = explodeAllArgs(fs, c.body());
        if (c.body() != b)
            return c.body(b);
        return c;
    }

    private Node visitLoop(X10Loop n) throws SemanticException {
        X10Formal f = (X10Formal) n.formal();
        if (!f.hasExplodedVars())
            return n;
        return n.locals(f.explode(this));
    }
}
