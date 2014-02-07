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

package x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.types.ContainerType;
import polyglot.types.LazyRef;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.TypeSystem;
import polyglot.util.Pair;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;


/**
 * Implements a type substitution [ActualT1,..., ActualTn/FormalX1,..., FormalXn].
 * A substitution is lazy by default (creating a reinstantiated wrapper object for
 * constructor and method instances), but can also be eager (replacing the types
 * right away).  Eager substitutions cannot be used until after typechecking, as
 * that may introduce dependency cycles.  
 * @author nystrom
 */
public class TypeParamSubst {
	public static final TypeParamSubst IDENTITY = new TypeParamSubst();
	private final List<? extends Type> typeArguments;
	private final List<ParameterType> typeParameters;
	private final TypeSystem ts;

	private final boolean identityInstantiation;
	private final boolean eager;

	public TypeParamSubst(TypeSystem ts, List<? extends Type> tas, List<ParameterType> tps) {
	    this(ts, tas, tps, false);
	}

	public TypeParamSubst(TypeSystem ts, List<? extends Type> tas, List<ParameterType> tps, boolean eager) {
		this.identityInstantiation = isIdentityInstantiation(tas, tps);
		this.eager = eager;
		tas = tas == null ? tps : tas;
		if (!(tps == null ? tas == null : tas.size() == tps.size())) {
		    assert (tps == null ? tas == null : tas.size() == tps.size());
		}
		this.ts = ts;
		this.typeArguments = tas == null ? Collections.<Type>emptyList() : tas;
		this.typeParameters = tps == null ? Collections.<ParameterType>emptyList() : tps;
	}

	private TypeParamSubst() {
	    this(null, null, null, false);
	}

    public ArrayList<Type> copyTypeArguments() {
        return new ArrayList<Type>(typeArguments);
    }
    public ArrayList<ParameterType> copyTypeParameters() {
        return new ArrayList<ParameterType>(typeParameters);
    }

	public static boolean isSameParameter(ParameterType pt1, ParameterType pt2) {
		return pt1 == pt2 ||
		(Types.get(pt1.def()) == Types.get(pt2.def()) && pt1.name().equals(pt2.name()));
	}

	private Type reinstantiateType(Type t, boolean forceTypeArguments) {
		if (t instanceof ParameterType) { // always eager
			ParameterType pt = (ParameterType) t;
			for (int i = 0; i < typeParameters.size(); i++) {
				ParameterType pt2 = typeParameters.get(i);
				if (i < typeArguments.size()) {
					if (isSameParameter(pt, pt2)) {
						return typeArguments.get(i);
					}
				}
			}
			return pt;
		}
		if (t instanceof ConstrainedType) { // always eager
			ConstrainedType ct = (ConstrainedType) t;
			ct = ct.baseType(reinstantiate(ct.baseType()));
			ct = ct.constraint(reinstantiate(ct.constraint()));
			return ct;
		}
		if (t instanceof MacroType) {
			MacroType mt = (MacroType) t;
			if (eager) {
			    mt = mt.definedType(reinstantiate(mt.definedType()));
			    mt = (MacroType) mt.formalNames(reinstantiate(mt.formalNames()));
			    mt = (MacroType) mt.formalTypes(reinstantiate(mt.formalTypes()));
			    mt = (MacroType) mt.typeParameters(reinstantiate(mt.typeParameters()));
			    mt = mt.guard(reinstantiate(mt.guard()));
			}
			return new ReinstantiatedMacroType(this, ts, mt.position(), Types.ref(mt.def()), mt);
		}
		//	if (t instanceof ClosureType) {
		//	    ClosureType ct = (ClosureType) t;
		//	    ct = ct.closureInstance(reinstantiate(ct.applyMethod()));
		//	    return ct;
		//	}
		if (t instanceof X10ClassType) { // always eager
			X10ClassType ct = (X10ClassType) t;
			if (!canReferToParams(ct))
				return ct;
			List<Type> typeArgs = ct.typeArguments();
			List<ParameterType> tParams = ct.x10Def().typeParameters();
			if (typeArgs == null && forceTypeArguments && !tParams.isEmpty()) {
			    typeArgs = new ArrayList<Type>(tParams);
			}
			// [DC] surely the following is an error in the compiler -- any pass that changes
			// the def should also update the types to avoid such inconsistencies
			if (typeArgs != null && typeArgs.size() < tParams.size()) {
			    typeArgs = new ArrayList<Type>(typeArgs);
			    // The def changed since the type was created; params were added
			    for (int i = typeArgs.size(); i < tParams.size(); i++) {
			        typeArgs.add(tParams.get(i));
			    }
			}
			ct = ct.typeArguments(reinstantiate(typeArgs));
			if (ct.isMember()) {
				ct = ct.container((ContainerType) reinstantiateType(ct.container(), ct.isInnerClass()));
			}
			return ct;
		}
		return t;
	}

