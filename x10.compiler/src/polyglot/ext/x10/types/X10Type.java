/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.types.Type;


/**
 * @author vj
 *
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
    
    /** Succeeds if this type represents an array of booleans.
     * 
     * @return 
     */
    boolean isBooleanArray();
    
    /** Succeeds if this type represents an array of chars.
     * 
     * @return 
     */
    boolean isCharArray();
    
    /** Succeeds if this type represents an array of bytes.
     * 
     * @return 
     */
    boolean isByteArray();
    
    /** Succeeds if this type represents an array of shorts.
     * 
     * @return 
     */
    boolean isShortArray();
    
    /** Succeeds if this type represents an array of longs.
     * 
     * @return 
     */
    boolean isLongArray();
    /** Succeeds if this type represents an array of ints.
     * 
     * @return 
     */
    boolean isIntArray();
    
    /** Succeeds if this type represents an array of floats.
     * 
     * @return 
     */
    boolean isFloatArray();
    
    /** Succeeds if this type represents an array of doubles.
     * 
     * @return 
     */
    boolean isDoubleArray();
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
    
    /** Return a subtype of the basetype with the given
     * depclause and type parameters.
     * 
     * @param d
     * @param g
     * @return
     */
    X10Type makeVariant(DepParameterExpr d, List g);
    X10Type baseType();
    /**
     * Return the depclause of this type or null if none.
     */
    DepParameterExpr depClause();
    List typeParameters();
    boolean isParametric();
    
    /** The list of properties of the class.
     
     * @return
     */
    List/*<PropertyInstance>*/ properties();

}
