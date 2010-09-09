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
    /** Succeeds if this type implements Indexable.
     * 
     * @return 
     */
    boolean isX10Array();
    
    /**
     * Succeeds if this type represents an array with the arithmetic operations add, sub, mul, div defined on it.
     * @return
     */
    boolean isPrimitiveTypeArray();
    /**
     * Succeeds if this type represents an array with a distribution, that is, an array possibly 
     * distributed across multiple places.
     * @return true if this type has a distribution.
     */
    boolean isDistributedArray();
    /** Succeeds if this type is a subtype of x10.lang.point.
     * 
     * @return
     */
    boolean isPoint();
    /**
     * Succeeds if this type is a subtype of x10.lang.place.
     * @return
     */
    boolean isPlace();
    /**
     * Succeeds if this type is a subtype of x10.lang.dist
     * @return
     */
    boolean isDistribution();
    /**
     * Succeeds if this type is a subtype of x10.lang.region.
     * @return
     */
    boolean isRegion();
    /**
     * Succeeds if this type is a subtype of x10.lang.clock.
     * @return
     */
    boolean isClock();
    
    boolean isValueType();
    

}
