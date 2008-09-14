/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.visit;

import java.util.Collections;
import java.util.List;
import java.util.Iterator;

import polyglot.ast.Block;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.frontend.Job;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.util.Position;
import polyglot.util.InternalCompilerError;
import polyglot.ext.x10.ast.Closure;
import polyglot.ext.x10.ast.X10ClockedLoop;
import polyglot.ext.x10.ast.X10ConstructorDecl;
import polyglot.ext.x10.ast.X10FieldDecl;
import polyglot.ext.x10.ast.X10Loop;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Def;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;

/**
 * Visitor that expands implicit declarations in formal parameters.
 */
public class CheckNativeAnnotationsVisitor extends ContextVisitor {
    public CheckNativeAnnotationsVisitor(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    public String getJavaRep(X10ClassDef def) {
        try {
            X10TypeSystem xts = (X10TypeSystem) this.typeSystem();
            Type rep = (Type) xts.systemResolver().find(QName.make("x10.compiler.NativeRep"));
            List<Type> as = def.annotationsMatching(rep);
            for (Type at : as) {
                assertNumberOfInitializers(at, 2);
                String lang = getPropertyInit(at, 0);
                if (lang != null && lang.equals("java")) {
                    return getPropertyInit(at, 1);
                }
            }
        }
        catch (SemanticException e) {}
        return null;
    }

    String getPropertyInit(Type at, int index) {
        at = X10TypeMixin.baseType(at);
        if (at instanceof X10ClassType) {
            X10ClassType act = (X10ClassType) at;
            if (index < act.propertyInitializers().size()) {
                Expr e = act.propertyInitializer(index);
                if (e instanceof StringLit) {
                    StringLit lit = (StringLit) e;
                    String s = lit.value();
                    return s;
                }
            }
        }
        return null;
    }

    void assertNumberOfInitializers(Type at, int len) {
        at = X10TypeMixin.baseType(at);
        if (at instanceof X10ClassType) {
            X10ClassType act = (X10ClassType) at;
            assert len == act.propertyInitializers().size();
        }
    }

    String getJavaImplForDef(X10Def o) {
        X10TypeSystem xts = (X10TypeSystem) o.typeSystem();
        try {
            Type java = (Type) xts.systemResolver().find(QName.make("x10.compiler.Native"));
            List<Type> as = o.annotationsMatching(java);
            for (Type at : as) {
                assertNumberOfInitializers(at, 2);
                String lang = getPropertyInit(at, 0);
                if (lang != null && lang.equals("java")) {
                    String lit = getPropertyInit(at, 1);
                    return lit;
                }
            }
        }
        catch (SemanticException e) {}
        return null;
    }


    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        boolean isNative = false;
        boolean classHasNativeRep = false;
        boolean defHasNativeImp = false;
        
        if (n instanceof ClassMember)
            return n;
        
        X10ClassDef cd = (X10ClassDef) context.currentClassDef();
        
        if (cd == null)
            return n;
        
        if (getJavaRep(cd) != null)
            classHasNativeRep = true;
        
        if (n instanceof X10MethodDecl) {
            X10MethodDecl md = (X10MethodDecl) n;
            X10Def def = (X10Def) md.methodDef();
            if (md.flags().flags().isNative())
                isNative = true;
            if (getJavaImplForDef(def) != null)
                defHasNativeImp = true;
        }
        
        if (n instanceof X10ConstructorDecl) {
            X10ConstructorDecl xd = (X10ConstructorDecl) n;
            X10Def def = (X10Def) xd.constructorDef();
            if (xd.flags().flags().isNative())
                isNative = true;
            if (getJavaImplForDef(def) != null)
                defHasNativeImp = true;
        }

        if (n instanceof X10FieldDecl) {
            X10FieldDecl fd = (X10FieldDecl) n;
            X10Def def = (X10Def) fd.fieldDef();
            if (getJavaImplForDef(def) != null)
                defHasNativeImp = true;
        }
        
        if (classHasNativeRep && ! isNative && n instanceof X10MethodDecl) {
            throw new SemanticException("Class with NativeRep annotation may contain only native methods.", n.position());
        }
        if (classHasNativeRep && ! isNative && n instanceof X10ConstructorDecl) {
            throw new SemanticException("Class with NativeRep annotation may contain only native constructors.", n.position());
        }
        if (defHasNativeImp && ! isNative && n instanceof X10MethodDecl) {
            throw new SemanticException("Method with Native annotation must be declared native.");
        }
        if (defHasNativeImp && n instanceof X10ConstructorDecl) {
            throw new SemanticException("Constructors may not have Native annotations.");
        }
        if (classHasNativeRep && ! defHasNativeImp && n instanceof X10MethodDecl) {
            throw new SemanticException("Methods of a class with NativeRep annotation must be annotated Native.", n.position());
        }
        if (classHasNativeRep && ! defHasNativeImp && n instanceof X10FieldDecl) {
            throw new SemanticException("Fields of a class with NativeRep annotation must be annotated Native.", n.position());
        }

        return n;
    }
}
