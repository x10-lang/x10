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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.Special;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.CodeInstance;
import polyglot.types.LocalDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import x10.ast.AssignPropertyCall;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtStmt;
import x10.ast.AtStmt_c;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.DepParameterExpr;
import x10.ast.SettableAssign;
import x10.ast.TypeParamNode;
import x10.ast.X10ConstructorCall;
import x10.ast.X10Formal;
import x10.extension.X10Ext_c;
import x10.types.AsyncDef;
import x10.types.AtDef;
import x10.types.ClosureDef;
import x10.types.ClosureDef_c;
import x10.types.ClosureInstance;
import x10.types.EnvironmentCapture;
import x10.types.ParameterType;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10LocalDef;
import x10.types.X10LocalInstance;
import x10.types.MethodInstance;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

/**
 * A {@link NodeTransformer} that transforms the types stored
 * in the visited AST nodes.  Subclasses should override the
 * various transform* methods with TypeObject arguments.
 */
public class TypeTransformer extends NodeTransformer {

    protected Type transformType(Type type) {
        return type;
    }

    protected <T> Ref<T> transformRef(Ref<T> ref) {
        return ref;
    }
    
    protected ParameterType transformParameterType(ParameterType pt) {
        return pt;
    }

    protected X10LocalInstance transformLocalInstance(X10LocalInstance li) {
        return li;
    }

    protected X10FieldInstance transformFieldInstance(X10FieldInstance fi) {
        return fi;
    }

    protected MethodInstance transformMethodInstance(MethodInstance mi) {
        return mi;
    }

    protected X10ConstructorInstance transformConstructorInstance(X10ConstructorInstance ci) {
        return ci;
    }

    protected ClosureInstance transformClosureInstance(ClosureInstance ci) {
        return ci;
    }
    
    protected final CodeInstance<?> transformCodeInstance(CodeInstance<?> ci) {
        if (ci instanceof MethodInstance) {
            return transformMethodInstance((MethodInstance) ci);
        } else if (ci instanceof X10ConstructorInstance) {
            return transformConstructorInstance((X10ConstructorInstance) ci);
        } else if (ci instanceof ClosureInstance) {
            return transformClosureInstance((ClosureInstance) ci);
        }
        return ci;
    }

    protected final List<Type> transformTypeList(List<Type> l) {
        List<Type> res = l;
        List<Type> acc = new ArrayList<Type>();
        for (Type t : l) {
            Type xt = transformType(t);
            acc.add(xt);
            if (xt != t) {
                res = acc;
            }
        }
        return res;
    }

    @Override
    public Node transform(Node n, Node old, ContextVisitor v) {
        n = super.transform(n, old, v);
        if (n instanceof Term) {
            X10Ext_c ext = (X10Ext_c) n.ext();
            Set<LocalDef> initVals = ext.initVals;
            if (initVals != null) {
                ext = (X10Ext_c) ext.copy();
                ext.initVals = CollectionFactory.newHashSet();
                for (LocalDef ld : initVals) {
                    ext.initVals.add(getLocal((X10LocalDef) ld));
                }
                n = n.ext(ext);
            }
        }
        return n;
    }

    @Override
    protected TypeParamNode transform(TypeParamNode pn, TypeParamNode old) {
        ParameterType type = pn.type();
        ParameterType pt = transformParameterType(type);
        if (!pt.name().equals(type.name()))
            pn = pn.name(visitor().nodeFactory().Id(pn.position(), pt.name()));
        pn = pn.type(pt);
        return pn;
    }

    @Override
    protected TypeNode transform(TypeNode tn, TypeNode old) {
        Ref<? extends Type> tr = transformRef(tn.typeRef());
        Type t = Types.get(tr);
        Type rt = transformType(t);
        if (t != rt) {
            tr = remapRef(tr);
            ((Ref<Type>)tr).update(rt);
        }
        if (tn.typeRef() != tr) {
            tn = tn.typeRef(tr);
        }
        return tn;
    }

    @Override
    protected Expr transformExpr(Expr e, Expr old) {
        Type rt = transformType(e.type());
        if (e.type() != rt) {
            e = e.type(rt);
        }
        return super.transformExpr(e, old);
    }

    @Override
    protected Local transform(Local l, Local old) {
        X10LocalInstance li = (X10LocalInstance) l.localInstance();
        X10LocalDef ld = getLocal(li.x10Def());
        if (li.x10Def() != ld) {
            li = transformLocalInstance(((X10LocalInstance) ld.asInstance()));
        }
        if (l.localInstance() != li) {
            return l.localInstance(li);
        }
        return l;
    }

