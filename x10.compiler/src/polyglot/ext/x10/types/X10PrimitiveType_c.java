/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import polyglot.ext.jl.types.PrimitiveType_c;
import polyglot.main.Report;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

/** X10 has no primitive types. Types such as int etc are all value class types. 
 * However, this particular X10 implementation uses Java primitive types to implement some of
 * X10's value class types, namely, char, boolean, byte, int etc etc. It implements other
 * value class types as Java classes.
 * 
 * Thus this class represents one of specially implemented X10 value class types.
 * @author praun
 * @author vj
 */
public class X10PrimitiveType_c extends PrimitiveType_c implements X10PrimitiveType {

	/** Used for deserializing types. */
    protected X10PrimitiveType_c() { }

    public X10PrimitiveType_c(TypeSystem ts, Kind kind) {
        super(ts, kind);
    }

    /** Every X10 value type descends from X10.lang.Object, the base class.
     *
     */
    public boolean descendsFromImpl(Type ancestor) {
    	X10TypeSystem xts = (X10TypeSystem) ts;
        return ts.equals(ancestor, xts.X10Object());
    }

    /** Return true if this type can be assigned to <code>toType</code>. */
    public boolean isImplicitCastValidImpl(Type toType) {
    	X10TypeSystem xts = (X10TypeSystem) ts;
        return ts.equals(toType, xts.X10Object()) ||
               super.isImplicitCastValidImpl(toType);
    }

    /** Returns true iff a cast from this to <code>toType</code> is valid. */
    public boolean isCastValidImpl(Type toType) {
    	X10TypeSystem xts = (X10TypeSystem) ts;
        return ts.equals(toType, xts.Object()) || super.isCastValidImpl(toType);
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
	public boolean isX10Array() { return false; }
	public X10ArrayType toX10Array() { return null; }

	/**
	 * Note that this (general) mix-in code correctly takes care of ensuring that
	 * int is a subtype of nullable int as well as x10.lang.X10Object.
	 */
	
	public  boolean isSubtypeImpl( Type t) {
    	X10Type target = (X10Type) t;
    	
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10PrimitiveType_c] isSubTypeImpl |" + this +  "| of |" + t + "|?");	
    	
    	boolean result = ts.equals(this, target) || ts.descendsFrom(this, target);
    	
       	if (result) {
       		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10PrimitiveType_c] ..." + result+".");	
     		return result;
    	}
    	if (target.isNullable()) {
    		NullableType toType = target.toNullable();
    		Type baseType = toType.base();
    		result = isSubtypeImpl( baseType );
    		if (Report.should_report("debug", 5))
    			Report.report( 5, "[X10PrimitiveType_c] ..." + result+".");	
    		return result;
    	}
    	if (Report.should_report("debug", 5))
			Report.report( 5, "[X10PrimitiveType_c] ..." + result+".");	
    	return false;
    }
	// ----------------------------- end manual mixin code from X10Type_c
	
	
}
