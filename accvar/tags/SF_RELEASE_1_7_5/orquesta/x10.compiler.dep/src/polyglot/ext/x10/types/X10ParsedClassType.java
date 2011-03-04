/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 30, 2004
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;

/**
 * @author vj
 *
 *
 */
public interface X10ParsedClassType extends ParsedClassType, X10ClassType, X10NamedType {

    /**
     * The root of the superclass hierarchy -- either ts.X10Object() or ts.Object().
     * Must return ts.X10Object() if it encounters ts.x10Object() on its way
     * from this type up the superType() links. Otherwise must return ts.Object()
     * @return
     */
    X10ClassType superClassRoot();

    /** Returns true iff superClassRoot() equals ts.Object().
     * @return
     */
    boolean isJavaType();

    /**
     * Returns true iff this type is an X10 array.
     * @return
     */
    boolean isX10Array();
    
    public X10ParsedClassType transferRegionProperties(X10ParsedClassType arg);
    
    /**
     * Returns true iff this type is an X10 array defined over a rectangular region.
     * @return
     */
    boolean isRect();
    X10ParsedClassType setRect();
    
    /**
     * Returns true iff this type is an X10 array defined over a local distribution.
     */
    boolean hasLocalProperty();
    C_Var onePlace();
    X10ParsedClassType setOnePlace(C_Var t);
    
    /** 
     * Returns true iff this type is an X10 array defined over a region that starts with zero in each dimension.
     * @return
     */
    boolean isZeroBased();
    X10ParsedClassType setZeroBased();
    
    /**
     * Add the clause rank==1 && rect && isZeroBased to this.
     *
     */
    X10ParsedClassType setZeroBasedRectRankOne();

    /**
     * If this type is an array/region/distribution, returns the rank of the array. Returns null if rank is not 
     * defined for this type.
     * @return
     */
    C_Var rank();
    X10ParsedClassType setRank(C_Var one);
    boolean isRankOne();
    boolean isRankTwo();
    boolean isRankThree();

    C_Var region();
    X10ParsedClassType setRegion(C_Term v);
    
    /** 
     * Returns true iff this type is an X10 distribution that maps all points to one place.
     * @return
     */
    boolean isConstantDist();
    X10ParsedClassType setConstantDist();

    /** 
     * Returns true iff this type is an X10 distribution that maps exactly one point to each place.
     * @return
     */
    boolean isUniqueDist();
    X10ParsedClassType setUniqueDist();
    
    C_Var distribution();
    X10ParsedClassType setDistribution(C_Term v);

    boolean isRail();
    X10ParsedClassType setRail();

    C_Var self();
}
