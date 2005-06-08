/*
 * Created on Nov 30, 2004
 */
package polyglot.ext.x10.types;

import polyglot.ext.jl.types.ReferenceType_c;
import polyglot.main.Report;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/** Implements an X10ReferenceType. We have it inherit from ReferenceType_c because
 * there is a lot of code there, and manually "mix-in" the code from X10Type_c.
 * @author vj
 *
 * 
 */
public abstract class X10ReferenceType_c extends ReferenceType_c implements
		X10ReferenceType {
	
	protected X10ReferenceType_c() {
		super();
	    }

	    public X10ReferenceType_c(TypeSystem ts) {
		this(ts, null);
	    }

	    public X10ReferenceType_c(TypeSystem ts, Position pos) {
		super(ts, pos);
	    }

	// ----------------------------- begin manual mixin code from X10Type_c
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
			Report.report( 5, "[X10ReferenceType_c] isSubTypeImpl |" + this +  "| of |" + t + "|?");	
    	
    	boolean result = ts.equals(this, target) || ts.descendsFrom(this, target);
    	
       	if (result) {
       		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10ReferenceType_c] ..." + result+".");	
     		return result;
    	}
    	if (target.isNullable()) {
    		NullableType toType = target.toNullable();
    		Type baseType = toType.base();
    		result = isSubtypeImpl( baseType );
    		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10ReferenceType_c] ..." + result+".");	
    		return result;
    	}
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10ReferenceType_c] ..." + result+".");	
    	return false;
    }
	// ----------------------------- end manual mixin code from X10Type_c
	
}
