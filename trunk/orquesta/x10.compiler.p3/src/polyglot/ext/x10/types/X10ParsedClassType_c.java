/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 30, 2004
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldAsTypeTransform;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.LazyRef;
import polyglot.types.MemberInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.ParsedClassType_c;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;

/** 6/2006 Modified so that every type is now potentially generic and dependent.
 * @author vj
 */
public class X10ParsedClassType_c extends ParsedClassType_c
implements X10ParsedClassType
{
    public X10ParsedClassType_c(ClassDef def) {
        super(def);
    }

    public X10ParsedClassType_c(TypeSystem ts, Position pos, Ref<? extends ClassDef> def) {
        super(ts, pos, def);
    }
    
    /** Property initializers, used in annotations. */
    List<Expr> propertyInitializers;
    public List<Expr> propertyInitializers() {
        if (propertyInitializers == null)
            return Collections.EMPTY_LIST;
        return Collections.unmodifiableList(propertyInitializers);
    }
    public Expr propertyInitializer(int i) {
        return propertyInitializers().get(i);
    }
    public X10ClassType propertyInitializers(List<Expr> inits) {
        X10ParsedClassType_c  n = (X10ParsedClassType_c) copy();
        n.propertyInitializers = inits;
        return n;
    }

    public List<X10ClassType> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }

    public List<X10ClassType> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }
    protected SemanticException realClauseInvalid;
	
    public X10ClassDef x10Def() {
        return (X10ClassDef) def();
    }

	public boolean isJavaType() {
	    return def().fromJavaClassFile();
	}
	
	@Override
	public Type superClass() {
	    return reinstantiate(super.superClass());
	}
	
	private Type reinstantiateType(Type t) {
	    if (t instanceof ParameterType) {
		ParameterType pt = (ParameterType) t;
		for (int i = 0; i < x10Def().typeParameters().size(); i++) {
		    ParameterType pt2 = x10Def().typeParameters().get(i);
		    if (i < typeArguments().size()) {
			if (pt.typeEquals(pt2)) {
			    return typeArguments().get(i);
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
		mt = mt.whereClause(reinstantiate(mt.whereClause()));
		mt = mt.definedType(reinstantiate(mt.definedType()));
		return mt;
	    }
	    if (t instanceof ClosureType) {
		ClosureType ct = (ClosureType) t;
		ct = (ClosureType) ct.copy();
		ct = ct.closureInstance(reinstantiate(ct.closureInstance()));
		return ct;
	    }
	    if (t instanceof X10ParsedClassType) {
		X10ParsedClassType ct = (X10ParsedClassType) t;
		ct = (X10ParsedClassType) ct.copy();
		return ct.typeArguments(reinstantiate(ct.typeArguments()));
	    }
	    return t;
	}

	@Override
	public List<Type> interfaces() {
	    return reinstantiate(super.interfaces());
	}
	
	public boolean isIdentityInstantiation() {
	    int n = x10Def().typeParameters().size();
	    if (n == 0 && typeArguments == null) return true;
	    if (typeArguments == null) return false;
	    if (n == 0 && typeArguments.size() == 0) return true;
	    if (n != typeArguments.size()) return false;
	    for (int i = 0; i < n; i++) {
		ParameterType pt = x10Def().typeParameters().get(i);
		Type at = typeArguments.get(0);
		if (pt.typeEquals(at)) {
		    return true;
		}
	    }
	    return false;
	}

	private <T> T reinstantiate(T t) {
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
	
	private ClosureInstance reinstantiateClosure(ClosureInstance t) {
	    ClosureInstance fi = (ClosureInstance) t.copy();
	    fi = (ClosureInstance) fi.returnType(reinstantiate(fi.returnType()));
	    fi = (ClosureInstance) fi.formalTypes(new ArrayList<Type>(reinstantiate(fi.formalTypes())));
	    fi = (ClosureInstance) fi.throwTypes(new ArrayList<Type>(reinstantiate(fi.throwTypes())));
	    fi = (ClosureInstance) fi.whereClause(reinstantiate(fi.whereClause()));
	    return fi;
	}
	private Ref reinstantiateRef(final Ref t) {
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
		return t.reinstantiateConstraint(c);
	    }
	    return c;
	}

	private XConstraint reinstantiateConstraint(XConstraint c) {
	    if (isIdentityInstantiation()) {
		return c;
	    }
	    
	    int n = x10Def().typeParameters().size();
	    assert typeArguments().size() == n;

	    XTerm[] ys = new XTerm[n];
	    XRoot[] xs = new XRoot[n];
	    
	    for (int i = 0; i < n; i++) {
		ParameterType pt = x10Def().typeParameters().get(i);
		Type at = typeArguments().get(0);
		
		X10TypeSystem xts = (X10TypeSystem) this.ts;
		XTerm p = xts.xtypeTranslator().trans(pt);
		XTerm a = xts.xtypeTranslator().trans(at);
		
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
	private XTerm reinstantiateTerm(XTerm t) {
	    if (isIdentityInstantiation()) {
		return t;
	    }
	    
	    int n = x10Def().typeParameters().size();
	    assert typeArguments().size() == n;

	    for (int i = 0; i < n; i++) {
		ParameterType pt = x10Def().typeParameters().get(i);
		Type at = typeArguments().get(0);
		
		X10TypeSystem xts = (X10TypeSystem) this.ts;
		XTerm p = xts.xtypeTranslator().trans(pt);
		XTerm a = xts.xtypeTranslator().trans(at);
		
		if (p instanceof XRoot) {
		    t = t.subst(p, (XRoot) a);
		}
	    }

	    return t;
	}

	private X10ConstructorInstance reinstantiateCI(X10ConstructorInstance t) {
	    X10ConstructorInstance fi = (X10ConstructorInstance) t.copy();
	    fi = fi.returnType(reinstantiate(fi.returnType()));
	    fi = (X10ConstructorInstance) fi.formalTypes(reinstantiate(fi.formalTypes()));
	    fi = (X10ConstructorInstance) fi.throwTypes(reinstantiate(fi.throwTypes()));
	    fi = (X10ConstructorInstance) fi.whereClause(reinstantiate(fi.whereClause()));
	    return fi;
	}

	private X10MethodInstance reinstantiateMI(X10MethodInstance t) {
	    X10MethodInstance fi = (X10MethodInstance) t.copy();
	    fi = fi.returnType(reinstantiate(fi.returnType()));
	    fi = (X10MethodInstance) fi.formalTypes(new ArrayList<Type>(reinstantiate(fi.formalTypes())));
	    fi = (X10MethodInstance) fi.throwTypes(reinstantiate(fi.throwTypes()));
	    fi = (X10MethodInstance) fi.whereClause(reinstantiate(fi.whereClause()));
	    return fi;
	}
	
	private X10FieldInstance reinstantiateFI(X10FieldInstance t) {
	    X10FieldInstance fi = (X10FieldInstance) t.copy();
	    fi = (X10FieldInstance) fi.type(reinstantiate(fi.type()));
	    fi = (X10FieldInstance) fi.whereClause(reinstantiate(fi.whereClause()));
	    return fi;
	}

	private <T> List<T> reinstantiate(List<T> list) {
	    if (isIdentityInstantiation()) {
		return list;
	    }
	    return new TransformingList<T, T>(list, new Transformation<T, T>() {
		public T transform(T o) {
		    return reinstantiate(o);
		}		
	    });
	}
	
	@Override
	public List<FieldInstance> fields() {
	    return reinstantiate(super.fields());
	}
	
	@Override
	public List<MethodInstance> methods() {
	    return reinstantiate(super.methods());
	}
	@Override
	public List<ConstructorInstance> constructors() {
	    return reinstantiate(super.constructors());
	}
	@Override
	public List<MemberInstance<?>> members() {
	    return reinstantiate(super.members());
	}
	@Override
	public List<ClassType> memberClasses() {
	    return reinstantiate(super.memberClasses());
	}

	/**
	 * A parsed class is safe iff it explicitly has a flag saying so.
	 */
	public boolean safe() {
		return X10Flags.toX10Flags(flags()).isSafe();
	}
	
	public List<FieldInstance> definedProperties() {
	    return new TransformingList<FieldDef, FieldInstance>(x10Def().properties(), new FieldAsTypeTransform());
	}

	public List<FieldInstance> properties() {
	    Type superType = superClass();
	    if (superType instanceof X10ClassType) {
	        List<FieldInstance> l = ((X10ClassType) superType).properties();
	        List<FieldInstance> l2 = new ArrayList<FieldInstance>();
	        l2.addAll(l);
	        l2.addAll(definedProperties());
	        return l2;
	    }
	    return definedProperties();
	}
	
	public List<Type> typeMembers() {
	    return new TransformingList<TypeDef, Type>(x10Def().memberTypes(), new TypeDefAsMacroTypeTransform());
	}
	
	public List<Type> typeMembersNamed(String name) {
	    List<Type> ts = new ArrayList<Type>();
	    for (TypeDef p : x10Def().memberTypes()) {
		if (name.equals(p.name())) {
		    ts.add(p.asType());
		}
	    }
	    return ts;
	}
	
	public List<Type> typeProperties() {
	    return new TransformingList<TypeProperty, Type>(x10Def().typeProperties(), new TypePropertyAsPathTypeTransform());
	}
	
	
	public Named typePropertyNamed(String name) {
		for (TypeProperty p : x10Def().typeProperties()) {
			if (name.equals(p.name())) {
				return (Named) p.asType();
			}
		}
		return null;
	}

	List<Type> typeArguments;
	
	public List<Type> typeArguments() {
	    if (typeArguments == null) {
		typeArguments = TypedList.copyAndCheck((List) x10Def().typeParameters(), Type.class, true);
	    }
	    return typeArguments;
	}
	
	public X10ParsedClassType typeArguments(List<Type> typeArgs) {
	    X10ParsedClassType_c n = (X10ParsedClassType_c) copy();
	    n.typeArguments = TypedList.copyAndCheck(typeArgs, Type.class, false);
	    return n;
	}
	
	@Override
	public Named memberTypeNamed(String name) {
	    Named n = super.memberTypeNamed(name);
	    if (n == null)
		n = typePropertyNamed(name);
	    if (n == null) {
		for (Type t : typeMembersNamed(name)) {
		    if (t instanceof MacroType) {
			MacroType mt = (MacroType) t;
			if (mt.formals().size() == 0 && mt.typeParameters().size() == 0) {
			    n = mt;
			    break;
			}
		    }
		}
	    }
	    return n;
	}
	
	public String toString() {
	    return super.toString() + (typeArguments == null || typeArguments.isEmpty() ? "" : typeArguments.toString());
	}
	
	boolean isX10Array;
	boolean isX10ArraySet;
	public boolean isX10Array() {
		if (isX10ArraySet) return isX10Array;
		isX10ArraySet = true;
		
		return isX10Array=((X10TypeSystem) typeSystem()).isX10Array(this);
	}
	
	    
}

