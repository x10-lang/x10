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

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ClassDecl;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.CodeDef;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.LocalClassRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import x10.ast.TypeParamNode;
import x10.ast.X10ClassDecl;
import x10.ast.X10NodeFactory;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context_c;
import x10.types.X10TypeSystem;

public class X10LocalClassRemover extends LocalClassRemover {

    /**
     * The type to be extended when translating an anonymous class that
     * implements an interface.
     */
    protected TypeNode defaultSuperType(Position pos) {
        X10TypeSystem ts = (X10TypeSystem) this.ts;
        return nf.CanonicalTypeNode(pos, ts.Object());
    }

    protected static TypeParamSubst subst(X10ClassType container) {
        X10ClassDef def = (X10ClassDef) container.def();

        List<Type> typeArgs = new ArrayList<Type>();
        List<ParameterType> typeParams = new ArrayList<ParameterType>();

        X10ClassDef outer = (X10ClassDef) Types.get(def.outer());
        if (def.typeParameters().size() > 0) {
            for (int i = 0; i < container.typeArguments().size(); i++) {
                Type ti = container.typeArguments().get(i);
                if (ti instanceof ParameterType) {
                    ParameterType pt = (ParameterType) ti;
                    if (Types.get(pt.def()) == def) {
                        X10ClassType outerType = (X10ClassType) outer.asType();
                        if (outerType.typeArguments().size() > 0) {
                            int j = i - container.typeArguments().size() + outerType.typeArguments().size();
                            assert 0 <= j && j < outerType.typeArguments().size() : "def " + def + "#" + def.typeParameters() + " outerType " + outerType + "#" + outerType.typeArguments() + " j " + j;
                            Type otj = outerType.typeArguments().get(j);
                            typeArgs.add(otj);
                            typeParams.add(pt);
                        }
                    }
                }
            }
        }

        if (typeParams.size() > 0) {
            X10TypeSystem ts = (X10TypeSystem) def.typeSystem();
            TypeParamSubst subst = new TypeParamSubst(ts, typeArgs, typeParams);
            return subst;
        }

        return null;
    }
    
    protected static TypeParamSubst inverseSubst(X10ClassType container) {
        X10ClassDef def = (X10ClassDef) container.def();
        
        List<Type> typeArgs = new ArrayList<Type>();
        List<ParameterType> typeParams = new ArrayList<ParameterType>();
        
        X10ClassDef outer = (X10ClassDef) Types.get(def.outer());
        if (def.typeParameters().size() > 0) {
            for (int i = 0; i < container.typeArguments().size(); i++) {
                Type ti = container.typeArguments().get(i);
                if (ti instanceof ParameterType) {
                    ParameterType pt = (ParameterType) ti;
                    if (Types.get(pt.def()) == def) {
                        X10ClassType outerType = (X10ClassType) outer.asType();
                        if (outerType.typeArguments().size() > 0) {
                            int j = i - container.typeArguments().size() + outerType.typeArguments().size();
                            assert 0 <= j && j < outerType.typeArguments().size() : "def " + def + "#" + def.typeParameters() + " outerType " + outerType + "#" + outerType.typeArguments() + " j " + j;
                            Type otj = outerType.typeArguments().get(j);
                            if (otj instanceof ParameterType) {
                                typeParams.add((ParameterType) otj);
                                typeArgs.add(pt);
                            }
                        }
                    }
                }
            }
        }
        
        if (typeArgs.size() > 0) {
            X10TypeSystem ts = (X10TypeSystem) def.typeSystem();
            TypeParamSubst subst = new TypeParamSubst(ts, typeArgs, typeParams);
            return subst;
        }
        
        return null;
    }

    protected class X10ConstructorCallRewriter extends ConstructorCallRewriter {
        public X10ConstructorCallRewriter(List<FieldDef> fields, ClassDef ct) {
            super(fields, ct);
        }
            
        public Node leave(Node old, Node n, NodeVisitor v) {
            Node n_ = super.leave(old, n, v);
            
            if (n_ instanceof New) {
                New neu = (New) n_;
                ConstructorInstance ci = neu.constructorInstance();
                ConstructorDef nci = ci.def();
                X10ClassType container = (X10ClassType) Types.get(nci.container());
                
                // HACK
                // This is a hack, we should parameterize the local class when created and not have to add parameters here.
                if (container.def() == theLocalClass) {
                    TypeParamSubst subst = subst(container);
                    if (subst != null) {
                        X10ConstructorInstance xci = (X10ConstructorInstance) subst.reinstantiate(ci);
                        neu = neu.constructorInstance(xci);
                        neu = neu.objectType(nf.CanonicalTypeNode(neu.objectType().position(), subst.reinstantiate(neu.objectType().type())));
                        neu = (New) neu.type(subst.reinstantiate(neu.type()));
                    }
                    // FIX:XTENLANG-949 (for mismatch between neu.argument and neu.ci.formalTypes)
                    if (neu.arguments().size() > ci.formalTypes().size()) {
                        List<Type> newFormalTypes = new ArrayList<Type>();
                        for (Expr arg : neu.arguments()) {
                            newFormalTypes.add(arg.type());
                        }
                        neu = neu.constructorInstance(ci.formalTypes(newFormalTypes));
                    }
                }
                
                return neu;
            }
            
            return n_;
        }
    }

