/*
 * Created on Nov 30, 2004
 *
 * 
 */
package polyglot.ext.x10.types;

import polyglot.ext.jl.types.ArrayType_c;
import polyglot.main.Report;
import polyglot.types.Type;
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
public class X10ArrayType_c extends ArrayType_c implements X10ArrayType {

    protected DepParameterExpr expr;
    protected boolean isValue;
	
	/**
	 * 
	 */
	public X10ArrayType_c() {
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
		super( ts, pos, base);
		this.expr = expr;
		this.isValue = isValue;
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
