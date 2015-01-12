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
package x10.types.matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.ContainerType;
import polyglot.types.LocalInstance;
import polyglot.types.NullType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.ClosureInstance;
import x10.types.ConstrainedType;
import x10.types.MacroType;
import x10.types.MethodInstance;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10LocalInstance;
import x10.types.X10ParsedClassType;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;

public class Subst {
    public static Type subst(Type t, Type[] Y, ParameterType[] X) throws SemanticException {
        assert Y.length == X.length;

        for (int i = 0; i < X.length; i++) {
            if (Y[i] == null)
                throw new SemanticException("Cannot infer type for parameter " + X[i] + ".");
            t = subst(t, Y[i], X[i]);
        }
        return t;
    }
        
    /**
     * Add in to the constraint for every component of the type t, recursively:
     * If t is T{c} ===> return addIn(T,in){c,in}
     * If t is A[T1,..., Tn] ===> return addIn(A,in)[addIn(T1,in),..., addIn(Tn,n)]
     * If t is X ==> return X{in}
     * @param t --- The type subjected to the change
     * @param in --- The constraint to be added
     * @return -- The type t, with c added to each component (recursively).
     * @throws XFailure -- If one of the component types is inconsistent.
     */
    public static Type addIn(Type t, CConstraint in) {
        if (t == null)
            return null;

        TypeSystem ts = (TypeSystem) t.typeSystem();

        t = ts.expandMacros(t);

        if (t instanceof NullType)
            return t;
        if (t instanceof UnknownType)
            return t;
        if (t instanceof ParameterType) {
        	return t;
          //  if (in.valid()) return t;
          //  return Types.xclause(t, in);
        }
        if (ts.isVoid(t)) {
            return t;
        }
        if (t instanceof X10ParsedClassType) {
            X10ParsedClassType ct = (X10ParsedClassType) t;
            List<Type> newArgs = new ArrayList<Type>();
            if (ct.typeArguments() == null)
                return ct;
            for (Type at : ct.typeArguments()) {
                Type at2 = addIn(at, in);
                newArgs.add(at2);
            }
            return ct.typeArguments(newArgs);
        }
        Type base = Types.baseType(t);
        CConstraint c = Types.xclause(t);
        if (t==base)
            assert t != base;

        base = addIn(base, in);

        if (c != null) {
            c = c.copy();
            c.addIn(in);
        }

        return Types.xclause(base, c);
    }

    public static Type project(Type t, XVar v) {
        if (t == null)
            return null;


        TypeSystem ts = (TypeSystem) t.typeSystem();

        t = ts.expandMacros(t);

        if (t instanceof NullType)
            return t;
        if (t instanceof UnknownType)
            return t;
        if (t instanceof ParameterType) {
            return t;
        }
        if (ts.isVoid(t)) {
            return t;
        }
        if (t instanceof X10ParsedClassType) {
            X10ParsedClassType ct = (X10ParsedClassType) t;
            List<Type> newArgs = new ArrayList<Type>();
            if (ct.typeArguments() == null)
                return ct;
            for (Type at : ct.typeArguments()) {
                Type at2 = project(at, v);
                newArgs.add(at2);
            }
            return ct.typeArguments(newArgs);
        }

        Type base = Types.baseType(t);
        CConstraint c = Types.xclause(t);
        if (t == base) 
            assert t != base;
        base = project(base, v);

        if (c != null) {
            c = c.copy().project(v);
        }

        return Types.xclause(base, c);
    }

    /**
     * Returns a new type formed from t by substituting y for x. The old type is not modified.
     * @param t
     * @param y
     * @param x
     * @return
     * @throws SemanticException
     */
    public static List<Type> subst(List<Type> ts, XTerm[] y, XVar[] x) throws SemanticException {
        List<Type> result= new ArrayList<Type>(ts.size());
        boolean changed = false;
        for (Type t : ts) {
            Type nt = subst(t, y, x);
            if (nt != t)
                changed = true;
            result.add(nt);
        }
        if (!changed) return ts;
        return result;
    }

