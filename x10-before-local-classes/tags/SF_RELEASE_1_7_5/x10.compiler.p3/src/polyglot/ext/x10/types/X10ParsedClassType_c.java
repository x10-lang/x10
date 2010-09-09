/*
 *
 * (C) Copyright IBM Corporation 2006-2008
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
import polyglot.types.FieldInstance;
import polyglot.types.Matcher;
import polyglot.types.MemberInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.ParsedClassType_c;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import x10.constraint.XRoot;
import x10.constraint.XVar;

/** 6/2006 Modified so that every type is now potentially generic and dependent.
 * @author vj
 */
public class X10ParsedClassType_c extends ParsedClassType_c
implements X10ParsedClassType
{
    TypeParamSubst subst;
    
    public int hashCode() {
        return def.hashCode();
    }
    
    @Override
    public boolean equalsImpl(TypeObject o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (o instanceof X10ParsedClassType_c) {
            X10ParsedClassType_c t = (X10ParsedClassType_c) o;
            
            if (def != t.def) {
                if (def == null || t.def == null)
                    return false;
                else if (! def.equals(t.def))
                    return false;
            }
            
            if (typeArguments != t.typeArguments) {
                if (typeArguments == null)
                    return t.typeArguments == null || t.typeArguments.isEmpty();
                else if (t.typeArguments == null)
                    return typeArguments.isEmpty();
                else if (! typeArguments.equals(t.typeArguments))
                    return false;
            }

            return true;
        }
        return false;
    }
    
    public
    TypeParamSubst subst() {
        if (subst == null)
            subst = new TypeParamSubst((X10TypeSystem) ts, typeArguments, x10Def().typeParameters());
        return subst;
    }
    
    public X10ParsedClassType_c(ClassDef def) {
        super(def);
        subst = null;
    }

    public X10ParsedClassType_c(TypeSystem ts, Position pos, Ref<? extends ClassDef> def) {
        super(ts, pos, def);
        subst = null;
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
	
	boolean hasParams() {
	    return x10Def().typeParameters() != null && x10Def().typeParameters().size() != 0;
	}
	
	@Override
	public Type superClass() {
	    Type sup = super.superClass();
	    Type base = X10TypeMixin.baseType(sup);
	    if (base instanceof X10ClassType) {
	        XRoot supVar = ((X10ClassType) base).x10Def().thisVar();
	        XRoot thisVar = x10Def().thisVar();
	        try {
	            sup = Subst.subst(sup, thisVar, supVar);
	        }
	        catch (SemanticException e) {
	        }
	    }
	    if (!hasParams())
		return sup;
	    TypeParamSubst subst = subst();
	    return subst.reinstantiate(sup);
	}
	
	@Override
	public List<Type> interfaces() {
	    List<Type> interfaces = super.interfaces();
	    List<Type> newInterfaces = new ArrayList<Type>(interfaces.size());
	    for (Type sup : interfaces) {
	        Type base = X10TypeMixin.baseType(sup);
	        if (base instanceof X10ClassType) {
	            XRoot supVar = ((X10ClassType) base).x10Def().thisVar();
	            XRoot thisVar = x10Def().thisVar();
	            try {
	                sup = Subst.subst(sup, thisVar, supVar);
	            }
	            catch (SemanticException e) {
	            }
	        }
	        newInterfaces.add(sup);
	    }

	    if (!hasParams())
	        return newInterfaces;
	    TypeParamSubst subst = subst();
	    return subst.reinstantiate(newInterfaces);
	}

	public boolean isIdentityInstantiation() {
	    if (!hasParams())
		return true;
            TypeParamSubst subst = subst();
	    return subst.isIdentityInstantiation();
	}

	@Override
	public List<FieldInstance> fields() {
	    if (!hasParams())
	        return super.fields();
            TypeParamSubst subst = subst();
	    return subst.reinstantiate(super.fields());
	}

	@Override
	public List<MethodInstance> methods() {
	    if (!hasParams())
	        return super.methods();
            TypeParamSubst subst = subst();
	    return subst.reinstantiate(super.methods());
	}
	@Override
	public List<ConstructorInstance> constructors() {
	    if (!hasParams())
	        return super.constructors();
            TypeParamSubst subst = subst();
	    return subst.reinstantiate(super.constructors());
	}
	@Override
	public List<MemberInstance<?>> members() {
	    if (!hasParams())
	        return super.members();
            TypeParamSubst subst = subst();
	    return subst.reinstantiate(super.members());
	}
	@Override
	public List<ClassType> memberClasses() {
	    if (!hasParams())
	        return super.memberClasses();
            TypeParamSubst subst = subst();
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
	    List<Type> l = new TransformingList<TypeDef, Type>(x10Def().memberTypes(), new TypeDefAsMacroTypeTransform());
	    if (!hasParams())
	        return l;
            TypeParamSubst subst = subst();
	    return subst.reinstantiate(l);
	}
	
	List<Type> typeArguments;
	
	public List<Type> typeArguments() {
	    if (typeArguments == null) {
		return TypedList.copyAndCheck((List) x10Def().typeParameters(), Type.class, true);
	    }
	    return typeArguments;
	}
	
	public X10ParsedClassType typeArguments(List<Type> typeArgs) {
	    X10ParsedClassType_c n = (X10ParsedClassType_c) copy();
	    n.typeArguments = TypedList.copyAndCheck(typeArgs, Type.class, false);
	    n.subst = null;
	    return n;
	}
	
	@Override
	public Named memberTypeMatching(Matcher<Named> matcher) {
	    Named n = super.memberTypeMatching(matcher);
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

