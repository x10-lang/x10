/*
 * Created on Nov 28, 2004
 *
 */
package polyglot.ext.x10.types;

import polyglot.ext.jl.types.NullType_c;
import polyglot.main.Report;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

/** Every X10 term must have a type. This is the type of the X10 term null.
 * Note that there is no X10 type called Null; only the term null.
 * @author vj
 *
 */
public class X10NullType_c extends NullType_c implements X10NullType {
	 /** Used for deserializing types. */
    protected X10NullType_c() { }

    public X10NullType_c( TypeSystem ts ) {
    	super(ts);
    }
    
    /**
     * This is different from the definition of jl.Nullable. X10 does not 
     * assume that every reference type contains null. The type must be nullable
     * for it to contain null.
     * TODO: Check if the result should be just: targetType.isNullable().
     */
    public boolean isImplicitCastValidImpl(Type toType) {
    	X10Type targetType = (X10Type) toType;
    	return toType.isNull() || targetType.isNullable();
    }	

    /** 
     * TOOD: vj -- check if this implementation is correct.
     * The definition of descendsFrom in TypeSystem is
     * Returns true iff child is not ancestor, but child descends from ancestor. 
     * In the X10 type system, the Null type should not descend from any type.
     */
    public boolean descendsFromImpl(Type ancestor) {
    	return ts.equals(ancestor, ts.Object());
        // if (ancestor.isNull()) return false;
        // if (ancestor.isReference()) return true;
        // return false;
    }


    /**
     * Same as isImplicitCastValidImpl.
     **/
    public boolean isCastValidImpl(Type toType) {
    	X10Type targetType = (X10Type) toType;
        return toType.isNull() || targetType.isNullable();
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


	public boolean isDistribution() { return false; }
	public boolean isDistributedArray() { return false; }
	public boolean isPrimitiveTypeArray() { return false; }
	public boolean isBooleanArray() { return false; }
    public boolean isCharArray() { return false; }
    public boolean isByteArray() { return false; }
    public boolean isShortArray() { return false; }
    public boolean isLongArray() { return false; }
    public boolean isIntArray() { return false; }
    public boolean isFloatArray() { return false; }
    public boolean isDoubleArray() { return false; }
	public boolean isClock() { return false; }
	public boolean isRegion() { return false; }
	public boolean isPlace() { return false;}
	public boolean isPoint() { return false; }
	public boolean isX10Array() { return false; }


	public  boolean isSubtypeImpl( Type t) {
    	X10Type target = (X10Type) t;
    	
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10UnknownType_c] isSubTypeImpl |" + this +  "| of |" + t + "|?");	
    	
    	boolean result = ts.equals(this, target) || ts.descendsFrom(this, target);
    	
       	if (result) {
       		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10UnknownType_c] ..." + result+".");	
     		return result;
    	}
    	if (target.isNullable()) {
    		NullableType toType = target.toNullable();
    		Type baseType = toType.base();
    		result = isSubtypeImpl( baseType );
    		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10UnknownType_c] ..." + result+".");	
    		return result;
    	}
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10UnknownType_c] ..." + result+".");	
    	return false;
    }
	// ----------------------------- end manual mixin code from X10Type_c
	public boolean isValueType() {
		return true;
	}
	
}
