/*
 * Created on Nov 30, 2004
 *
 * 
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ext.jl.types.ArrayType_c;
import polyglot.ext.jl.types.ReferenceType_c;
import polyglot.main.Report;
import polyglot.types.ArrayType;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.ast.Expr;
import polyglot.ext.x10.ast.DepParameterExpr;

/** An immutable representation of an X10 Array type. All arrays in
 *  X10 are of this type.
 * TODO: This version does not implement jagged arrays.
 * @author vj
 * 
 */
public class X10ArrayType_c extends ReferenceType_c implements X10ArrayType {
	protected Type base;
	protected List fields;
	protected List methods;
	protected List interfaces;
	protected DepParameterExpr expr;
	protected boolean isValue;
	
	/**
	 * 
	 */
	protected X10ArrayType_c() {
		super();	
	}
	
	/**
	 * @param ts
	 * @param pos
	 * @param base
	 */
	public X10ArrayType_c(TypeSystem ts, Position pos, Type base) {
		this( ts, pos, base, false, null );
	}
	
	public X10ArrayType_c( TypeSystem ts, Position pos, Type base, boolean isValue) {
		this( ts, pos, base, isValue, null );
	}
	
	public X10ArrayType_c( TypeSystem ts, Position pos, Type base, DepParameterExpr expr) {
		this( ts, pos, base, false, expr );
	}
	public X10ArrayType_c( TypeSystem ts, Position pos, Type base, boolean isValue, DepParameterExpr expr  ) {
		super( ts, pos );
		this.base = base;
		this.expr = expr;
		this.isValue = isValue;
	}
	
	void init() {
		if (methods == null) {
			methods = new ArrayList(1);
			
			// Add method public Object clone()
			methods.add(ts.methodInstance(position(),
					this,
					ts.Public(),
					ts.Object(),
					"clone",
					Collections.EMPTY_LIST,
					Collections.EMPTY_LIST));
		}
		
		if (fields == null) {
			fields = new ArrayList(4);
			
			// Add field public final int length
			fields.add(ts.fieldInstance(position(),
					this,
					ts.Public().Final(),
					ts.Int(),
			"rank"));
			fields.add(ts.fieldInstance(position(),
					this,
					ts.Public().Final(),
					((X10TypeSystem) ts).region(),
			"region"));
			fields.add(ts.fieldInstance(position(),
					this,
					ts.Public().Final(),
					((X10TypeSystem) ts).distribution(),
			"distribution"));
		}
		
		if (interfaces == null) {
			interfaces = new ArrayList(2);
			interfaces.add(ts.Cloneable());
			interfaces.add(ts.Serializable());
		}
	}
	
	/** Get the base type of the array. */
	public Type base() {
		return this.base;
	}
	
	/** Set the base type of the array. */
	public X10ArrayType base(Type base) {
		if (base == this.base)
			return this;
		X10ArrayType_c n = (X10ArrayType_c) copy();
		n.base = base;
		return n;
	}
	
	
	public String toString() {
		StringBuffer s = new StringBuffer(base() == null? "?" : base.toString());
		if (isValue) s.append(" value ");
		s.append("[." + (expr == null? "" : expr.toString()) + "]");
		return s.toString();
	}
	
	/** Translate the type. 
	 * TODO: Fix for other types than int and double. */
	public String translate(Resolver c) {
		if (base().isInt()) {
			return "x10.array.sharedmemory.IntArray_c";
		}
		if (base().isDouble()) {
			return "x10.array.sharedmemory.DoubleArray_c";
		}
		throw new Error("TODO: Make X10ArrayType support more than int and double.") ;
	}
	
	/** Returns true iff the type is canonical. */
	public boolean isCanonical() {
		return base().isCanonical();
	}
	
	/** This is not a Java Array.
	 * 
	 */
	public boolean isX10Array() { return true; }
	public X10ArrayType toX10Array() { return this; }

	
	/** Get the methods implemented by the array type. */
	public List methods() {
		init();
		return Collections.unmodifiableList(methods);
	}
	
	/** Get the fields of the array type. */
	public List fields() {
		init();
		return Collections.unmodifiableList(fields);
	}
	
