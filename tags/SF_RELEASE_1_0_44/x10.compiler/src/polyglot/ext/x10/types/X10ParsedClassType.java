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
	X10ParsedClassType makeVariant();
	
	
	
	/** Returns true iff superClassRoot() equals ts.Object().
	 * @return
	 */
	boolean isJavaType();
	
	/**
	 * Returns true iff this type is an X10 array.
	 * @return
	 */
	boolean isX10Array();
	
	/**
	 * Returns true iff this type is an X10 array defined over a rectangular region.
	 * @return
	 */
	boolean isRect();
	void setRect();
	
	/**
	 * Returns true iff this type is an X10 array defined over a local distribution.
	 */
	boolean hasLocalProperty();
	C_Var onePlace();
	void setOnePlace(C_Var t);
	
	/** 
	 * Returns true iff this type is an X10 array defined over a region that starts with zero in each dimension.
	 * @return
	 */
	boolean isZeroBased();
	void setZeroBased();
	
	boolean isRail();
	void setRail();
	/**
	 * If this type is an array/region/distribution, returns the rank of the array. Returns null if rank is not 
	 * defined for this type.
	 * @return
	 */
	C_Var rank();
	void setRank(C_Var rank);
	
	boolean isRankOne();
	boolean isRankTwo();
	boolean isRankThree();
	
	C_Var self();
	
	void setDistribution(C_Var dist);
	
	C_Var distribution();
	
	
	/**
	 * Check whether the real clause associated with this type is invalid. Throw the
	 * associated semantic exception if it is.
	 * @throws SemanticException
	 */
	void checkRealClause() throws SemanticException;
}
