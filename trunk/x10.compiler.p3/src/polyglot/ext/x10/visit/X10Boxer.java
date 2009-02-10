/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.visit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.Closure;
import polyglot.ext.x10.ast.X10Boxed_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;

/**
 * Visitor that inserts boxing and unboxing code into the AST.
 */
public class X10Boxer extends ContextVisitor {
    X10TypeSystem xts;

    public X10Boxer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
    }
    
    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof X10Boxed_c) {
            X10Boxed_c b = (X10Boxed_c) n;
            return b.wrap(this);
        }
        return super.leaveCall(old, n, v);
    }
}
