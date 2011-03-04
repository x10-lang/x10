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
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.types.LocalDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.ast.AssignPropertyCall;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.DepParameterExpr;
import x10.ast.SettableAssign;
import x10.ast.TypeParamNode;
import x10.ast.X10ConstructorCall;
import x10.ast.X10Formal;
import x10.types.ClosureDef;
import x10.types.ParameterType;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10LocalDef;
import x10.types.X10LocalInstance;
import x10.types.X10MethodInstance;
import x10.types.X10TypeSystem;

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

    protected X10MethodInstance transformMethodInstance(X10MethodInstance mi) {
        return mi;
    }

    protected X10ConstructorInstance transformConstructorInstance(X10ConstructorInstance ci) {
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
        X10MethodInstance mi = transformMethodInstance((X10MethodInstance) c.methodInstance());
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
        X10MethodInstance ci = transformMethodInstance(c.closureInstance());
        if (c.closureInstance() != ci) {
            return c.closureInstance(ci);
        }
        return c;
    }

    @Override
    protected SettableAssign transform(SettableAssign a, SettableAssign old) {
        X10MethodInstance mi = transformMethodInstance(a.methodInstance());
        X10MethodInstance ami = transformMethodInstance(a.applyMethodInstance());
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
        boolean sigChanged = d.returnType() != old.returnType();
        List<Formal> params = d.formals();
        List<Formal> oldParams = old.formals();
        for (int i = 0; i < params.size(); i++) {
            sigChanged |= params.get(i) != oldParams.get(i);
        }
        sigChanged |= d.guard() != old.guard();
        if (sigChanged) {
            ClosureDef cd = d.closureDef();
            DepParameterExpr g = d.guard();
            List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
            List<LocalDef> formalNames = new ArrayList<LocalDef>();
            for (int i = 0; i < params.size(); i++) {
                Formal p = params.get(i);
                argTypes.add(p.type().typeRef());
                formalNames.add(p.localDef());
            }
            X10TypeSystem xts = (X10TypeSystem) visitor().typeSystem();
            ClosureDef icd = xts.closureDef(cd.position(), cd.typeContainer(), cd.methodContainer(),
                                            d.returnType().typeRef(),
                                            argTypes, cd.thisVar(), formalNames,
                                            g == null ? null : g.valueConstraint(),
                                            null);
            return d.closureDef(icd);
        }
        return d;
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
    protected LocalDecl transform(LocalDecl d, LocalDecl old) {
        boolean sigChanged = d.type() != old.type(); // conservative compare detects changes in substructure
        if (sigChanged) {
            X10LocalDef ld = (X10LocalDef) d.localDef();
            X10TypeSystem xts = (X10TypeSystem) visitor().typeSystem();
            X10LocalDef ild = xts.localDef(ld.position(), ld.flags(), d.type().typeRef(), ld.name());
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
            X10TypeSystem xts = (X10TypeSystem) visitor().typeSystem();
            X10LocalDef ild = xts.localDef(ld.position(), ld.flags(), f.type().typeRef(), ld.name());
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
    private final HashMap<IdentityRefKey, Ref<?>> refs = new HashMap<IdentityRefKey, Ref<?>>();

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

    protected final HashMap<X10LocalDef, X10LocalDef> vars = new HashMap<X10LocalDef, X10LocalDef>();

    protected void mapLocal(X10LocalDef def, X10LocalDef newDef) {
        vars.put(def, newDef);
    }

    protected X10LocalDef getLocal(X10LocalDef def) {
        X10LocalDef remappedDef = vars.get(def);
        return remappedDef != null ? remappedDef : def;
    }
}
