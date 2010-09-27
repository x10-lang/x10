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
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.CodeDef;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SubtypeSet;
import polyglot.visit.ContextVisitor;
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
import x10.ast.X10ConstructorCall;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10FieldDecl;
import x10.ast.X10Formal;
import x10.ast.X10MethodDecl;
import x10.ast.X10NodeFactory;
import x10.types.ClosureDef;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context_c;
import x10.types.X10FieldInstance;
import x10.types.X10MethodDef;
import x10.types.X10MethodInstance;
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
                    if (!params.isEmpty() && (ta.equals(params) || ta.isEmpty())) {
                        ta.clear();
                        ta.addAll(typeArgs);
                        assert (typeArgs.size() == params.size());
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
                    tb.pushCode(context.currentCode());
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
                cd = rewriteTypeParams(subst, cd);
            }

            n = cd.body((ClassBody) rewriteConstructorCalls(cd.body(), def, newFields, def.typeParameters()));
        } else {
            n = n.body((ClassBody) rewriteConstructorCalls(n.body(), n.classDef(), newFields));
        }

        return InnerClassRemover.addFieldsToClass(n, newFields, ts, nf, false);
    }

    private X10ClassDecl rewriteTypeParams(final TypeParamSubst subst, X10ClassDecl cd) {
        return (X10ClassDecl) cd.visit(new TypeTransformingVisitor(job, ts, nf, subst).context(context));
    }

    public static class TypeTransformingVisitor extends ContextVisitor {
        protected final TypeParamSubst subst;

        protected TypeTransformingVisitor(Job job, TypeSystem ts, NodeFactory nf, TypeParamSubst subst) {
            super(job, ts, nf);
            this.subst = subst;
        }

        @Override
        protected Node leaveCall(Node old, Node n, NodeVisitor v) {
            if (n instanceof TypeParamNode) {
                return transform((TypeParamNode) n, (TypeParamNode) old);
            } else if (n instanceof TypeNode) {
                return transform((TypeNode) n, (TypeNode) old);
            } else if (n instanceof X10Formal) {
                return transform((X10Formal) n, (X10Formal) old);
            } else if (n instanceof X10ClassDecl) {
                return transform((X10ClassDecl) n, (X10ClassDecl) old);
            } else if (n instanceof X10FieldDecl) {
                return transform((X10FieldDecl) n, (X10FieldDecl) old);
            } else if (n instanceof X10ConstructorDecl) {
                return transform((X10ConstructorDecl) n, (X10ConstructorDecl) old);
            } else if (n instanceof X10MethodDecl) {
                return transform((X10MethodDecl) n, (X10MethodDecl) old);
            } else if (n instanceof Expr) {
                return transformExpr((Expr) n, (Expr) old);
            } else if (n instanceof Stmt) {
                return transformStmt((Stmt) n, (Stmt) old);
            }
            return transformNode(n, old);
        }

        protected Node transformNode(Node n, Node old) {
            return n;
        }

        protected TypeParamNode transform(TypeParamNode pn, TypeParamNode old) {
            ParameterType type = pn.type();
            ParameterType pt = subst.reinstantiate(type);
            if (pt == type) {
                assert false : "No substitution found for type parameter " + pt;
            }
            if (!pt.name().equals(type.name()))
                pn = pn.name(nf.Id(pn.position(), pt.name()));
            pn = pn.type(pt);
            return pn;
        }

        protected TypeNode transform(TypeNode tn, TypeNode old) {
            Type rt = subst.reinstantiate(tn.type());
            if (tn.type() != rt) {
                tn = tn.typeRef(Types.ref(rt));
            }
            return tn;
        }

        protected Expr transformExpr(Expr e, Expr old) {
            Type rt = subst.reinstantiate(e.type());
            if (e.type() != rt) {
                e = e.type(rt);
            }
            if (e instanceof Local) {
                return transform((Local) e, (Local) old);
            } else if (e instanceof Field) {
                return transform((Field) e, (Field) old);
            } else if (e instanceof Call) {
                return transform((Call) e, (Call) old);
            } else if (e instanceof New) {
                return transform((New) e, (New) old);
            } else if (e instanceof ClosureCall) {
                return transform((ClosureCall) e, (ClosureCall) old);
            } else if (e instanceof SettableAssign) {
                return transform((SettableAssign) e, (SettableAssign) old);
            } else if (e instanceof FieldAssign) {
                return transform((FieldAssign) e, (FieldAssign) old);
            } else if (e instanceof Closure) {
                return transform((Closure) e, (Closure) old);
            } else if (e instanceof Special) {
                return transform((Special) e, (Special) old);
            }
            return transform(e, old);
        }

        protected Expr transform(Expr e, Expr old) {
            return e;
        }

        protected Local transform(Local l, Local old) {
            LocalDef ld = getLocal(l.localInstance().def());
            if (l.localInstance().def() != ld) {
                return l.localInstance(subst.reinstantiate(ld.asInstance()));
            }
            return l;
        }

        protected Field transform(Field f, Field old) {
            FieldInstance fi = subst.reinstantiate(f.fieldInstance());
            if (f.fieldInstance() != fi) {
                return f.fieldInstance(fi);
            }
            return f;
        }

        protected Call transform(Call c, Call old) {
            MethodInstance mi = subst.reinstantiate(c.methodInstance());
            if (c.methodInstance() != mi) {
                return c.methodInstance(mi);
            }
            return c;
        }

        protected New transform(New w, New old) {
            ConstructorInstance ci = subst.reinstantiate(w.constructorInstance());
            if (w.constructorInstance() != ci) {
                return w.constructorInstance(ci);
            }
            return w;
        }

        protected ClosureCall transform(ClosureCall c, ClosureCall old) {
            X10MethodInstance ci = subst.reinstantiate(c.closureInstance());
            if (c.closureInstance() != ci) {
                return c.closureInstance(ci);
            }
            return c;
        }

        protected SettableAssign transform(SettableAssign a, SettableAssign old) {
            X10MethodInstance mi = subst.reinstantiate(a.methodInstance());
            X10MethodInstance ami = subst.reinstantiate(a.applyMethodInstance());
            if (a.methodInstance() != mi || a.applyMethodInstance() != ami) {
                return a.methodInstance(mi).applyMethodInstance(ami);
            }
            return a;
        }

        protected FieldAssign transform(FieldAssign f, FieldAssign old) {
            FieldInstance fi = subst.reinstantiate(f.fieldInstance());
            if (f.fieldInstance() != fi) {
                return f.fieldInstance(fi);
            }
            return f;
        }

        protected Closure transform(Closure d, Closure old) {
            boolean sigChanged = d.returnType() != old.returnType();
            List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
            List<LocalDef> formalNames = new ArrayList<LocalDef>();
            List<Formal> params = d.formals();
            List<Formal> oldParams = old.formals();
            for (int i = 0; i < params.size(); i++) {
                Formal p = params.get(i);
                sigChanged |= p != oldParams.get(i);
                argTypes.add(p.type().typeRef());
                formalNames.add(p.localDef());
            }
            sigChanged |= d.guard() != old.guard();
            List<Ref <? extends Type>> excTypes = new ArrayList<Ref<? extends Type>>();
            if (sigChanged) {
                ClosureDef cd = (ClosureDef) d.closureDef();
                DepParameterExpr g = d.guard();
                X10TypeSystem xts = (X10TypeSystem) ts;
                ClosureDef icd = xts.closureDef(cd.position(), cd.typeContainer(), cd.methodContainer(),
                                                d.returnType().typeRef(),
                                                argTypes, cd.thisVar(), formalNames,
                                                g == null ? null : g.valueConstraint(),
                                                null);
                return d.closureDef(icd);
            }
            return d;
        }

        protected Special transform(Special s, Special old) {
            return s;
        }

        protected Stmt transformStmt(Stmt s, Stmt old) {
            if (s instanceof X10ConstructorCall) {
                return transform((X10ConstructorCall) s, (X10ConstructorCall) old);
            } else if (s instanceof AssignPropertyCall) {
                return transform((AssignPropertyCall) s, (AssignPropertyCall) old);
            } else if (s instanceof LocalDecl) {
                return transform((LocalDecl) s, (LocalDecl) old);
            }
            return transform(s, old);
        }

        protected Stmt transform(Stmt s, Stmt old) {
            return s;
        }

        protected X10ConstructorCall transform(X10ConstructorCall c, X10ConstructorCall old) {
            ConstructorInstance ci = subst.reinstantiate(c.constructorInstance());
            if (c.constructorInstance() != ci) {
                return c.constructorInstance(ci);
            }
            return c;
        }

        protected AssignPropertyCall transform(AssignPropertyCall p, AssignPropertyCall old) {
            List<X10FieldInstance> ps = subst.reinstantiate(p.properties());
            if (p.properties() != ps) {
                return p.properties(ps);
            }
            return p;
        }

        protected LocalDecl transform(LocalDecl d, LocalDecl old) {
            boolean sigChanged = d.type() != old.type(); // conservative compare detects changes in substructure
            if (sigChanged) {
                LocalDef ld = d.localDef();
                X10TypeSystem xts = (X10TypeSystem) ts;
                LocalDef ild = xts.localDef(ld.position(), ld.flags(), d.type().typeRef(), ld.name());
                mapLocal(ld, ild);
                return d.localDef(ild);
            }
            return d;
        }

        protected X10Formal transform(X10Formal f, X10Formal old) {
            boolean sigChanged = f.type() != old.type(); // conservative compare detects changes in substructure
            if (sigChanged) {
                LocalDef ld = f.localDef();
                X10TypeSystem xts = (X10TypeSystem) ts;
                LocalDef ild = xts.localDef(ld.position(), ld.flags(), f.type().typeRef(), ld.name());
                mapLocal(ld, ild);
                return f.localDef(ild);
            }
            return f;
        }

        protected X10ClassDecl transform(X10ClassDecl d, X10ClassDecl old) {
            return d;
        }

        protected X10FieldDecl transform(X10FieldDecl d, X10FieldDecl old) {
            return d;
        }

        protected X10ConstructorDecl transform(X10ConstructorDecl d, X10ConstructorDecl old) {
            return d;
        }

        protected X10MethodDecl transform(X10MethodDecl d, X10MethodDecl old) {
            return d;
        }

        private final HashMap<LocalDef, LocalDef> vars = new HashMap<LocalDef, LocalDef>();

        protected void mapLocal(LocalDef def, LocalDef newDef) {
            vars.put(def, newDef);
        }

        protected LocalDef getLocal(LocalDef def) {
            LocalDef remappedDef = vars.get(def);
            return remappedDef != null ? remappedDef : def;
        }
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
