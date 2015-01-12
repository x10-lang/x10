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
import polyglot.ast.VarDecl;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.CodeInstance;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
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
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.extension.X10Ext_c;
import x10.types.AsyncDef;
import x10.types.AtDef;
import x10.types.ClosureDef;
import x10.types.ClosureDef_c;
import x10.types.ClosureInstance;
import x10.types.ClosureType;
import x10.types.ConstrainedType;
import x10.types.EnvironmentCapture;
import x10.types.FunctionType;
import x10.types.ParameterType;
import x10.types.ThisDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10LocalDef;
import x10.types.X10LocalInstance;
import x10.types.MethodInstance;
import x10.types.X10MemberDef;
import x10.types.X10ParsedClassType;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CLocal;
import x10.types.constraints.ConstraintManager;

import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import x10.util.CollectionFactory;

/**
 * A {@link NodeTransformer} that transforms the types stored
 * in the visited AST nodes.  Subclasses should override the
 * various transform* methods with TypeObject arguments.
 */
public class TypeTransformer extends NodeTransformer {

    protected Type transformType(Type type) {
        Type nt = transformTypeRecursively(type);
        //if (nt != null && nt.toString().contains("!!!")) { // validation
        //    throw new InternalCompilerError("Type was not fully transformed: "+nt, nt.position());
        //}
        return nt;
    }

