/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 26, 2004
 */
package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;


/**
 * Implementation of the nullable type constructor.
 *
 * @author vj
 */
public class NullableType_c extends X10ReferenceType_c implements NullableType {

	protected X10NamedType base;
    
	// RMF 11/2/2005 --
	// Attempting to initialize these (unused) fields at construction time causes a
	// chicken-and-egg problem for any recursive type with a nullable field of the
	// owner's type (e.g. class Node { nullable Node link; }): can't initialize the
	// type of the nullable field without knowing all the fields/methods first!
//	protected List fields;
//	protected List methods;
	// IP 2/9/2006 -- This is unused
//	protected List interfaces;
	
	// TODO: vj add dep clauses.

	/** Used for deserializing types. */
	protected NullableType_c() { }

	public String name() {
		return base.name();
	}
	public String fullName() {
		return base.fullName();
	}
	public boolean propertiesElaborated() {
		return true;
	}
	/**
	 * Factory method for producing nullable types. Implements nullable nullable X = nullable X.
	 *
	 * @param ts
	 * @param pos
	 * @param base -- the base type for the nullable type.
	 * @return
	 */
	public static NullableType makeNullable(TypeSystem ts, Position pos, X10NamedType base) {
		if (base instanceof NullableType)
			return (NullableType) base;
		//System.out.println("NullableType_c:makeNullable: base=" + base);
		return new NullableType_c(ts, pos, base);
	}
	
@Override
public Object copy() {
	// TODO Auto-generated method stub
	return super.copy();
}

	/**
	 * Constructor is made private. Called only in the factory method above.
	 *
	 * @param ts
	 * @param pos
	 * @param base
	 */
	private NullableType_c(TypeSystem ts, Position pos, X10NamedType base) {
		super(ts, pos);
		this.base = base;
		assert base != null;

//		this.methods = base.methods();
//		this.fields = base.fields();
//		this.interfaces = base.interfaces();
	}

	
	public X10NamedType base() {
		return this.base;
	}

	/** Set the base type. */
	public NullableType base(X10NamedType base) {
		if (base == this.base)
			return this;
		NullableType_c n = (NullableType_c) copy();
		n.base = base;
		return n;
	}

	public void print(CodeWriter w) {
		// [IP] FIXME: is this the right thing to do here?
		w.write("/"+"*"+"nullable<"+"*"+"/");
		base.print(w);
		w.write("/"+"*"+">"+"*"+"/");
	}

	public String toString() {
		return toStringForDisplay();
	}
	public String toStringForDisplay() {
		return " nullable<"  + base.toStringForDisplay() +">";
	}

	private static String getStackTrace() {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] trace = new Throwable().getStackTrace();
		for (int i=2; i < trace.length; i++)
			sb.append("\t").append(trace[i]).append("\n");
		return sb.toString();
	}

	/**
	 * This needs to be changed so that the right boxed class is produced for
	 * primitive types.
	 * [IP] TODO: Fix this.
	 * @author vj
	 */
	public String translate(Resolver c) {
		return base.translate(c);
	}

	public boolean isCanonical() {
		return base.isCanonical();
	}

	public List methods() {
		if (base instanceof X10ReferenceType)
			return ((X10ReferenceType) base).methods();
		return Collections.EMPTY_LIST;
	}

	public List fields() {
		if (base instanceof X10ReferenceType)
			return ((X10ReferenceType) base).fields();
		return Collections.EMPTY_LIST;
	}

	public FieldInstance fieldNamed(String name) {
		if (base instanceof X10ReferenceType)
			return ((X10ReferenceType) base).fieldNamed(name);
		return null;
	}

	public Type superType() {
		if (base instanceof X10ReferenceType)
			return ((X10ReferenceType) base).superType();
		X10TypeSystem ts = (X10TypeSystem) base.typeSystem();
		return ts.X10Object();
	}

	public List interfaces() {
		if (base instanceof X10ReferenceType)
			return ((X10ReferenceType) base).interfaces();
		return Collections.EMPTY_LIST;
	}

    public boolean typeEqualsImpl(Type o) {
        return equalsImpl(o);
    }
    public int hashCode() {
        return 
          (rootType == this ? base.hashCode() << 2 : rootType.hashCode() ) 
        + (depClause != null ? depClause.hashCode() : 0)
        + ((typeParameters !=null && ! typeParameters.isEmpty()) ? typeParameters.hashCode() :0);
        
    }
	

	public boolean equalsImpl(TypeObject t) {
		if (t instanceof NullableType) {
			NullableType a = (NullableType) t;
			return ts.equals(base, a.base());
		}
		return false;
		
	}

	public boolean isImplicitCastValidImpl(Type toType) {
		X10Type other = (X10Type) toType;
        X10TypeSystem xts = (X10TypeSystem) other.typeSystem();
		if (xts.isNullable(other)) {
			NullableType target = X10Type_c.toNullable(other);
			return base.isImplicitCastValidImpl(target.base());
		}
		return super.isImplicitCastValidImpl(toType);
	}

	/**
	 * This method only test if java types are compatibles for the cast.
	 * Hence even a potentially invalid cast as ((T1) nullable T2) is accepted.
	 * However it is the responsability of X10Cast class to generates code
	 * that checks expression value is not null.
	 */
	public boolean isCastValidImpl(Type toType) {
        X10Type other  = (X10Type) toType;
        X10TypeSystem xts = (X10TypeSystem) other.typeSystem();
    	return base.isCastValidImpl(other);
    	// return (xts.isNullable(other) && base.isCastValidImpl(X10Type_c.toNullable(other).base()));
    }
	
	public boolean isArray() {
     return base.isArray();   
    }
    public ArrayType toArray() { return base.toArray();}
    
    public List properties() {
        //return base.properties();
    	 return Collections.EMPTY_LIST;
    }
    public List<FieldInstance> definedProperties() {
        return Collections.EMPTY_LIST;
    }
    
    public NullableType toNullable() { return this;}
    public FutureType toFuture() { return X10Type_c.toFuture(this);}
    public boolean safe() { return base.safe();}

    public boolean isValue() {
    	return base.isValue();
    }
}