	private static boolean canReferToParams(X10ClassType t) {
		// FIXME: to fix XTENLANG-2055, this should be false for null typeargs,
		// but constructor and method def containers are not instantiated.
		// TODO: (t.typeArguments() != null && t.typeArguments().size() != 0)
		if (t.typeArguments() == null || t.typeArguments().size() != 0) {
			return true;
		}
		if (t.isMember())
			return canReferToParams((X10ClassType) t.outer());
		if (! t.isTopLevel())
			return true;
		return false;
	}

	public boolean isIdentityInstantiation() {
	    return identityInstantiation;
	}

	public boolean isEager() {
	    return eager;
	}
	
	private static boolean isIdentityInstantiation(List<? extends Type> tas, List<ParameterType> tps) {
	    if (tas == null) return true;
	    int n = tps.size();
	    if (n != tas.size()) return false;
	    for (int i = 0; i < n; i++) {
	        ParameterType pt = tps.get(i);
	        Type at = tas.get(i);
	        if (at instanceof ParameterType) {
	            ParameterType apt = (ParameterType) at;
	            if (! isSameParameter(pt, apt))
	                return false;
	        }
	        else {
	            return false;
	        }
	    }
	    return true;
	}

	//private Map<Object,Object> cache = CollectionFactory.newHashMap();

	//@SuppressWarnings("unchecked") // Casting to a generic type parameter
	public <T> T reinstantiate(T t) {
	    if (t == null)
	        return null;
	    //Object o = cache.get(t);
	    //if (o != null && false)
	    //    return (T) o;
	    T x = reinstantiateUncached(t);
	    //cache.put(t, x);
	    return x;
	}

	@SuppressWarnings("unchecked") // Casting to a generic type parameter
	private <T> T reinstantiateUncached(T t) {
		if (isIdentityInstantiation()) {
			return t;
		}
		if (t instanceof Ref<?>) return (T) reinstantiateRef((Ref<?>) t);
		if (t instanceof Type) return (T) reinstantiateType((Type) t, true);
		if (t instanceof X10FieldInstance) return (T) reinstantiateFI((X10FieldInstance) t);
		if (t instanceof MethodInstance) return (T) reinstantiateMI((MethodInstance) t);
		if (t instanceof X10ConstructorInstance) return (T) reinstantiateCI((X10ConstructorInstance) t);
		if (t instanceof ClosureInstance) return (T) reinstantiateClosure((ClosureInstance) t);
		if (t instanceof CConstraint) return (T) reinstantiateConstraint((CConstraint) t);
		if (t instanceof XTerm) return (T) reinstantiateTerm((XTerm) t);
		if (t instanceof TypeConstraint) return (T) reinstantiateTypeConstraint((TypeConstraint) t);
		if (t instanceof X10LocalInstance) return (T) reinstantiateLI((X10LocalInstance) t);
		//if (t instanceof X10LocalDef) return (T) reinstantiateLD((X10LocalDef) t);
		if (t instanceof TypeParamSubst) return (T) reinstantiateTPS((TypeParamSubst) t);
        assert false : t;
		return t;
	}

    private TypeParamSubst reinstantiateTPS(TypeParamSubst t) {
        //List<? extends Type> tas = reinstantiate(t.typeArguments);
        List<ParameterType> tps = reinstantiate(t.typeParameters);
        return new TypeParamSubst(ts, t.typeArguments, tps, t.eager);
    }

