/*
 * Created on Nov 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import polyglot.ext.jl.types.NullType_c;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

/**
 * @author vj
 *
 */
public class X10NullType_c extends NullType_c {
	 /** Used for deserializing types. */
    protected X10NullType_c() { }

    public X10NullType_c( TypeSystem ts ) {
    	super(ts);
    }
    
    public boolean isImplicitCastValidImpl(Type toType) {
    	return toType.isNull() || toType.isNullable();
	    }

}
