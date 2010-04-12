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

package x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.types.LazyRef;
import polyglot.types.LocalInstance;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Pair;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.SubtypeConstraint_c;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.TypeConstraint_c;

/**
 * Comments by vj.
 * Implements a type substitution [ActualT1,..., ActualTn/FormalX1,..., FormalXn].
 * @author nystrom
 *
 */
public class TypeParamSubst {
	List<Type> typeArguments;
	List<ParameterType> typeParameters;
	X10TypeSystem ts;

	final boolean missingArgs;
	public TypeParamSubst(X10TypeSystem ts, List<Type> typeArguments2, 
			List<ParameterType> typeParameters2) {
		missingArgs = typeParameters2 != null && typeParameters2.size() > 0 && typeArguments2 == null;
		typeArguments2 = typeArguments2 == null ? (List) typeParameters2 : (List) typeArguments2; 
		assert (typeParameters2 == null ? typeArguments2 == null 
				: typeArguments2.size() == typeParameters2.size());
		this.ts = ts;
		this.typeArguments = typeArguments2 == null ? Collections.EMPTY_LIST : typeArguments2;
		this.typeParameters = typeParameters2 == null ? Collections.EMPTY_LIST : typeParameters2;
	}

	public static boolean isSameParameter(ParameterType pt1, ParameterType pt2) {
		return pt1 == pt2 
		|| (Types.get(pt1.def()) == Types.get(pt2.def()) && pt1.name().equals(pt2.name()));
	}

	public Type reinstantiateType(Type t) {
		if (t instanceof ParameterType) {
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
		if (t instanceof ConstrainedType) {
			ConstrainedType ct = (ConstrainedType) t;
			ct = ct.baseType(reinstantiate(ct.baseType()));
			ct = ct.constraint(reinstantiate(ct.constraint()));
			return ct;
		}
		if (t instanceof MacroType) {
			MacroType mt = (MacroType) t;
			final MacroType fi = mt;
			return new ReinstantiatedMacroType(this, ts, mt.position(), Types.ref(mt.def()), fi);
		}
		//	if (t instanceof ClosureType) {
			//	    ClosureType ct = (ClosureType) t;
			//	    ct = (ClosureType) ct.copy();
			//	    ct = ct.closureInstance(reinstantiate(ct.applyMethod()));
			//	    return ct;
			//	}
		if (t instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) t;
			if (! hasParams(ct))
				return t;
			ct = (X10ClassType) ct.copy();
			ct = ct.typeArguments(reinstantiate(ct.typeArguments()));
			if (ct.isMember()) {
				ct = (X10ParsedClassType) ct.container(reinstantiate(ct.container()));
			}
			//	    This just sucks up memory and slows things down.
			//	    Pair p = new Pair(ct.def(), ct.typeArguments());
			//	    Map<Object, Type> tcache = ((X10TypeSystem_c) ts).tcache;
			//	    Type o = tcache.get(p);
			//	    if (o != null)
			//	        return o;
			//	    tcache.put(p, ct);
			return ct;
		}
		return t;
	}

	boolean hasParams(X10ClassType t) {
		if (t.typeArguments().size() != 0) {
			return true;
		}
		if (t.isMember())
			return hasParams((X10ClassType) t.outer());
		if (! t.isTopLevel())
			return true;
		return false;
	}