	private X10LocalInstance reinstantiateLI(X10LocalInstance t) {
		if (eager) {
		    X10LocalInstance li = t;
		    li = li.type(reinstantiate(li.type()));
		    return li;
		}
		return new ReinstantiatedLocalInstance(this, t.typeSystem(), t.position(), Types.ref(t.x10Def()), t);
	}
	/*
	public X10LocalInstance reinstantiateLD(X10LocalDef t) {
		final X10LocalDef ld = (X10LocalDef) t.copy();
		return new ReinstantiatedLocalDef(this, ld.typeSystem(), ld.position(), Types.ref(ld.x10Def()), ld);
	}
	*/
	private ClosureInstance reinstantiateClosure(ClosureInstance t) {
		if (eager) {
		    final ClosureInstance fi = t;
		    ClosureInstance res = new ClosureInstance_c(fi.typeSystem(), fi.position(), fi.errorPosition(), Types.ref(fi.def()));
		    res = (ClosureInstance) res.returnType(reinstantiate(fi.returnType()));
		    res = (ClosureInstance) res.formalNames(reinstantiate(fi.formalNames()));
		    res = (ClosureInstance) res.formalTypes(reinstantiate(fi.formalTypes()));
		    //res = (ClosureInstance) res.throwTypes(reinstantiate(fi.throwTypes()));
		    res = (ClosureInstance) res.guard(reinstantiate(fi.guard()));
		    return res;
		}
		return new ReinstantiatedClosureInstance_c(this, t.typeSystem(), t.position(), Types.ref(t.def()), t);
	}

	private <T> Ref<T> reinstantiateRef(final Ref<T> t) {
		if (eager || t.known()) {
			return Types.ref(reinstantiate(t.get()));
		}
		final LazyRef<T> r = Types.lazyRef(null);
		r.setResolver(new Runnable() {
			public void run() {
				r.update(reinstantiate(t.get()));
			}
		});
		return r;
	}

	public static CConstraint reinstantiateConstraint(X10ClassType ct, CConstraint c) {
		if (c == null || c.valid())
			return c;
		CConstraint result = c;
		if (ct instanceof X10ParsedClassType_c) {
			X10ParsedClassType_c t = (X10ParsedClassType_c) ct;
	        if (t.isIdentityInstantiation()) {
	            return c;
	        }
			result = t.subst().reinstantiateConstraint(c);
		}
		return result;
	}

	public static TypeConstraint reinstantiateTypeConstraint(X10ClassType ct, TypeConstraint c) {
		if (c == null)
			return c;
		TypeConstraint result = c;
		if (ct instanceof X10ParsedClassType_c) {
			X10ParsedClassType_c t = (X10ParsedClassType_c) ct;
			if (t.isIdentityInstantiation()) {
			    return c;
			}
			result = t.subst().reinstantiateTypeConstraint(c);
		}
		return result;
	}

	private CConstraint reinstantiateConstraint(CConstraint c) {
		int n = typeParameters.size();
		assert typeArguments.size() == n;

		XTerm[] ys = new XTerm[n];
		XVar[] xs = new XVar[n];

		for (int i = 0; i < n; i++) {
			ParameterType pt = typeParameters.get(i);
			Type at = typeArguments.get(i);

			XTerm p = ts.xtypeTranslator().translate(pt);
			XTerm a = ts.xtypeTranslator().translate(at);

			ys[i] = a;

			if (p instanceof XVar) {
				xs[i] = (XVar) p;
			}
			else {
				xs[i] = ConstraintManager.getConstraintSystem().makeLit("error");
			}
		}

		CConstraint result;

		try {
			result = c.substitute(ys, xs);
		}
		catch (XFailure e) {
			result = ConstraintManager.getConstraintSystem().makeCConstraint();
			result.setInconsistent();
		}

		return result;
	}
	private TypeConstraint reinstantiateTypeConstraint(TypeConstraint c) {
		int n = typeParameters.size();
		assert typeArguments.size() == n;
		boolean changed = false;
		List<SubtypeConstraint> terms = new ArrayList<SubtypeConstraint>(c.terms().size());
		for (SubtypeConstraint s : c.terms()) {
			Type sub = s.subtype();
			Type sup = s.supertype();
			Type sub1 = reinstantiate(sub);
			Type sup1 = reinstantiate(sup);
			if (sub != sub1 || sup != sup1) {
				changed = true;
				s = new SubtypeConstraint(sub1, sup1, s.kind());
			}
			terms.add(s);
		}
		if (changed) {
			c = new TypeConstraint();
			c.addTerms(terms);
		}
		return c;
	}

