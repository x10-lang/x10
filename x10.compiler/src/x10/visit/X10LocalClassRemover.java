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
import java.util.HashMap;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
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
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SubtypeSet;
import polyglot.visit.InnerClassRemover;
import polyglot.visit.LocalClassRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import x10.ast.AssignPropertyCall;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.DepParameterExpr;
import x10.ast.SettableAssign;
import x10.ast.TypeParamNode;
import x10.ast.X10ClassDecl;
import x10.ast.X10NodeFactory;
import x10.types.ClosureDef;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context_c;
import x10.types.X10TypeMixin;
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

//    protected static TypeParamSubst subst(X10ClassType container) {
//        X10ClassDef def = (X10ClassDef) container.def();
//
//        List<Type> typeArgs = new ArrayList<Type>();
//        List<ParameterType> typeParams = new ArrayList<ParameterType>();
//
//        X10ClassDef outer = (X10ClassDef) Types.get(def.outer());
//        if (def.typeParameters().size() > 0) {
//            for (int i = 0; i < container.typeArguments().size(); i++) {
//                Type ti = container.typeArguments().get(i);
//                if (ti instanceof ParameterType) {
//                    ParameterType pt = (ParameterType) ti;
//                    if (Types.get(pt.def()) == def) {
//                        X10ClassType outerType = (X10ClassType) outer.asType();
//                        if (outerType.typeArguments().size() > 0) {
//                            int j = i - container.typeArguments().size() + outerType.typeArguments().size();
//                            assert 0 <= j && j < outerType.typeArguments().size() : "def " + def + "#" + def.typeParameters() + " outerType " + outerType + "#" + outerType.typeArguments() + " j " + j;
//                            Type otj = outerType.typeArguments().get(j);
//                            typeArgs.add(otj);
//                            typeParams.add(pt);
//                        }
//                    }
//                }
//            }
//        }
//
//        if (typeParams.size() > 0) {
//            X10TypeSystem ts = (X10TypeSystem) def.typeSystem();
//            TypeParamSubst subst = new TypeParamSubst(ts, typeArgs, typeParams);
//            return subst;
//        }
//
//        return null;
//    }
    
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
        private List<? extends Type> typeArgs;
        public X10ConstructorCallRewriter(List<FieldDef> fields, ClassDef ct, List<? extends Type> typeArgs) {
            super(fields, ct);
            this.typeArgs = typeArgs;
        }
            
        public Node leave(Node old, Node n, NodeVisitor v) {
            Node n_ = super.leave(old, n, v);
            
            if (n_ instanceof New) {
                New neu = (New) n_;
                ConstructorInstance ci = neu.constructorInstance();
                ConstructorDef nci = ci.def();
                X10ClassType container = (X10ClassType) Types.get(nci.container());
                
                if (container.def() == theLocalClass) {
                    X10ClassType type = (X10ClassType) X10TypeMixin.baseType(neu.objectType().type());
                    List<Type> ta = new ArrayList<Type>(type.typeArguments());
                    List<ParameterType> params = type.x10Def().typeParameters();
                    if (ta.equals(params)) {
                        ta.clear();
                        ta.addAll(typeArgs);
                    }
                    TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, ta, params);
                    X10ConstructorInstance xci = (X10ConstructorInstance) subst.reinstantiate(ci);
                    neu = neu.constructorInstance(xci);
                    neu = neu.objectType(nf.CanonicalTypeNode(neu.objectType().position(), subst.reinstantiate(type)));
                    neu = (New) neu.type(subst.reinstantiate(neu.type()));
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
    protected X10ConstructorInstance computeConstructorInstance(ConstructorDef cd) {
        ClassDef container = ((X10ClassType) Types.get(cd.container())).def();
        return (X10ConstructorInstance) cd.asInstance().container(computeConstructedType(container));
    }

    @Override
    protected X10ClassType computeConstructedType(ClassDef cd) {
        X10ClassDef def = (X10ClassDef) cd;
        X10ClassDef outer = (X10ClassDef) Types.get(def.outer());
        assert outer != null;
        X10ClassType t = ((X10ClassType)def.asType()).typeArguments(new ArrayList<Type>(outer.typeParameters()));
        return t;
    }

    @Override
    protected ClassDecl rewriteLocalClass(ClassDecl n, List<FieldDef> newFields) {
        if (n instanceof X10ClassDecl && n.classDef().isMember() && !n.classDef().flags().isStatic()) {
            X10ClassDecl cd = (X10ClassDecl) n;
            X10ClassDef def = (X10ClassDef) cd.classDef();
            X10ClassDef outer = (X10ClassDef) Types.get(def.outer());
            assert outer != null;

            List<TypeParamNode> params = new ArrayList<TypeParamNode>();

            for (int i = 0; i < outer.typeParameters().size(); i++) {
                ParameterType p = outer.typeParameters().get(i);
                ParameterType.Variance v = outer.variances().get(i);

                X10NodeFactory xnf = (X10NodeFactory) nf;
                TypeParamNode pn = xnf.TypeParamNode(n.position(), xnf.Id(n.position(), Name.makeFresh(p.name())), v);
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
                TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, def.typeParameters(), outer.typeParameters());
                cd = rewriteTypeParams(subst, cd, outer);
            }

            n = cd.body((ClassBody) rewriteConstructorCalls(cd.body(), def, newFields, def.typeParameters()));
        } else {
            n = n.body((ClassBody) rewriteConstructorCalls(n.body(), n.classDef(), newFields));
        }

        return InnerClassRemover.addFieldsToClass(n, newFields, ts, nf, false);
    }

    private X10ClassDecl rewriteTypeParams(final TypeParamSubst subst, X10ClassDecl cd, final X10ClassDef outer) {
        return (X10ClassDecl) cd.visit(new NodeVisitor() {
            final HashMap<LocalDef, LocalDef> vars = new HashMap<LocalDef, LocalDef>();
            // TODO: integrate this with instantiate() in the Inliner
            @Override
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof TypeParamNode) {
                    TypeParamNode pn = (TypeParamNode) n;
                    if (pn.type().def().get() == outer) {
                        ParameterType pt = subst.reinstantiate(pn.type());
                        if (pt == pn.type()) {
                            assert false : "parameter " + pt + " not found in " + outer;
                        }
                        pn = pn.name(nf.Id(pn.position(), pt.name()));
                        pn = pn.type(pt);
                    }
                    return pn;
                }
                if (n instanceof TypeNode) {
                    TypeNode tn = (TypeNode) n;
                    Type t = tn.type();
                    Type t2 = subst.reinstantiateType(t);
                    if (t != t2)
                        ((Ref<Type>) tn.typeRef()).update(t2);
                    return tn;
                }
                if (n instanceof LocalDecl) {
                    LocalDecl d = (LocalDecl) n;
                    boolean sigChanged = d.type() != ((LocalDecl) old).type(); // conservative compare detects changes in substructure
                    if (sigChanged) {
                        LocalDef ld = d.localDef();
                        Name name = ld.name();
                        LocalDef ild = ts.localDef(ld.position(), ld.flags(), d.type().typeRef(), name);
                        vars.put(ld, ild);
                        d = d.localDef(ild);
                    }
                    return d;
                }
                if (n instanceof ConstructorCall) {
                    ConstructorCall c = (ConstructorCall) n;
                    return c.constructorInstance(subst.reinstantiate(c.constructorInstance()));
                }
                if (n instanceof Expr) {
                    Expr e = (Expr) n;
                    Type t = e.type();
                    t = subst.reinstantiateType(t);
                    e = e.type(t);
                    if (n instanceof Call) {
                        Call c = (Call) n;
                        return c.methodInstance(subst.reinstantiate(c.methodInstance()));
                    }
                    if (n instanceof New) {
                        New x = (New) n;
                        return x.constructorInstance(subst.reinstantiate(x.constructorInstance()));
                    }
                    if (n instanceof ClosureCall) {
                        ClosureCall c = (ClosureCall) n;
                        return c.closureInstance(subst.reinstantiate(c.closureInstance()));
                    }
                    if (n instanceof Field) {
                        Field f = (Field) n;
                        return f.fieldInstance(subst.reinstantiate(f.fieldInstance()));
                    }
                    if (n instanceof Local) {
                        Local l = (Local) n;
                        return l.localInstance(subst.reinstantiate(getLocal(l.localInstance().def()).asInstance()));
                    }
                    if (n instanceof FieldAssign) {
                        FieldAssign f = (FieldAssign) n;
                        return f.fieldInstance(subst.reinstantiate(f.fieldInstance()));
                    }
                    if (n instanceof Closure) {
                        Closure d = (Closure) n;
                        boolean sigChanged = d.returnType() != ((Closure) old).returnType();
                        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
                        List<LocalDef> formalNames = new ArrayList<LocalDef>();
                        List<Formal> params = d.formals();
                        List<Formal> oldParams = ((Closure) old).formals();
                        for (int i = 0; i < params.size(); i++) {
                            Formal p = params.get(i);
                            sigChanged |= p != oldParams.get(i);
                            argTypes.add(p.type().typeRef());
                            formalNames.add(p.localDef());
                        }
                        sigChanged |= d.guard() != ((Closure) old).guard();
                        List<Ref <? extends Type>> excTypes = new ArrayList<Ref<? extends Type>>();
                        SubtypeSet excs = d.exceptions() == null ? new SubtypeSet(typeSystem()) : d.exceptions();
                        SubtypeSet oldExcs = ((Closure) old).exceptions();
                        if (null != excs) {
                            for (Type et : excs) {
                                sigChanged |= !oldExcs.contains(et);
                                excTypes.add(Types.ref(et));
                            }
                        }
                        if (sigChanged) {
                            ClosureDef cd = (ClosureDef) d.closureDef();
                            DepParameterExpr g = d.guard();
                            ClosureDef icd = ((X10TypeSystem) ts).closureDef(cd.position(), cd.typeContainer(), cd.methodContainer(),
                                                         d.returnType().typeRef(),
                                                         argTypes, cd.thisVar(), formalNames,
                                                         g == null ? null : g.valueConstraint(),
                                                         excTypes, null);
                            return d.closureDef(icd);
                        }
                        return d;
                    }
                    if (n instanceof AssignPropertyCall) {
                        AssignPropertyCall a = (AssignPropertyCall) n;
                        return a.properties(subst.reinstantiate(a.properties()));
                    }
                    if (n instanceof SettableAssign) {
                        SettableAssign a = (SettableAssign) n;
                        return a.methodInstance(subst.reinstantiate(a.methodInstance()))
                                .applyMethodInstance(subst.reinstantiate(a.applyMethodInstance()));
                    }
                    return e;
                }
                return n;
            }
            private LocalDef getLocal(LocalDef def) {
                LocalDef remappedDef = vars.get(def);
                return remappedDef != null ? remappedDef : def;
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
    protected Node rewriteConstructorCalls(Node s, final ClassDef ct, final List<FieldDef> fields) {
        X10ClassDef def = (X10ClassDef) ct;
        X10ClassDef outer = (X10ClassDef) Types.get(def.outer());
        return rewriteConstructorCalls(s, ct, fields, outer.typeParameters());
    }
    
    protected Node rewriteConstructorCalls(Node s, final ClassDef ct, final List<FieldDef> fields, List<? extends Type> typeArgs) {
        Node r = s.visit(new X10ConstructorCallRewriter(fields, ct, typeArgs));
        return r;
    }
}
