/*
 * Created on Nov 30, 2004
 *
 */
package polyglot.ext.x10.types;

import polyglot.ext.jl.types.ClassType_c;
import polyglot.main.Report;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.types.Type;


/**
 * @author vj
 */
public abstract class X10ClassType_c extends ClassType_c implements
		X10ClassType {

	/**
	 * 
	 */
	public X10ClassType_c() {
		super();
	}

	/**
	 * @param ts
	 */
	public X10ClassType_c(TypeSystem ts) {
		super(ts);
	}

	/**
	 * @param ts
	 * @param pos
	 */
	public X10ClassType_c(TypeSystem ts, Position pos) {
		super(ts, pos);
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

	 public boolean isImplicitCastValidImpl(Type toType) {
	 	X10Type targetType = (X10Type) toType;
        if (! targetType.isClass() && ! targetType.isNullable()) 
        	return false;
        boolean result = ts.isSubtype(this, targetType);
        return result;
    }
	
	public  boolean isSubtypeImpl( Type t) {
    	X10Type target = (X10Type) t;
    	
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10ClassType_c] isSubTypeImpl |" + this +  "| of |" + t + "|?");	
    	
    	boolean result = ts.equals(this, target) || ts.descendsFrom(this, target);
    	
       	if (result) {
       		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10ClassType_c] ..." + result+".");	
     		return result;
    	}
    	if (target.isNullable()) {
    		NullableType toType = target.toNullable();
    		Type baseType = toType.base();
    		result = isSubtypeImpl( baseType );
    		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10ClassType_c] ..." + result+".");	
    		return result;
    	}
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10ClassType_c] ..." + result+".");	
    	return false;
    }
	// ----------------------------- end manual mixin code from X10Type_c
	
}