    public X10LocalClassRemover(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    @Override
    protected ClassDecl rewriteLocalClass(ClassDecl n, List<FieldDef> newFields) {
        if (n instanceof X10ClassDecl) {
            X10ClassDecl cd = (X10ClassDecl) n;

            if (cd.classDef().isMember() && ! cd.classDef().flags().isStatic()) {
                X10ClassDef def = (X10ClassDef) cd.classDef();
                X10ClassDef outer = (X10ClassDef) Types.get(def.outer());
                assert outer != null;

                List<TypeParamNode> params = new ArrayList<TypeParamNode>();

                for (int i = 0; i < outer.typeParameters().size(); i++) {
                    ParameterType p = outer.typeParameters().get(i);
                    ParameterType.Variance v = outer.variances().get(i);

                    X10NodeFactory xnf = (X10NodeFactory) nf;
                    TypeParamNode pn = xnf.TypeParamNode(n.position(), ! p.isHere(), xnf.Id(n.position(), Name.makeFresh(p.name())), v);
                    TypeBuilder tb = new TypeBuilder(job, ts, nf);
                    try {
                        tb = tb.pushClass(outer);
                        tb = tb.pushClass(def);
                        pn = (TypeParamNode) pn.del().buildTypes(tb);
                        def.addTypeParameter(pn.type(), v);
                    }
                    catch (SemanticException e) {
                        throw new InternalCompilerError(e);
                    }
                    params.add(pn);
                }

                if (! params.isEmpty()) {
                    cd = cd.typeParameters(params);
                    cd = rewriteTypeParams(cd, outer);
                }

                n = cd;
            }
        }

        return super.rewriteLocalClass(n, newFields);
    }

    private X10ClassDecl rewriteTypeParams(final X10ClassDecl cd, final X10ClassDef outer) {
        return (X10ClassDecl) cd.visit(new NodeVisitor() { 
            @Override
            public Node leave(Node old, Node n, NodeVisitor v) {
                X10ClassDef xcd = (X10ClassDef) cd.classDef();
                if (n instanceof TypeParamNode) {
                    TypeParamNode pn = (TypeParamNode) n;
                    if (pn.type().def().get() == outer) {
                        ParameterType pt = remapType(pn.type(), xcd);
                        pn = pn.name(nf.Id(pn.position(), pt.name()));
                        pn = pn.type(pt);
                    }
                    return pn;
                }
                if (n instanceof TypeNode) {
                    TypeNode tn = (TypeNode) n;
                    Type t = tn.type();
                    Type t2 = new TypeParamSubst((X10TypeSystem) ts, (List) xcd.typeParameters(), (List) outer.typeParameters()).reinstantiateType(t);
                    if (t != t2)
                        ((Ref<Type>) tn.typeRef()).update(t2);
                    return tn;
                }
                if (n instanceof Expr) {
                    Expr e = (Expr) n;
                    Type t = e.type();
                    t = new TypeParamSubst((X10TypeSystem) ts, (List) xcd.typeParameters(), (List) outer.typeParameters()).reinstantiateType(t);
                    return e;
                }
                return n;
            }

            private ParameterType remapType(ParameterType type, X10ClassDef cd) {
                X10ClassDef outer = (X10ClassDef) type.def().get();
                for (int i = 0; i < outer.typeParameters().size(); i++) {
                    ParameterType p = outer.typeParameters().get(i);
                    if (p.name().equals(type.name()))
                        return cd.typeParameters().get(i);
                }
                assert false : "parameter " + type + " not found in " + outer;
                return null;
            }  
        });
    }

    @Override
    protected boolean isLocal(Context c, Name name) {
        X10Context_c xcon = (X10Context_c)c;
        CodeDef ci = xcon.definingCodeDef(name);
        if (ci == null) return false;
        while (c != null) {
            CodeDef curr = c.currentCode();
            if (curr == ci) return true;
            // Allow closures, asyncs
            if (curr instanceof MethodDef && ((MethodDef) curr).name().equals(Name.make("$dummyAsync$")))
                ;
            else {
                // FIX:XTENLANG-1159
                return xcon.isValInScopeInClass(name);
            }
            c = c.pop();
        }
        // FIX:XTENLANG-1159
        return xcon.isValInScopeInClass(name);
    }
    
    @Override
    protected
    Node rewriteConstructorCalls(Node s, final ClassDef ct, final List<FieldDef> fields) {
        Node r = s.visit(new X10ConstructorCallRewriter(fields, ct));
        return r;
    }
}