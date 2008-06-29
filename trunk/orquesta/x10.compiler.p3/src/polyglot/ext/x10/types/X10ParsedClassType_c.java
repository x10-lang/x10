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
import polyglot.types.FieldAsTypeTransform;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Named;
import polyglot.types.ParsedClassType_c;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.TransformingList;

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

    public X10ClassType superClassRoot() {
        return (X10ClassType) (isJavaType() ? ts.Object() : ((X10TypeSystem) ts).X10Object());
    }
	
    public X10ClassDef x10Def() {
        return (X10ClassDef) def();
    }

	public boolean isJavaType() {
	    return def().fromJavaClassFile();
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
	
	@Override
	public Named memberTypeNamed(String name) {
		Named n = super.memberTypeNamed(name);
		if (n == null)
			return typePropertyNamed(name);
		return null;
	}
	
	public String toString() {
	    return super.toString();
	}
	
	boolean isX10Array;
	boolean isX10ArraySet;
	public boolean isX10Array() {
		if (isX10ArraySet) return isX10Array;
		isX10ArraySet = true;
		
		return isX10Array=((X10TypeSystem) typeSystem()).isX10Array(this);
	}

	    
}