    public static Type subst(Type t, XTerm[] y, XVar[] x) throws SemanticException {
        assert y.length == x.length;

        if (t == null)
            return null;

        TypeSystem ts = (TypeSystem) t.typeSystem();

        t = ts.expandMacros(t);

        Type base = Types.baseType(t);
        CConstraint c = Types.xclause(t);


        if (t instanceof X10ParsedClassType) {
            X10ParsedClassType ct = (X10ParsedClassType) t;
            if (ct.typeArguments() == null)
                return ct;
            List<Type> newArgs = new ArrayList<Type>();
            boolean changed = false;
            for (Type at : ct.typeArguments()) {
                Type at2 = subst(at, y, x);
                newArgs.add(at2);
                if (at2 != at) changed = true;
            }
            if (changed && ! newArgs.isEmpty())
                return ct.typeArguments(newArgs);
        } else 
            if (c != null) {
                CConstraint newC = c;
                Type newBase = subst(base, y, x);

                try {
                    newC = newC.substitute(y, x);
                    //                  newC = newC.saturate();
                }
                catch (XFailure e) {
                    throw new SemanticException("Cannot instantiate formal parameters on actuals.");
                }

                if (newBase != base || newC != c) {
                    return Types.xclause(newBase, newC);
                }
            }


        return t;
    }