    @Override
    protected Field transform(Field f, Field old) {
        X10FieldInstance fi = transformFieldInstance((X10FieldInstance) f.fieldInstance());
        if (f.fieldInstance() != fi) {
            return f.fieldInstance(fi);
        }
        return f;
    }

    @Override
    protected Call transform(Call c, Call old) {
        MethodInstance mi = transformMethodInstance((MethodInstance) c.methodInstance());
        if (c.methodInstance() != mi) {
            return c.methodInstance(mi);
        }
        return c;
    }

    @Override
    protected New transform(New w, New old) {
        X10ConstructorInstance ci = transformConstructorInstance((X10ConstructorInstance) w.constructorInstance());
        if (w.constructorInstance() != ci) {
            return w.constructorInstance(ci);
        }
        return w;
    }

    @Override
    protected ClosureCall transform(ClosureCall c, ClosureCall old) {
        MethodInstance ci = transformMethodInstance(c.closureInstance());
        if (c.closureInstance() != ci) {
            return c.closureInstance(ci);
        }
        return c;
    }

    @Override
    protected SettableAssign transform(SettableAssign a, SettableAssign old) {
        MethodInstance mi = transformMethodInstance(a.methodInstance());
        MethodInstance ami = transformMethodInstance(a.applyMethodInstance());
        if (a.methodInstance() != mi || a.applyMethodInstance() != ami) {
            return a.methodInstance(mi).applyMethodInstance(ami);
        }
        return a;
    }

    @Override
    protected FieldAssign transform(FieldAssign f, FieldAssign old) {
        X10FieldInstance fi = transformFieldInstance((X10FieldInstance) f.fieldInstance());
        if (f.fieldInstance() != fi) {
            return f.fieldInstance(fi);
        }
        return f;
    }

    @Override
    protected Closure transform(Closure d, Closure old) {
        ClosureDef cd = d.closureDef();
        boolean sigChanged = d.returnType() != old.returnType();
        List<Formal> params = d.formals();
        List<Formal> oldParams = old.formals();
        for (int i = 0; i < params.size(); i++) {
            sigChanged |= params.get(i) != oldParams.get(i);
        }
        sigChanged |= d.guard() != old.guard();
        if (sigChanged) {
            DepParameterExpr g = d.guard();
            List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
            List<LocalDef> formalNames = new ArrayList<LocalDef>();
            for (int i = 0; i < params.size(); i++) {
                Formal p = params.get(i);
                argTypes.add(p.type().typeRef());
                formalNames.add(p.localDef());
            }
            TypeSystem xts = visitor().typeSystem();
            ClosureDef icd = (ClosureDef) cd.copy();
            icd.setReturnType(d.returnType().typeRef());
            icd.setFormalTypes(argTypes);
            icd.setFormalNames(formalNames);
            icd.setGuard(g == null ? null : g.valueConstraint());
            cd = icd;
        }
        List<VarInstance<? extends VarDef>> ce = transformCapturedEnvironment(cd.capturedEnvironment());
        if (cd.capturedEnvironment() != ce) {
            if (cd == d.closureDef()) {
                cd = (ClosureDef) cd.copy();
            }
            cd.setCapturedEnvironment(ce);
        }
        return d.closureDef(cd);
    }

    @Override
    protected Special transform(Special s, Special old) {
        return s;
    }

    @Override
    protected X10ConstructorCall transform(X10ConstructorCall c, X10ConstructorCall old) {
        X10ConstructorInstance ci = transformConstructorInstance(c.constructorInstance());
        if (c.constructorInstance() != ci) {
            return c.constructorInstance(ci);
        }
        return c;
    }

    @Override
    protected AssignPropertyCall transform(AssignPropertyCall p, AssignPropertyCall old) {
        List<X10FieldInstance> ps = p.properties();
        List<X10FieldInstance> acc = new ArrayList<X10FieldInstance>();
        for (X10FieldInstance fi : p.properties()) {
            X10FieldInstance xfi = transformFieldInstance(fi);
            acc.add(xfi);
            if (xfi != fi) {
                ps = acc;
            }
        }
        if (p.properties() != ps) {
            return p.properties(ps);
        }
        return p;
    }

    @Override
    protected AtStmt transform(AtStmt d, AtStmt old) {
        AtDef ad = d.atDef();
        List<VarInstance<? extends VarDef>> ce = transformCapturedEnvironment(ad.capturedEnvironment());
        if (ad.capturedEnvironment() != ce) {
            AtDef iad = (AtDef) ad.copy();
            iad.setCapturedEnvironment(ce);
            return d.atDef(iad);
        }
        return d;
    }

