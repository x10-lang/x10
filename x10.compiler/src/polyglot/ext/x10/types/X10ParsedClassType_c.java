/*
 * Created on Nov 30, 2004
 *
 */
package polyglot.ext.x10.types;

import polyglot.ext.jl.types.ParsedClassType_c;
import polyglot.frontend.Source;
import polyglot.main.Report;
import polyglot.types.LazyClassInitializer;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

/**
 * @author vj
 *
 */
public class X10ParsedClassType_c extends ParsedClassType_c implements
		X10ParsedClassType {

	/**
	 * 
	 */
	public X10ParsedClassType_c() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ts
	 * @param init
	 * @param fromSource
	 */
	public X10ParsedClassType_c(TypeSystem ts, LazyClassInitializer init,
			Source fromSource) {
		super(ts, init, fromSource);
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
	
	/** Returns true iff the type implements x10.lang.Indexable.
	 * 
	 */
	public boolean isX10Array() { 
		return ts.isSubtype(this, ((X10TypeSystem) ts).Indexable());
	}
	
	public boolean isPrimitiveTypeArray() {
		return 
            isBooleanArray() || 
            isCharArray() || 
            isByteArray() || 
            isShortArray() || 
            isIntArray() || 
            isLongArray() ||
            isFloatArray() ||
            isDoubleArray();
	}
    
	public boolean isDistributedArray() {
		return 
        isBooleanArray() || 
        isCharArray() || 
        isByteArray() || 
        isShortArray() || 
        isIntArray() || 
        isLongArray() ||
        isFloatArray() ||
        isDoubleArray();
	}

	public boolean isBooleanArray() {
        X10TypeSystem xts = (X10TypeSystem) ts;
        return xts.isSubtype( this, xts.booleanArray()); 
    }
    public boolean isCharArray() {
        X10TypeSystem xts = (X10TypeSystem) ts;
        return xts.isSubtype( this, xts.charArray()); 
    }
    public boolean isByteArray() {
        X10TypeSystem xts = (X10TypeSystem) ts;
        return xts.isSubtype( this, xts.byteArray()); 
    }
    public boolean isShortArray() {
        X10TypeSystem xts = (X10TypeSystem) ts;
        return xts.isSubtype( this, xts.shortArray()); 
    }
    /**
	 * Returns true if this type is a subtype of intArray.
	 * implies isX10Array().
	 */
	public boolean isIntArray() {
		X10TypeSystem xts = (X10TypeSystem) ts;
		return xts.isSubtype( this, xts.intArray()); 
	}
	
	public boolean isValueType() {
		X10TypeSystem xts = (X10TypeSystem) ts;
		return xts.isSubtype( this, xts.value()); 
	}
	/**
	 * Returns true if this type is a subtype of longArray.
	 * implies isX10Array().
	 */
	public boolean isLongArray() {
		X10TypeSystem xts = (X10TypeSystem) ts;
		return xts.isSubtype( this, xts.longArray()); 
	}
	public boolean isFloatArray() {
        X10TypeSystem xts = (X10TypeSystem) ts;
        return xts.isSubtype( this, xts.floatArray());
    }
    /**
	 * Returns true if this type is a subtype of doubleArray.
	 * implies isX10Array().
	 */
	public boolean isDoubleArray() {
		X10TypeSystem xts = (X10TypeSystem) ts;
		return xts.isSubtype( this, xts.doubleArray());
	}
	public boolean isClock() {
		X10TypeSystem xts = (X10TypeSystem) ts;
		return xts.isSubtype( this, xts.clock());
	}
	public boolean isPoint() {
		X10TypeSystem xts = (X10TypeSystem) ts;
		return xts.isSubtype( this, xts.point());
	}
	public boolean isPlace() {
		X10TypeSystem xts = (X10TypeSystem) ts;
		return xts.isSubtype( this, xts.place());
	}
	public boolean isRegion() {
		X10TypeSystem xts = (X10TypeSystem) ts;
		return xts.isSubtype( this, xts.region());
	}
	public boolean isDistribution() {
		X10TypeSystem xts = (X10TypeSystem) ts;
		return xts.isSubtype( this, xts.distribution());
	}

	
	public  boolean isSubtypeImpl( Type t) {
    	X10Type target = (X10Type) t;
    	
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10ParsedClassType_c] isSubTypeImpl |" + this +  "| of |" + t + "|?");	
    	
    	boolean result = ts.equals(this, target);
        result = result || ts.descendsFrom(this, target);
    	
       	if (result) {
       		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10ParsedClassType_c] ..." + result+".");	
     		return result;
    	}
    	if (target.isNullable()) {
    		NullableType toType = target.toNullable();
    		Type baseType = toType.base();
    		result = isSubtypeImpl( baseType );
    		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10ParsedClassType_c] ..." + result+".");	
    		return result;
    	}
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10ParsedClassType_c] ..." + result+".");	
    	return false;
    }
	// ----------------------------- end manual mixin code from X10Type_c
	
	// ugh... toString() is being used to write out code..!!
	
	 public boolean isImplicitCastValidImpl(Type toType) {
	 	if (toType.isArray()) return false;
	 	X10Type targetType = (X10Type) toType;
        if (! targetType.isClass() && ! targetType.isNullable()) 
        	return false;
        boolean result = ts.isSubtype(this, targetType);
        return result;
    }

    /** Returns true iff a cast from this to <code>toType</code> is valid. */
    public boolean isCastValidImpl(Type toType) {
    	X10TypeSystem xts = (X10TypeSystem) ts;
    	X10Type targetType = (X10Type) toType;
    	
    	boolean result = (toType.isPrimitive() && ts.equals(this, xts.X10Object()));
    	if (result) return result;
    	if (targetType.isNullable()) {
    		NullableType type = targetType.toNullable();
    		return isCastValidImpl( type.base() );
    	}
        if (targetType.isFuture()) {
            // If we can cast the Future into this type, we can do the reverse
            return targetType.isCastValidImpl(this);
        }
        return super.isCastValidImpl(toType);
    }
}