	public boolean isMissingParameters() {
		return missingArgs;
	}
	public boolean isIdentityInstantiation() {
		if (typeArguments == null) return true;
		int n = typeParameters.size();
		if (n != typeArguments.size()) return false;
		for (int i = 0; i < n; i++) {
			ParameterType pt = typeParameters.get(i);
			Type at = typeArguments.get(i);
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

	Map<Object,Object> cache = new HashMap<Object, Object>();

	public <T> T reinstantiate(T t) {
		if (t == null)
			return null;
		Object o = cache.get(t);
		if (o != null && false)
			return (T) o;
		T x = reinstantiateUncached(t);
		cache.put(t, x);
		return x;
	}

	private <T> T reinstantiateUncached(T t) {
		if (isIdentityInstantiation()) {
			return t;
		}
		if (t instanceof Ref) return (T) reinstantiateRef((Ref) t);
		if (t instanceof Type) return (T) reinstantiateType((Type) t);
		if (t instanceof X10FieldInstance) return (T) reinstantiateFI((X10FieldInstance) t);
		if (t instanceof X10MethodInstance) return (T) reinstantiateMI((X10MethodInstance) t);
		if (t instanceof X10ConstructorInstance) return (T) reinstantiateCI((X10ConstructorInstance) t);
		if (t instanceof ClosureInstance) return (T) reinstantiateClosure((ClosureInstance) t);
		if (t instanceof CConstraint) return (T) reinstantiateConstraint((CConstraint) t);
		if (t instanceof XTerm) return (T) reinstantiateTerm((XTerm) t);
		if (t instanceof TypeConstraint) return (T) reinstantiateTypeConstraint((TypeConstraint) t);
		if (t instanceof LocalInstance) return (T) reinstantiateLI((X10LocalInstance) t);
		return t;
	}

	public X10LocalInstance reinstantiateLI(X10LocalInstance t) {
		final X10LocalInstance li = (X10LocalInstance) t.copy();
		return new ReinstantiatedLocalInstance(this, li.typeSystem(), li.position(), Types.ref(li.x10Def()), li);
	}
	public ClosureInstance reinstantiateClosure(ClosureInstance t) {
		final ClosureInstance fi = (ClosureInstance) t.copy();
//		ClosureInstance res = new ClosureInstance_c(fi.typeSystem(), fi.position(), Types.ref(fi.def()));
//		res = (ClosureInstance) res.returnType(reinstantiate(fi.returnType()));
//		res = (ClosureInstance) res.formalTypes(reinstantiate(fi.formalTypes()));
//		res = (ClosureInstance) res.throwTypes(reinstantiate(fi.throwTypes()));
//		res = (ClosureInstance) res.guard(reinstantiate(fi.guard()));
//		return res;
		return new ReinstantiatedClosureInstance_c(this, fi.typeSystem(), fi.position(), Types.ref(fi.def()), fi);
	}

	public Ref reinstantiateRef(final Ref t) {
		if (t.known()) {
			return Types.ref(reinstantiate(t.get()));
		}
		final LazyRef r = Types.lazyRef(null);
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
			result = t.subst().reinstantiateTypeConstraint(c);
		}
		return result;
	}

	public CConstraint reinstantiateConstraint(CConstraint c) {
		if (isIdentityInstantiation()) {
			return c;
		}

		int n = typeParameters.size();
		assert typeArguments.size() == n;

		XTerm[] ys = new XTerm[n];
		XRoot[] xs = new XRoot[n];

		for (int i = 0; i < n; i++) {
			ParameterType pt = typeParameters.get(i);
			Type at = typeArguments.get(i);

			XTerm p = ts.xtypeTranslator().trans(pt);
			XTerm a = ts.xtypeTranslator().trans(at);

			ys[i] = a;

			if (p instanceof XRoot) {
				xs[i] = (XRoot) p;
			}
			else {
				xs[i] = XTerms.makeLit(XTerms.makeName("error"));
			}
		}

		CConstraint result;

		try {
			result = c.substitute(ys, xs);
		}
		catch (XFailure e) {
			result = new CConstraint_c();
			result.setInconsistent();
		}

		return result;
	}
	public TypeConstraint reinstantiateTypeConstraint(TypeConstraint c) {
		if (isIdentityInstantiation()) {
			return c;
		}

		int n = typeParameters.size();
		assert typeArguments.size() == n;


		for (int i = 0; i < n; i++) {
			ParameterType pt = typeParameters.get(i);
			Type at = typeArguments.get(i);

			List<SubtypeConstraint> terms = new ArrayList<SubtypeConstraint>(c.terms().size());
			for (SubtypeConstraint s : c.terms()) {
				Type t1 = s.subtype();
				Type t2 = s.supertype();
				Type t1_ = reinstantiate(t1);
				Type t2_ = reinstantiate(t2);
				terms.add(new SubtypeConstraint_c(t1_, t2_, s.isEqualityConstraint()));
			}
			TypeConstraint_c c_ = new TypeConstraint_c();
			c_.addTerms(terms);
			c = c_;
		}

		return c;
	}

	public XTerm reinstantiateTerm(XTerm t) {
		if (isIdentityInstantiation()) {
			return t;
		}

		int n = typeParameters.size();
		assert typeArguments.size() == n;

		for (int i = 0; i < n; i++) {
			ParameterType pt = typeParameters.get(i);
			Type at = typeArguments.get(0);

			XTerm p = ts.xtypeTranslator().trans(pt);
			XTerm a = ts.xtypeTranslator().trans(at);

			if (p instanceof XRoot) {
				t = t.subst(p, (XRoot) a);
			}
		}

		return t;
	}

	public X10ConstructorInstance reinstantiateCI(X10ConstructorInstance t) {
		final X10ConstructorInstance fi = (X10ConstructorInstance) t.copy();
//		X10ConstructorInstance res = new X10ConstructorInstance_c(fi.typeSystem(), fi.position(), Types.ref(fi.x10Def()));
//		res = (X10ConstructorInstance) res.returnType(reinstantiate(fi.returnType()));
//		res = (X10ConstructorInstance) res.formalTypes(reinstantiate(fi.formalTypes()));
//		res = (X10ConstructorInstance) res.throwTypes(reinstantiate(fi.throwTypes()));
//		res = (X10ConstructorInstance) res.container(reinstantiate(fi.container()));
//		res = (X10ConstructorInstance) res.guard(reinstantiate(fi.guard()));
//		return res;
		return new ReinstantiatedConstructorInstance(this, fi.typeSystem(), fi.position(), Types.ref(fi.x10Def()),
				fi);
	}

	public X10MethodInstance reinstantiateMI(X10MethodInstance t) {
		final X10MethodInstance fi = (X10MethodInstance) t.copy();
//		X10MethodInstance res = new X10MethodInstance_c(fi.typeSystem(), fi.position(), Types.ref(fi.x10Def()));
//		res = (X10MethodInstance) res.returnType(reinstantiate(fi.returnType()));
//		res = (X10MethodInstance) res.formalTypes(reinstantiate(fi.formalTypes()));
//		res = (X10MethodInstance) res.throwTypes(reinstantiate(fi.throwTypes()));
//		res = (X10MethodInstance) res.container(reinstantiate(fi.container()));
//		res = (X10MethodInstance) res.guard(reinstantiate(fi.guard()));
//		return res;
		return new ReinstantiatedMethodInstance(this, fi.typeSystem(), fi.position(), Types.ref(fi.x10Def()), fi);
	}

	public X10FieldInstance reinstantiateFI(X10FieldInstance t) {
		final X10FieldInstance fi = (X10FieldInstance) t.copy();
//		X10FieldInstance res = new X10FieldInstance_c(fi.typeSystem(), fi.position(), Types.ref(fi.x10Def()));
//		res = (X10FieldInstance) res.type(reinstantiate(fi.type()));
//		res = (X10FieldInstance) res.container(reinstantiate(fi.container()));
//		res = (X10FieldInstance) res.guard(reinstantiate(fi.guard()));
//		return res;
		return new ReinstantiatedFieldInstance(this, fi.typeSystem(), fi.position(), Types.ref(fi.x10Def()), fi);
	}

	public <T> List<T> reinstantiate(List<T> list) {
		if (isIdentityInstantiation()) {
			return list;
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