    @Override
    protected AtEach transform(AtEach a, AtEach old) {
        AtDef ad = a.atDef();
        List<VarInstance<? extends VarDef>> ce = transformCapturedEnvironment(ad.capturedEnvironment());
        if (ad.capturedEnvironment() != ce) {
            AtDef iad = (AtDef) ad.copy();
            iad.setCapturedEnvironment(ce);
            return a.atDef(iad);
        }
        return a;
    }
    
    @Override
    protected Async transform(Async a, Async old) {
        AsyncDef ad = a.asyncDef();
        List<VarInstance<? extends VarDef>> ce = transformCapturedEnvironment(ad.capturedEnvironment());
        if (ad.capturedEnvironment() != ce) {
            AsyncDef iad = (AsyncDef) ad.copy();
            iad.setCapturedEnvironment(ce);
            return a.asyncDef(iad);
        }
        return a;
    }

    private List<VarInstance<? extends VarDef>> transformCapturedEnvironment(List<VarInstance<? extends VarDef>> ce) {
        List<VarInstance<? extends VarDef>> ice = new ArrayList<VarInstance<? extends VarDef>>();
        List<VarInstance<? extends VarDef>> res = ce;
        for (VarInstance<?> vi : ce) {
            if (vi instanceof X10LocalInstance) {
                X10LocalInstance li = (X10LocalInstance) vi;
                X10LocalDef ld = getLocal(li.x10Def());
                if (li.x10Def() != ld) {
                    li = transformLocalInstance(((X10LocalInstance) ld.asInstance()));
                    res = ice;
                }
                ClosureDef_c.addCapturedVariable(ice, li);
            } else if (vi instanceof X10FieldInstance) {
                X10FieldInstance fi = transformFieldInstance((X10FieldInstance) vi);
                if (fi != vi) {
                    res = ice;
                }
                ClosureDef_c.addCapturedVariable(ice, fi);
            } else {
                ClosureDef_c.addCapturedVariable(ice, vi);
            }
        }
        return res;
    }

    @Override
    protected LocalDecl transform(LocalDecl d, LocalDecl old) {
        boolean sigChanged = d.type() != old.type(); // conservative compare detects changes in substructure
        if (sigChanged) {
            X10LocalDef ld = (X10LocalDef) d.localDef();
            TypeSystem xts = visitor().typeSystem();
            X10LocalDef ild = xts.localDef(ld.position(), ld.flags(), d.type().typeRef(), ld.name());
            if (ld.isAsyncInit()) ild.setAsyncInit(); // FIXME: we should really be using copy()
            mapLocal(ld, ild);
            return d.localDef(ild);
        }
        return d;
    }

    @Override
    protected X10Formal transform(X10Formal f, X10Formal old) {
        boolean sigChanged = f.type() != old.type(); // conservative compare detects changes in substructure
        if (sigChanged) {
            X10LocalDef ld = f.localDef();
            TypeSystem xts = visitor().typeSystem();
            X10LocalDef ild = xts.localDef(ld.position(), ld.flags(), f.type().typeRef(), ld.name());
            if (ld.isAsyncInit()) ild.setAsyncInit(); // FIXME: we should really be using copy()
            mapLocal(ld, ild);
            return f.localDef(ild);
        }
        return f;
    }

    private static final class IdentityRefKey {
        private Ref<?> v;
        public IdentityRefKey(Ref<?> v) { this.v = v; }
        public int hashCode() { return System.identityHashCode(v); }
        public boolean equals(Object o) {
            return o instanceof IdentityRefKey && ((IdentityRefKey)o).v == this.v;
        }
    }
    private final Map<IdentityRefKey, Ref<?>> refs = CollectionFactory.newHashMap();

    @SuppressWarnings("unchecked")
    protected <T> Ref<T> remapRef(Ref<T> ref) {
        if (ref == null) return null;
        IdentityRefKey key = new IdentityRefKey(ref);
        Ref<T> remappedRef = (Ref<T>) refs.get(key);
        if (remappedRef == null) {
            refs.put(key, remappedRef = Types.ref(ref.get()));
        }
        return remappedRef;
    }

    protected final Map<X10LocalDef, X10LocalDef> vars = CollectionFactory.newHashMap();

    protected void mapLocal(X10LocalDef def, X10LocalDef newDef) {
        vars.put(def, newDef);
    }

    protected X10LocalDef getLocal(X10LocalDef def) {
        X10LocalDef remappedDef = vars.get(def);
        return remappedDef != null ? remappedDef : def;
    }
}
