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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.X10ClassDecl;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10FieldDecl;
import x10.ast.X10MethodDecl;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Def;
import x10.types.X10MethodDef;
import x10.types.constants.StringValue;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

/**
 * Visitor that checks @Native and @NativeRep annotations.
 */
public class CheckNativeAnnotationsVisitor extends ContextVisitor {
    String theLanguage;

    public CheckNativeAnnotationsVisitor(Job job, TypeSystem ts, NodeFactory nf, String theLanguage) {
        super(job, ts, nf);
        this.theLanguage = theLanguage;
    }

    public Map<String, String> getNativeRepParam(X10ClassDef def, int i) {
        Map<String,String> map = CollectionFactory.newHashMap();
        try {
            TypeSystem xts = (TypeSystem) this.typeSystem();
            Type rep = xts.NativeRep();
            List<Type> as = def.annotationsMatching(rep);
            for (Type at : as) {
                assertNumberOfInitializers(at, 4);
                String lang = getPropertyInit(at, 0);
                if (lang != null) {
                    String lit = getPropertyInit(at, i);
                    map.put(lang, lit);
                }
            }
        }
        catch (SemanticException e) {}
        return map;
    }

    public Map<String, String> getNativeRep(X10ClassDef def) {
        return getNativeRepParam(def, 1);
    }

    public Map<String, String> getNativeBoxedRep(X10ClassDef def) {
        return getNativeRepParam(def, 2);
    }

    public Map<String, String> getNativeRTTRep(X10ClassDef def) {
        return getNativeRepParam(def, 3);
    }

    String getPropertyInit(Type at, int index) throws SemanticException {
        at = Types.baseType(at);
        if (at instanceof X10ClassType) {
            X10ClassType act = (X10ClassType) at;
            if (index < act.propertyInitializers().size()) {
                Expr e = act.propertyInitializer(index);
                if (e.isConstant() && e.constantValue() instanceof StringValue) {
                    return ((StringValue)e.constantValue()).value();
                }
                else {
                    throw new SemanticException("Property initializer for @" + at + " must be a string literal.");
                }
            }
        }
        return null;
    }

    void assertNumberOfInitializers(Type at, int len) {
        at = Types.baseType(at);
        if (at instanceof X10ClassType) {
            X10ClassType act = (X10ClassType) at;
            assert len == act.propertyInitializers().size();
        }
    }

    Map<String,String> getNativeImplForDef(X10Def o) {
        Map<String,String> map = CollectionFactory.newHashMap();
        TypeSystem xts = (TypeSystem) o.typeSystem();
        try {
            Type java = xts.NativeType();
            List<Type> as = o.annotationsMatching(java);
            for (Type at : as) {
                assertNumberOfInitializers(at, 2);
                String lang = getPropertyInit(at, 0);
                if (lang != null) {
                    String lit = getPropertyInit(at, 1);
                    map.put(lang, lit);
                }
            }
        }
        catch (SemanticException e) {}
        return map;
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        boolean isNative = false;
        boolean classHasNativeRep = false;
        boolean defHasNativeImp = false;

        if (! (n instanceof ClassMember))
            return n;

        X10ClassDef cd = (X10ClassDef) context.currentClassDef();

        if (cd == null)
            return n;

        Map<String,String> nativeReps = getNativeRep(cd);
        Map<String,String> nativeImps = Collections.<String,String>emptyMap();

        if (! nativeReps.isEmpty())
            classHasNativeRep = true;

        if (n instanceof X10MethodDecl) {
            X10MethodDecl md = (X10MethodDecl) n;
            X10Def def = (X10Def) md.methodDef();
            if (md.flags().flags().isNative())
                isNative = true;
            nativeImps = getNativeImplForDef(def);
        }

        if (n instanceof X10ConstructorDecl) {
            X10ConstructorDecl xd = (X10ConstructorDecl) n;
            X10Def def = (X10Def) xd.constructorDef();
            if (xd.flags().flags().isNative())
                isNative = true;
            nativeImps = getNativeImplForDef(def);
        }

        if (n instanceof X10FieldDecl) {
            X10FieldDecl fd = (X10FieldDecl) n;
            X10Def def = (X10Def) fd.fieldDef();
            nativeImps = getNativeImplForDef(def);
        }

        if (n instanceof X10ClassDecl) {
            if (nativeReps.containsKey(theLanguage)) {
                {
                    Type t = cd.asType().superClass();
                    if (t != null) {
                        X10ClassType ct = (X10ClassType) Types.baseType(t);
                        X10ClassDef sd = ct.x10Def();
                        Map<String, String> map = getNativeRep(sd);
                        if (!map.containsKey(theLanguage)) {
                            throw new SemanticException("Class with NativeRep annotation may only extend classes with NativeRep.", n.position());
                        }
                    }
                }
            }
        }

        if (isNative && nativeImps.get(theLanguage) == null && n instanceof X10MethodDecl)
            throw new SemanticException("Native methods must have a @Native annotation for backend \"" + theLanguage + "\".", n.position());

        //        if (! nativeReps.isEmpty() && ! isNative && n instanceof X10MethodDecl) {
        //        	throw new SemanticException("Class with NativeRep annotation may contain only native methods.", n.position());
        //        }
        //        if (! nativeReps.isEmpty() && ! isNative && n instanceof X10ConstructorDecl) {
        //        	throw new SemanticException("Class with NativeRep annotation may contain only native constructors.", n.position());
        //        }
        //        if (! nativeImps.isEmpty() && ! isNative && n instanceof X10MethodDecl) {
        //        	throw new SemanticException("Method with Native annotation must be declared native.");
        //        }

        if (nativeReps.containsKey(theLanguage)) {
            if (! nativeImps.containsKey(theLanguage) && n instanceof X10MethodDecl) {
                X10MethodDecl md = (X10MethodDecl) n;
                X10MethodDef def = (X10MethodDef) md.methodDef();
                // HACK: ignore unary property methods -- there could be a native annotation on the property
                if (def.flags().isProperty() && def.formalTypes().size() == 0)
                    ;
                else if (md.name().toString().equals("typeName"))  // special case this synthetic method
                	;
                else
                    throw new SemanticException("Method  " + md + "\n of class " + def.container()+ " with NativeRep annotation must be annotated Native.", n.position());
            }
            if (! nativeImps.containsKey(theLanguage) && n instanceof X10FieldDecl) {
                throw new SemanticException("Fields of a class with NativeRep annotation must be annotated Native.", n.position());
            }
        }

        if (! nativeImps.isEmpty() && n instanceof X10ConstructorDecl) {
            throw new SemanticException("Constructors may not have Native annotations.");
        }

        return n;
    }
}
