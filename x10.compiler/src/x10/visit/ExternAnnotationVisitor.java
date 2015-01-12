/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.X10ClassDecl;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10FieldDecl;
import x10.ast.X10MethodDecl;
import x10.extension.X10Del;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Def;
import x10.types.X10MethodDef;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * Visitor to replace @NativeCPPExtern annotations with @Native annotations.
 */
public class ExternAnnotationVisitor extends ContextVisitor {
    String theLanguage;

    public ExternAnnotationVisitor(Job job, TypeSystem ts, NodeFactory nf, String theLanguage) {
        super(job, ts, nf);
        this.theLanguage = theLanguage;
    }

    boolean isExtern(X10Def o) {
        return !o.annotationsNamed(QName.make("x10.compiler.NativeCPPExtern")).isEmpty();
    }

    void process(X10MethodDef def) {
        StringBuilder sb = new StringBuilder(def.name() + "(");
        for (int i=1; i<=def.formalTypes().size(); i++) {
            if (i > 1) sb.append(",");
            sb.append("(#" + i + ")");
            if (Types.isX10RegionArray(def.formalTypes().get(i-1).get()))
                sb.append("->raw()->raw()");
            if (Types.isX10Rail(def.formalTypes().get(i-1).get()))
                sb.append("->raw");
        }
        sb.append(") /* ExternAnnotationVisitor */");
        List<Expr> inits = new ArrayList<Expr>(2);
        inits.add(nf.StringLit(Position.COMPILER_GENERATED, theLanguage));
        inits.add(nf.StringLit(Position.COMPILER_GENERATED, sb.toString()));
        List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>(def.defAnnotations().size() + 1);
        for (Ref<? extends Type> at : def.defAnnotations()) {
            ats.add(at);
        }
        X10ClassType t = ts.NativeType();
        ats.add(Types.ref(t.propertyInitializers(inits)));
        def.setDefAnnotations(ats);
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof X10MethodDecl) {
            X10MethodDef def = (X10MethodDef) ((X10MethodDecl) n).methodDef();
            if (def.flags().isNative() && def.flags().isStatic() && isExtern(def))
                process(def);
            }

        return n;
    }
}
