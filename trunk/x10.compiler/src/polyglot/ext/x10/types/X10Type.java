/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import polyglot.types.Type;


/**
 * @author vj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface X10Type extends Type {
	
    
    /** Added for the X10 type system. Returns true if the type is of the form nullable X, for some type X.
     * @author vj
     * 
     */
    boolean isNullable( );
    
    /** Added for the X10 type system. Returns true if the type is of the form future X, for some type X.
     * @author vj
     * 
     */
    boolean isFuture( );

    NullableType toNullable();
    FutureType toFuture();
    boolean isX10Array();
    X10ArrayType toX10Array();
    

}
