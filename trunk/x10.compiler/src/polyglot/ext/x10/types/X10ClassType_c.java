/*
 * Created on Nov 30, 2004
 *
 */
package polyglot.ext.x10.types;

import polyglot.ext.jl.types.ClassType_c;
import polyglot.main.Report;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;


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


	 public boolean isImplicitCastValidImpl(Type toType) {
	 	X10Type targetType = (X10Type) toType;
        if (! targetType.isClass() && targetType.toNullable() != null) 
        	return false;
        boolean result = ts.isSubtype(this, targetType);
        return result;
    }
     
     public boolean typeEqualsImpl(Type o) {
            return equalsImpl(o);
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
        NullableType toType = target.toNullable();
    	if (toType != null) {
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
     public NullableType toNullable() { return X10Type_c.toNullable(this);}
        public FutureType toFuture() { return X10Type_c.toFuture(this);}
	// ----------------------------- end manual mixin code from X10Type_c
	
}