    public static Type subst(Type t, XTerm[] y, XVar[] x, Type[] Y, ParameterType[] X) throws SemanticException {
        if (t instanceof ConstrainedType) {
            ConstrainedType ct = (ConstrainedType) t;
            Type base = Types.get(ct.baseType()); // do not call X10TypeMixin.baseType(ct); that will strip constraints in ct
            base = subst(base, y, x, Y, X);
            CConstraint c = Types.get(ct.constraint());
            c =  subst(c, y, x, Y, X);
           
            return Types.xclause(base, c);
        }
        if (t instanceof ParameterType) {
            for (int i = 0; i < X.length; i++) {
                if (TypeParamSubst.isSameParameter((ParameterType) t, X[i]))
                    return Y[i];
            }
            return t;
        }
        if (t instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) t;
            if (ct.isIdentityInstantiation()) {
                List<Type> args = new ArrayList<Type>();
                boolean changed = false;
                for (Type ti : ct.x10Def().typeParameters()) {
                    Type ti2 = subst(ti, y, x, Y, X);
                    if (ti2 != ti)
                        changed = true;
                    args.add(ti2);
                }
                if (changed)
                    return ct.typeArguments(args);
                return ct;
            }
            else {
                if (ct.typeArguments() == null)
                    return ct;
                List<Type> args = new ArrayList<Type>();
                boolean changed = false;
                for (Type ti : ct.typeArguments()) {
                    Type ti2 = subst(ti, y, x, Y, X);
                    if (ti2 != ti)
                        changed = true;
                    args.add(ti2);
                }
                if (changed)
                    return ct.typeArguments(args);
                return ct;
            }
        }
        if (t instanceof MacroType) {
            MacroType mt = (MacroType) t;
            return subst(mt.definedType(), y, x, Y, X);
        }
        return t;
    }

    public static Type subst(Type t, XTerm y, XVar x) throws SemanticException {
        return subst(t, new XTerm[] { y }, new XVar[] { x });
    }

    public static Type subst(Type t, Type Y, ParameterType X) throws SemanticException {
        return subst(t, new XTerm[] {}, new XVar[] { }, new Type[] { Y }, new ParameterType[] { X });
    }

    public static CConstraint subst(CConstraint t, XTerm y, XVar x) throws SemanticException {
        return subst(t, new XTerm[] { y }, new XVar[] { x }, new Type[0], new ParameterType[0]);
    }

    // FIXME: this is wrong, because types may appear in the constraint
    public static CConstraint subst(CConstraint t, Type Y, ParameterType X) throws SemanticException {
        return t;
    }

    public static TypeConstraint subst(TypeConstraint t, XTerm y, XVar x) throws SemanticException {
        if (t == null)
            return null;
        return t.subst(y, x);
    }

    public static TypeConstraint subst(TypeConstraint t, Type Y, ParameterType X) throws SemanticException {
        if (t == null)
            return null;
        TypeParamSubst subst = new TypeParamSubst((TypeSystem) Y.typeSystem(), Arrays.asList(Y), Arrays.asList(X));
        return subst.reinstantiate(t);
    }

    public static TypeConstraint subst(TypeConstraint t, XTerm[] y, XVar[] x) throws SemanticException {
        assert y.length == x.length;

        if (t == null)
            return null;

        for (int i = 0; i < x.length; i++)
            t = subst(t, y[i], x[i]);

        return t;
    }

    public static TypeConstraint subst(TypeConstraint t, Type[] Y, ParameterType[] X) throws SemanticException {
        assert Y.length == X.length;

        if (t == null)
            return null;

        for (int i = 0; i < X.length; i++) {
            if (Y[i] == null)
                throw new SemanticException("Cannot infer type for parameter " + X[i] + ".");
            t = subst(t, Y[i], X[i]);
        }

        return t;
    }

    public static TypeConstraint subst(TypeConstraint t, XTerm[] y, XVar[] x, Type[] Y, ParameterType[] X) throws SemanticException {
        TypeConstraint t2 = subst(t, y, x);
        TypeConstraint t3 = subst(t2, Y, X);
        return t3;
    }

    public static CConstraint subst(CConstraint t, XTerm[] y, XVar[] x) throws SemanticException {
        if (t == null)
            return null;

        try {
            CConstraint c = t.substitute(y, x);
            return c;
        }
        catch (XFailure e) {
            throw new SemanticException("Cannot instantiate formal parameters on actuals.");
        }
    }

    public static CConstraint subst(CConstraint t, Type[] Y, ParameterType[] X) throws SemanticException {
        if (t == null)
            return null;

        return t;
    }

    public static CConstraint subst(CConstraint t, XTerm[] y, XVar[] x, Type[] Y, ParameterType[] X) throws SemanticException {
        CConstraint t2 = subst(t, y, x);
        CConstraint t3 = subst(t2, Y, X);
        return t3;
    }

    public static X10FieldInstance subst(X10FieldInstance fi, XTerm[] y, XVar[] x) throws SemanticException {
        Type ft = subst(fi.type(), y, x);
        Type rt = subst(fi.rightType(), y, x);
        ContainerType ct = (ContainerType) subst(fi.container(), y, x);
        return (X10FieldInstance) fi.type(ft, rt).container(ct);
    }

    public static X10FieldInstance subst(X10FieldInstance fi, XTerm y, XVar x) throws SemanticException {
        return subst(fi, new XTerm[] { y }, new XVar[] { x });
    }

    public static X10LocalInstance subst(X10LocalInstance li, XTerm[] y, XVar[] x) throws SemanticException {
        Type ft = subst(li.type(), y, x);
        return li.type(ft);
    }

    public static X10LocalInstance subst(X10LocalInstance li, XTerm y, XVar x) throws SemanticException {
        return subst(li, new XTerm[] { y }, new XVar[] { x });
    }

    /**
     * @param ci
     * @param y
     * @param x
     * @return
     * @throws SemanticException 
     */
    public static X10ConstructorInstance subst(X10ConstructorInstance ci, XTerm[] y, XVar[] x) throws SemanticException {
        Type returnType = ci.returnType();
        Type newReturnType = subst(returnType, y, x);
        if (newReturnType != returnType) {
            ci = ci.returnType(newReturnType);
        }
        List<Type> formalTypes = ci.formalTypes();
        List<Type> newFormalTypes = subst(formalTypes, y, x);
        if (newFormalTypes != formalTypes) {
            ci = ci.formalTypes(newFormalTypes);
        }
        List<LocalInstance> newFormalNames = new ArrayList<LocalInstance>();
        boolean changed = false;
        for (LocalInstance li : ci.formalNames()) {
            try {
                LocalInstance newLI = subst((X10LocalInstance) li, y, x);
                if (newLI != li) changed = true;
                newFormalNames.add(newLI);
            }
            catch (SemanticException e) {
                newFormalNames.add(li);
            }
        }
        if (changed) {
            ci = (X10ConstructorInstance) ci.formalNames(newFormalNames);
        }
        ContainerType ct = (ContainerType) subst(ci.container(), y, x);
        if (ct != ci.container()) {
            ci =  (X10ConstructorInstance) ci.container(ct);
        }
        return ci;
    }

    public static X10ConstructorInstance subst(X10ConstructorInstance ci, XTerm y, XVar x) throws SemanticException {
        return subst(ci, new XTerm[] { y }, new XVar[] { x });
    }

    /**
     * @param ci
     * @param y
     * @param x
     * @return
     * @throws SemanticException 
     */
    public static MethodInstance subst(MethodInstance mi, XTerm[] y, XVar[] x) throws SemanticException {
        Type returnType = mi.returnType();
        Type newReturnType = subst(returnType, y, x);
        if (newReturnType != returnType) {
            mi = mi.returnType(newReturnType);
        }
        List<Type> formalTypes = mi.formalTypes();
        List<Type> newFormalTypes = subst(formalTypes, y, x);
        if (newFormalTypes != formalTypes) {
            mi = mi.formalTypes(newFormalTypes);
        }
        List<LocalInstance> newFormalNames = new ArrayList<LocalInstance>();
        boolean changed = false;
        for (LocalInstance li : mi.formalNames()) {
            try {
                LocalInstance newLI = subst((X10LocalInstance) li, y, x);
                if (newLI != li) changed = true;
                newFormalNames.add(newLI);
            }
            catch (SemanticException e) {
                newFormalNames.add(li);
            }
        }
        if (changed) {
            mi = (MethodInstance) mi.formalNames(newFormalNames);
        }
        ContainerType ct = (ContainerType) subst(mi.container(), y, x);
        if (ct != mi.container()) {
            mi =  (MethodInstance) mi.container(ct);
        }
        return mi;
    }

    public static MethodInstance subst(MethodInstance mi, XTerm y, XVar x) throws SemanticException {
        return subst(mi, new XTerm[] { y }, new XVar[] { x });
    }

    /**
     * @param ci
     * @param y
     * @param x
     * @return
     * @throws SemanticException 
     */
    public static ClosureInstance subst(ClosureInstance ci, XTerm[] y, XVar[] x) throws SemanticException {
        Type returnType = ci.returnType();
        Type newReturnType = subst(returnType, y, x);
        if (newReturnType != returnType) {
            ci = (ClosureInstance) ci.returnType(newReturnType);
        }
        List<Type> formalTypes = ci.formalTypes();
        List<Type> newFormalTypes = subst(formalTypes, y, x);
        if (newFormalTypes != formalTypes) {
            ci = (ClosureInstance) ci.formalTypes(newFormalTypes);
        }
        List<LocalInstance> newFormalNames = new ArrayList<LocalInstance>();
        boolean changed = false;
        for (LocalInstance li : ci.formalNames()) {
            try {
                LocalInstance newLI = subst((X10LocalInstance) li, y, x);
                if (newLI != li) changed = true;
                newFormalNames.add(newLI);
            }
            catch (SemanticException e) {
                newFormalNames.add(li);
            }
        }
        if (changed) {
            ci = (ClosureInstance) ci.formalNames(newFormalNames);
        }
        ClassType ct = (ClassType) subst(ci.typeContainer(), y, x);
        if (ct != ci.typeContainer()) {
            ci =  (ClosureInstance) ci.typeContainer(ct);
        }
        /* FIXME I'm not sure how to do substitution on a ClosureInstance's method container.  
        // It won't matter for the Inlining type transformer which overwrites the method container.
        // But, it might for other users of the Reinstantiator.
        //
        ContainerType ct = (ContainerType) subst(ci.methodContainer(), y, x);
        if (ct != ci.methodContainer()) {
            ci =  (ClosureInstance) ci.methodContainer(ct);
        }
        */
        return ci;
    }

    public static ClosureInstance subst(ClosureInstance mi, XTerm y, XVar x) throws SemanticException {
        return subst(mi, new XTerm[] { y }, new XVar[] { x });
    }

}