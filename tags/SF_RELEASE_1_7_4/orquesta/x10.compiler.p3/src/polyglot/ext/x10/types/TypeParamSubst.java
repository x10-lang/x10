package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.types.LazyRef;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;

public class TypeParamSubst {
    List<Type> typeArguments;
    List<ParameterType> typeParameters;
    X10TypeSystem ts;

    public TypeParamSubst(X10TypeSystem ts, List<Type> typeArguments2, List<ParameterType> typeParameters2) {
        typeArguments2 = typeArguments2 == null ? (List) typeParameters2 : (List) typeArguments2; 
        assert (typeParameters2 == null ? typeArguments2 == null : typeArguments2.size() == typeParameters2.size());
	this.ts = ts;
	this.typeArguments = typeArguments2 == null ? Collections.EMPTY_LIST : typeArguments2;
	this.typeParameters = typeParameters2 == null ? Collections.EMPTY_LIST : typeParameters2;
    }

    public static boolean isSameParameter(ParameterType pt1, ParameterType pt2) {
	return pt1 == pt2 || (Types.get(pt1.def()) == Types.get(pt2.def()) && pt1.name().equals(pt2.name()));
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
	    mt = mt.formalTypes(reinstantiate(mt.formalTypes()));
	    mt = mt.formals(reinstantiate(mt.formals()));
	    mt = mt.guard(reinstantiate(mt.guard()));
	    mt = mt.definedType(reinstantiate(mt.definedType()));
	    return mt;
	}
//	if (t instanceof ClosureType) {
//	    ClosureType ct = (ClosureType) t;
//	    ct = (ClosureType) ct.copy();
//	    ct = ct.closureInstance(reinstantiate(ct.applyMethod()));
//	    return ct;
//	}
	if (t instanceof X10ClassType) {
	    X10ClassType ct = (X10ClassType) t;
	    ct = (X10ClassType) ct.copy();
	    ct = ct.typeArguments(reinstantiate(ct.typeArguments()));
	    if (ct.isMember()) {
		ct = (X10ParsedClassType) ct.container(reinstantiate(ct.container()));
	    }
	    return ct;
	}
	return t;
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
    
    public <T> T reinstantiate(T t) {
	if (t == null)
	    return null;
	if (isIdentityInstantiation()) {
	    return t;
	}
	if (t instanceof Ref) return (T) reinstantiateRef((Ref) t);
	if (t instanceof Type) return (T) reinstantiateType((Type) t);
	if (t instanceof X10FieldInstance) return (T) reinstantiateFI((X10FieldInstance) t);
	if (t instanceof X10MethodInstance) return (T) reinstantiateMI((X10MethodInstance) t);
	if (t instanceof X10ConstructorInstance) return (T) reinstantiateCI((X10ConstructorInstance) t);
	if (t instanceof ClosureInstance) return (T) reinstantiateClosure((ClosureInstance) t);
	if (t instanceof XConstraint) return (T) reinstantiateConstraint((XConstraint) t);
	if (t instanceof XTerm) return (T) reinstantiateTerm((XTerm) t);
	return t;
    }

    public ClosureInstance reinstantiateClosure(ClosureInstance t) {
	ClosureInstance fi = (ClosureInstance) t.copy();
	fi = (ClosureInstance) fi.returnType(reinstantiate(fi.returnType()));
	fi = (ClosureInstance) fi.formalTypes(new ArrayList<Type>(reinstantiate(fi.formalTypes())));
	fi = (ClosureInstance) fi.throwTypes(new ArrayList<Type>(reinstantiate(fi.throwTypes())));
	fi = (ClosureInstance) fi.guard(reinstantiate(fi.guard()));
	return fi;
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

    public static XConstraint reinstantiateConstraint(X10ClassType ct, XConstraint c) {
	if (c == null || c.valid()) return c;
	if (ct instanceof X10ParsedClassType_c) {
	    X10ParsedClassType_c t = (X10ParsedClassType_c) ct;
	    new TypeParamSubst((X10TypeSystem) t.typeSystem(), t.typeArguments, t.x10Def().typeParameters()).reinstantiateConstraint(c);
	}
	return c;
    }

    public XConstraint reinstantiateConstraint(XConstraint c) {
	if (isIdentityInstantiation()) {
	    return c;
	}

	int n = typeParameters.size();
	assert typeArguments.size() == n;

	XTerm[] ys = new XTerm[n];
	XRoot[] xs = new XRoot[n];

	for (int i = 0; i < n; i++) {
	    ParameterType pt = typeParameters.get(i);
	    Type at = typeArguments.get(0);

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

	try {
	    c = c.substitute(ys, xs);
	}
	catch (XFailure e) {
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
	X10ConstructorInstance fi = (X10ConstructorInstance) t.copy();
	fi = (X10ConstructorInstance) fi.returnType(reinstantiate(fi.returnType()));
	fi = (X10ConstructorInstance) fi.formalTypes(reinstantiate(fi.formalTypes()));
	fi = (X10ConstructorInstance) fi.throwTypes(reinstantiate(fi.throwTypes()));
	fi = (X10ConstructorInstance) fi.guard(reinstantiate(fi.guard()));
	fi = (X10ConstructorInstance) fi.container(reinstantiate(fi.container()));
	return fi;
    }

    public X10MethodInstance reinstantiateMI(X10MethodInstance t) {
	X10MethodInstance fi = (X10MethodInstance) t.copy();
	fi = (X10MethodInstance) fi.returnType(reinstantiate(fi.returnType()));
	fi = (X10MethodInstance) fi.formalTypes(new ArrayList<Type>(reinstantiate(fi.formalTypes())));
	fi = (X10MethodInstance) fi.throwTypes(reinstantiate(fi.throwTypes()));
	fi = (X10MethodInstance) fi.guard(reinstantiate(fi.guard()));
	fi = (X10MethodInstance) fi.container(reinstantiate(fi.container()));
	return fi;
    }

    public X10FieldInstance reinstantiateFI(X10FieldInstance t) {
	X10FieldInstance fi = (X10FieldInstance) t.copy();
	fi = (X10FieldInstance) fi.type(reinstantiate(fi.type()));
	fi = (X10FieldInstance) fi.guard(reinstantiate(fi.guard()));
	fi = (X10FieldInstance) fi.container(reinstantiate(fi.container()));
	return fi;
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