	/** Get the clone() method. */
	public MethodInstance cloneMethod() {
		return (MethodInstance) methods().get(0);
	}
	
	/** Get a field of the type by name. */
	public FieldInstance fieldNamed(String name) {
		FieldInstance fi = lengthField();
		return name.equals(fi.name()) ? fi : null;
	}
	
	/** Get the length field. */
	public FieldInstance lengthField() {
		return (FieldInstance) fields().get(0);
	}
	
	/** Get the super type of the array type. */
	public Type superType() {
		return ts.Object();
	}
	
	/** Get the interfaces implemented by the array type. */
	public List interfaces() {
		init();
		return Collections.unmodifiableList(interfaces);
	}
	
	public int hashCode() {
		return base().hashCode() << 1;
	}
	
	public boolean equalsImpl(TypeObject t) {
		if (t instanceof X10ArrayType) {
			X10ArrayType a = (X10ArrayType) t;
			return ts.equals(base(), a.base()) && isValue == a.isValue();
		}
		return false;
	}
	
	public boolean isImplicitCastValidImpl(Type toType) {
		if (toType.isArray()) return false;
		X10Type targetType = (X10Type) toType;
		if (targetType.isX10Array()) {
			X10ArrayType other = targetType.toX10Array();
			if ((isValue == other.isValue()) && (base().isPrimitive() || other.base().isPrimitive())) {
				return ts.equals(base(), other.base());
			}
			return ts.isImplicitCastValid(base(), other.base());
		}
		
		// toType is not an array, but this is.  Check if the array
		// is a subtype of the toType.  This happens when toType
		// is java.lang.Object.
		return ts.isSubtype(this, toType);
	}
	
	/**
	 * Requires: all type arguments are canonical.  ToType is not a NullType.
	 *
	 * Returns true iff a cast from this to toType is valid; in other
	 * words, some non-null members of this are also members of toType.
	 **/
	public boolean isCastValidImpl(Type toType) {
		if (! toType.isReference()) return false;
		if (toType.isArray()) return false;
		X10Type target = (X10Type) toType;
		if (target.isX10Array()) {
			X10ArrayType other = target.toX10Array();
			Type fromBase = base();
			Type toBase = other.base();
			
			if (fromBase.isPrimitive()) return ts.equals(toBase, fromBase);
			if (toBase.isPrimitive()) return false;
			
			if (fromBase.isNull()) return false;
			if (toBase.isNull()) return false;
			
			// Both are reference types.
			return ts.isCastValid(fromBase, toBase);
		}
		
		// Ancestor is not an array, but child is.  Check if the array
		// is a subtype of the ancestor.  This happens when ancestor
		// is java.lang.Object.
		return ts.isSubtype(this, toType);
	}
	//	 ----------------------------- begin manual mixin code from X10Type_c
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isNullable()
	 */
	public boolean isNullable() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isFuture()
	 */
	public boolean isFuture() {
		return false;
	}
	public boolean isValue() {
		return isValue;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#toNullable()
	 */
	public NullableType toNullable() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#toFuture()
	 */
	public FutureType toFuture() {
		return null;
	}
	
	
	public  boolean isSubtypeImpl( Type t) {
		X10Type target = (X10Type) t;
		
		if (Report.should_report("debug", 5))
			Report.report( 5, "[X10ArrayType_c] isSubTypeImpl |" + this +  "| of |" + t + "|?");	
		
		boolean result = ts.equals(this, target) || ts.descendsFrom(this, target);
		
		if (result) {
			if (Report.should_report("debug", 5))
				Report.report( 5, "[X10ArrayType_c] ..." + result+".");	
			return result;
		}
		if (target.isNullable()) {
			NullableType toType = target.toNullable();
			Type baseType = toType.base();
			result = isSubtypeImpl( baseType );
			if (Report.should_report("debug", 5))
				Report.report( 5, "[X10ArrayType_c] ..." + result+".");	
			return result;
		}
		if (Report.should_report("debug", 5))
			Report.report( 5, "[X10ArrayType_c] ..." + result+".");	
		return false;
	}
	// ----------------------------- end manual mixin code from X10Type_c
	
	
}