    protected CConstraint transformConstraint(CConstraint c) {
        if (c == null)
            return null;
        VarDef currentLocal = this.visitor().context().varWhoseTypeIsBeingElaborated();
        List<XVar> oldvars = new ArrayList<XVar>();
        List<XVar> newvars = new ArrayList<XVar>();
        for (XVar v : c.vars()) {
            if (v instanceof CLocal) {
                CLocal l = (CLocal) v;
                X10LocalDef ld = l.name();
                X10LocalDef newld = vars.get(ld);
                if (ld == currentLocal) { // we are in the declaration for this variable
                    assert (newld == null);
                    Type rt = Types.get(ld.type());
                    TypeSystem ts = rt.typeSystem();
                    newld = copyLocalDef(ld);
                    mapLocal(ld, newld); // have to do this first, else get infinite recursion
                    mapLocal(newld, newld); // just in case some client substitutes first
                    Type newrt = transformType(rt);
                    newld.setType(Types.ref(newrt));
                }
                if (newld == null || newld == ld) continue;
                oldvars.add(v);
                //if (!l.s.endsWith("!!!")) l.s+="!!!"; // validation
                //newvars.add(ConstraintManager.getConstraintSystem().makeLocal(newld, newld.name().toString())); // validation
                newvars.add(ConstraintManager.getConstraintSystem().makeLocal(newld, v.toString()));
            }
        }
        try {
            CConstraint newC = c.substitute(newvars.toArray(new XTerm[0]), oldvars.toArray(new XVar[0]));
            if (newC != c && (!newC.entails(c) || !c.entails(newC))) {
                c = newC;
            }
        } catch (XFailure e) {
            throw new InternalCompilerError("Unexpected failure while transforming constraint: "+c);
        }
        return c;
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
        if (l == null)
            return null;
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

    private Type transformTypeRecursively(Type t) {
        if (t instanceof ConstrainedType) {
            ConstrainedType ct = (ConstrainedType) t;
            Type bt = Types.get(ct.baseType());
            Type newbt = transformTypeRecursively(bt);
            CConstraint constraint = ct.getRealXClause();
            CConstraint newConstraint = transformConstraint(constraint);
            if (newbt != bt || newConstraint != constraint) {
                t = Types.xclause(newbt, newConstraint);
            }
        } else if (t instanceof ClosureType) { // order matters!
            ClosureType ft = (ClosureType) t; // TODO
            X10ClassType ct = (X10ClassType) ft.outer();
            FunctionType fi = ft.functionInterface();
            CodeInstance<?> cm = ft.methodContainer();
            X10ClassType nct = (X10ClassType) transformType(ct);
            FunctionType nfi = (FunctionType) transformTypeRecursively(fi);
            CodeInstance<?> ncm = transformCodeInstance(cm);
            if (nct != ct || nfi != fi || ncm != cm) {
                List<Ref<? extends Type>> fts = new ArrayList<Ref<? extends Type>>();
                List<ParameterType> tps = Collections.<ParameterType>emptyList();
                for (Type a : nfi.argumentTypes()) {
                    fts.add(Types.ref(a));
                }
                List<LocalDef> nfns = new ArrayList<LocalDef>();
                for (LocalInstance li : nfi.formalNames()) {
                    nfns.add(li.def());
                }
                ThisDef td = ncm.def() instanceof X10MemberDef ? ((X10MemberDef) ncm.def()).thisDef() : ct.x10Def().thisDef();
                Type ot = null; // FIXME
                TypeSystem ts = t.typeSystem();
                ClosureDef cd = ts.closureDef(ft.position(), Types.ref(nct), Types.ref(ncm), Types.ref(nfi.returnType()), fts, td, nfns, Types.ref(nfi.guard()), Types.ref(ot));
                t = ts.closureType(cd);
            }
        } else if (t instanceof FunctionType) { // order matters!
            FunctionType ft = (FunctionType) t;
            Type rt = ft.returnType();
            List<Type> tas = ft.typeParameters();
            assert (tas.isEmpty());
            List<LocalInstance> fns = ft.formalNames();
            List<Type> ats = ft.argumentTypes();
            CConstraint g = ft.guard();
            Type nrt = transformType(rt);
            List<LocalDef> nfns = new ArrayList<LocalDef>();
            boolean changedFN = false;
            for (LocalInstance li : fns) {
                X10LocalDef ld = (X10LocalDef) li.def();
                X10LocalDef newld = vars.get(ld);
                if (newld == null) {
                    Type lt = Types.get(ld.type());
                    Type newlt = transformType(lt);
                    if (newlt != rt) {
                        newld = copyLocalDef(ld);
                        newld.setType(Types.ref(newlt));
                    } else {
                        newld = ld; // unchanged
                    }
                    mapLocal(ld, newld);
                }
                if (newld != ld) changedFN = true;
                nfns.add(newld);
            }
            List<Type> nats = transformTypeList(ats);
            CConstraint ng = transformConstraint(g);
            if (nrt != rt || changedFN || nats != ats || ng != g) {
                List<Ref<? extends Type>> fts = new ArrayList<Ref<? extends Type>>();
                List<ParameterType> tps = Collections.<ParameterType>emptyList();
                for (Type a : nats) {
                    fts.add(Types.ref(a));
                }
                TypeSystem ts = t.typeSystem();
                t = ts.functionType(ft.position(), Types.ref(nrt), tps, fts, nfns, Types.ref(ng));
            }
        } else if (t instanceof X10ParsedClassType) { // order matters!
            X10ParsedClassType qt = (X10ParsedClassType) t;
            List<Type> tas = qt.typeArguments();
            List<Type> ntas = transformTypeList(tas);
            if (ntas != tas) {
                qt = qt.typeArguments(ntas);
            }
            X10ClassType ct = (X10ClassType) qt.outer();
            X10ClassType nct = ct == null ? null : (X10ClassType) transformType(ct);
            if (ct != null && !qt.isInnerClass()) {
                nct = nct.typeArguments(null);
                TypeSystem ts = ct.typeSystem();
                if (ts.typeEquals(nct, ct, ts.emptyContext())) {
                    nct = ct;
                }
            }
            if (nct != ct) {
                qt = qt.container(nct);
            }
            t = qt;
        }
        // TODO: annotations
        return t;
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
            X10LocalInstance newli = transformLocalInstance(((X10LocalInstance) ld.asInstance()));
            li = li.lval() ? (X10LocalInstance)newli.lval(true) : newli;
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
        List<VarInstance<? extends VarDef>> ce = cd.capturedEnvironment();
        List<VarInstance<? extends VarDef>> ice = transformCapturedEnvironment(ce);
        if (ce != ice) {
            if (cd == d.closureDef()) {
                cd = (ClosureDef) cd.copy();
            }
            cd.setCapturedEnvironment(ice);
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
                    if (vi.lval()) {
                        li = (X10LocalInstance)li.lval(true);
                    }
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
        return (LocalDecl) transformVarDecl(d, old);
    }

    @Override
    protected X10Formal transform(X10Formal f, X10Formal old) {
        return (X10Formal) transformVarDecl(f, old);
    }

    private VarDecl transformVarDecl(VarDecl d, VarDecl old) {
        boolean sigChanged = d.type() != old.type(); // conservative compare detects changes in substructure
        // There may already be a localdef mapping for this variable.  This happens when variable references
        // are encountered before the declaration, e.g., in return types and in the variable initializer type
        // (the self binding).  If that happens, use the existing mapping, but validate.
        // We use a mapping of a localdef to itself to indicate that a reference was encountered before the
        // declaration, but processing the reference did not change the local def (which means that this
        // method cannot change the local def either).
        TypeSystem xts = visitor().typeSystem();
        X10LocalDef ld = (X10LocalDef) d.localDef();
        X10LocalDef mld = vars.get(ld);
        if (mld != null) {
            if (sigChanged) {
                // validate the type
                if (mld == ld || !xts.typeEquals(d.type().type(), Types.get(mld.type()), visitor().context())) {
                    throw new InternalCompilerError("Inconsistent local mapping for "+d.name().id(), d.position());
                }
                // adjust the return type node's type reference to match that of the stored localdef
                d = d.type(d.type().typeRef(mld.type()));
            }
            // now use the new mapping
            return d.localDef(mld);
        }
        if (sigChanged) {
            X10LocalDef ild = copyLocalDef(ld);
            ild.setType(d.type().typeRef());
            mapLocal(ld, ild);
            return d.localDef(ild);
        } else {
            mapLocal(ld, ld); // mark this localdef as having been processed
        }
        return d;
    }

    protected static final X10LocalDef copyLocalDef(X10LocalDef ld) {
        TypeSystem xts = ld.typeSystem(); 
        X10LocalDef res = xts.localDef(ld.position(), ld.flags(), Types.ref(Types.get(ld.type())), ld.name());
        if (ld.isAsyncInit()) res.setAsyncInit(); // Cannot use copy() because it's not a deep clone
        return res;
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
