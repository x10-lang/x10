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
	public boolean isX10Array() { 
		return ts.isSubtype(this, ((X10TypeSystem) ts).Indexable());
	}
	

	
	public  boolean isSubtypeImpl( Type t) {
    	X10Type target = (X10Type) t;
    	
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10ParsedClassType_c] isSubTypeImpl |" + this +  "| of |" + t + "|?");	
    	
    	boolean result = ts.equals(this, target) || ts.descendsFrom(this, target);
    	
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
        return super.isCastValidImpl(toType);
    }
}
