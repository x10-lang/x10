/*
 * Created on Nov 30, 2004
 *
 */
package polyglot.ext.x10.types;

import polyglot.ext.jl.types.Type_c;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.main.Report;

/** This class is added for the sake of symmetry, but may not be used very much.
 * Most ..ext.x10.type.X10*Type classes actually subclass from corresponding
 * ..ext.jl.type.*Type classes, and manually add the methods to implement X10Type.
 * Only those X10*Type classes which dont may extend X10Type_c.
 * 
 * TODO: Check all the other predicates from Type_c and determine which of them need to 
 * be redefined here.
 * 
 * @author vj
 *
 * 
 */
public abstract class X10Type_c extends Type_c implements X10Type {
	
	
	  /** Added for the X10 type system.
     * @author vj
     * @return
     */
    public boolean isNullable() { return false; }
    
    /** Added for the X10 type system.
     * @author vj
     * @return
     */
    public boolean isFuture() { return false; }
    
	/** Returns a non-null iff isFuture() returns true. */
    public FutureType toFuture() {
	return null;
    }
    
    /** Returns a non-null iff isNullable() returns true. */
    public NullableType toNullable() {
	return null;
    }
    
    public boolean isX10Array() {
    	return false;
    }
    public X10ArrayType toX10Array() {
    	return null;
    }
    
    public  boolean isSubtypeImpl( Type t) {
    	X10Type target = (X10Type) t;
    	
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10Type_c] isSubTypeImpl |" + this +  "| of |" + t + "|?");	
    	
    	boolean result = ts.equals(this, target) || ts.descendsFrom(this, target);
    	
       	if (result) {
       		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10Type_c] ..." + result+".");	
     		return result;
    	}
    	if (target.isNullable()) {
    		NullableType toType = target.toNullable();
    		Type baseType = toType.base();
    		result = isSubtypeImpl( baseType );
    		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10Type_c] ..." + result+".");	
    		return result;
    	}
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10Type_c] ..." + result+".");	
    	return false;
    }
    
   
}
