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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.frontend.Job;
import polyglot.main.Report;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.*;
import x10.types.ClassDef;
import x10.types.ClassType;
import x10.types.ConstructorDef;
import x10.types.ConstructorInstance;
import x10.types.FieldDef;
import x10.types.Flags;
import x10.types.LocalDef;
import x10.types.MethodInstance;
import x10.types.Name;
import x10.types.ParameterType;
import x10.types.QName;
import x10.types.Ref;
import x10.types.SemanticException;
import x10.types.Type;
import x10.types.TypeSystem;
import x10.types.Types;
import x10.types.X10Def;
import x10.types.X10ConstructorDef;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Flags;
import x10.types.X10MethodDef;
import x10.types.X10TypeMixin;
import x10.types.TypeSystem;

/**
 * Visitor that expands @NativeClass and @NativeDef annotations.
 */
public class MainMethodFinder extends ContextVisitor {
    final TypeSystem ts;
    final NodeFactory nf;
    final Method hasMain;

    public MainMethodFinder(Job job, TypeSystem ts, NodeFactory nf, Method hasMain) {
        super(job, ts, nf);
        this.ts = (TypeSystem) ts;
        this.nf = (NodeFactory) nf;
        this.hasMain = hasMain;
    }

    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (!(n instanceof X10MethodDecl_c))
            return n;

        X10MethodDecl_c m = (X10MethodDecl_c) n;

        if (m.formals().size() == 1 &&
                X10PrettyPrinterVisitor.isMainMethod(ts, m.flags().flags(), m.name().id(), m.returnType().type(), m.formals().get(0).declType(), context)) {
            try {
                hasMain.invoke(null, context.currentClass().name().toString());
            } catch (Throwable t) { t.printStackTrace(); }
        }
        return n;
    }
}