	private XTerm reinstantiateTerm(XTerm t) {
		int n = typeParameters.size();
		assert typeArguments.size() == n;

		for (int i = 0; i < n; i++) {
			ParameterType pt = typeParameters.get(i);
			Type at = typeArguments.get(0);

			XTerm p = ts.xtypeTranslator().translate(pt);
			XTerm a = ts.xtypeTranslator().translate(at);

			if (p instanceof XVar) {
				t = t.subst(p, (XVar) a);
			}
		}

		return t;
	}

	private X10ConstructorInstance reinstantiateCI(X10ConstructorInstance t) {
		if (eager) {
		    X10ConstructorInstance ci = t;
		    ci = (X10ConstructorInstance) ci.returnType(reinstantiate(ci.returnType()));
		    ci = (X10ConstructorInstance) ci.formalNames(reinstantiate(ci.formalNames()));
		    ci = (X10ConstructorInstance) ci.formalTypes(reinstantiate(ci.formalTypes()));
		    ci = (X10ConstructorInstance) ci.throwTypes(reinstantiate(ci.throwTypes()));
		    ci = (X10ConstructorInstance) ci.typeParameters(reinstantiate(ci.typeParameters()));
		    ci = (X10ConstructorInstance) ci.container(reinstantiate(ci.container()));
		    ci = (X10ConstructorInstance) ci.guard(reinstantiate(ci.guard()));
		    ci = (X10ConstructorInstance) ci.typeGuard(reinstantiate(ci.typeGuard()));
		    return ci;
		}
		return new ReinstantiatedConstructorInstance(this, t.typeSystem(), t.position(), Types.ref(t.x10Def()), t);
	}

	private MethodInstance reinstantiateMI(MethodInstance t) {
		if (eager) {
		    MethodInstance mi = t;
		    mi = (MethodInstance) mi.typeParameters(reinstantiate(mi.typeParameters()));
		    mi = (MethodInstance) mi.returnType(reinstantiate(mi.returnType()));
		    mi = (MethodInstance) mi.formalNames(reinstantiate(mi.formalNames()));
		    mi = (MethodInstance) mi.formalTypes(reinstantiate(mi.formalTypes()));
		    mi = (MethodInstance) mi.throwTypes(reinstantiate(mi.throwTypes()));
		    mi = (MethodInstance) mi.container(reinstantiate(mi.container()));
		    mi = (MethodInstance) mi.guard(reinstantiate(mi.guard()));
		    mi = (MethodInstance) mi.typeGuard(reinstantiate(mi.typeGuard()));
		    return mi;
		}
		return new ReinstantiatedMethodInstance(this, t.typeSystem(), t.position(), Types.ref(t.x10Def()), t);
	}

	private X10FieldInstance reinstantiateFI(X10FieldInstance t) {
		if (eager) {
		    X10FieldInstance fi = t;
		    fi = (X10FieldInstance) fi.type(reinstantiate(fi.type()));
		    fi = (X10FieldInstance) fi.container(reinstantiate(fi.container()));
		    return fi;
		}
		return new ReinstantiatedFieldInstance(this, t.typeSystem(), t.position(), Types.ref(t.x10Def()), t);
	}

	public <T> List<T> reinstantiate(List<T> list) {
		if (isIdentityInstantiation()) {
			return list;
		}
		if (list == null)
		    return null;
		if (eager) {
		    boolean changed = false;
		    List<T> res = new ArrayList<T>();
		    for (T t : list) {
		        T r = reinstantiate(t);
		        changed |= (r != t);
		        res.add(r);
		    }
		    if (!changed)
		        return list;
		    return res;
		}
		return new TransformingList<T, T>(list, new Transformation<T, T>() {
			public T transform(T o) {
				return reinstantiate(o);
			}		
		});
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(typeArguments);
		sb.append("/");
		sb.append(typeParameters);
		sb.append("]");
		return sb.toString();
	}

}
