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
import polyglot.types.Matcher;
import polyglot.types.MemberInstance;
import polyglot.types.MethodAsTypeTransform;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.ParsedClassType_c;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Name;
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

    public List<Type> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }

    public List<Type> annotationsMatching(Type t) {
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
	    TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, typeArguments, x10Def().typeParameters());
	    return subst.reinstantiate(super.superClass());
	}

	@Override
	public List<Type> interfaces() {
	    TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, typeArguments, x10Def().typeParameters());
	    return subst.reinstantiate(super.interfaces());
	}
	
	public boolean isIdentityInstantiation() {
	    TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, typeArguments(), x10Def().typeParameters());
	    return subst.isIdentityInstantiation();
	}

	@Override
	public List<FieldInstance> fields() {
	    TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, typeArguments(), x10Def().typeParameters());
	    return subst.reinstantiate(super.fields());
	}
	
	@Override
	public List<MethodInstance> methods() {
	    TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, typeArguments(), x10Def().typeParameters());
	    return subst.reinstantiate(super.methods());
	}
	@Override
	public List<ConstructorInstance> constructors() {
	    TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, typeArguments(), x10Def().typeParameters());
	    return subst.reinstantiate(super.constructors());
	}
	@Override
	public List<MemberInstance<?>> members() {
	    TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, typeArguments(), x10Def().typeParameters());
	    return subst.reinstantiate(super.members());
	}
	@Override
	public List<ClassType> memberClasses() {
	    TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, typeArguments(), x10Def().typeParameters());
	    return subst.reinstantiate(super.memberClasses());
	}

	/**
	 * A parsed class is safe iff it explicitly has a flag saying so.
	 */
	public boolean safe() {
		return X10Flags.toX10Flags(flags()).isSafe();
	}

	public static class X10FieldAsTypeTransform implements Transformation<X10FieldDef, FieldInstance> {
	    public FieldInstance transform(X10FieldDef def) {
		return def.asInstance();
	    }
	}

	public List<FieldInstance> definedProperties() {
	    return new TransformingList<X10FieldDef, FieldInstance>(x10Def().properties(), new X10FieldAsTypeTransform());
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
	
	public List<Type> typeProperties() {
	    return new TransformingList<TypeProperty, Type>(x10Def().typeProperties(), new TypePropertyAsPathTypeTransform());
	}
	
	public PathType typePropertiesMatching(Matcher<Named> matcher) {
		for (TypeProperty p : x10Def().typeProperties()) {
		    PathType t = p.asType();
		    try {
		        Named n = matcher.instantiate(t);
		        if (n instanceof PathType)
		            return (PathType) n;
		    }
		    catch (SemanticException e) {
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
	public Named memberTypeMatching(Matcher<Named> matcher) {
	    Named n = super.memberTypeMatching(matcher);
	    if (n != null)
	        return n;

	    n = typePropertiesMatching(matcher);
	    if (n != null)
	        return n;
	    
	    n = typeMemberMatching(matcher);
	    if (n != null)
	        return n;
	    
	    return null;
	}
	
	public MacroType typeMemberMatching(Matcher<Named> matcher) {
	    for (Type t : typeMembers()) {
	        if (t instanceof MacroType) {
	            MacroType mt = (MacroType) t;
	            try {
	                Named n = matcher.instantiate(mt);
	                if (n instanceof MacroType)
	                    return (MacroType) n;
	            }
	            catch (SemanticException e) {
	            }
		}
	    }
	    
	    return null;
	}
	
	public String toString() {
	    if (propertyInitializers != null) {
		String s = propertyInitializers.toString();
		return super.toString() + "(" + s.substring(1, s.length()-1) + ")";
	    }
	    return super.toString() + (typeArguments == null || typeArguments.isEmpty() ? "" : typeArguments.toString());
	}
	    
}

