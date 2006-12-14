/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 1, 2004
 *
 */
package x10.lang;

import x10.base.TypeArgument;
import x10.compilergenerated.Parameter1;

/**
 * @author Christoph von Praun
 *
 * This class implements the root of the inheritance Hierarchy
 * for objects in an X10 program.
 */
public class Object implements Parameter1 {
	/**
	 * the place where this object was allocated.
	 */
	public final place location;

	private long _uniqueTag;// Used in distributed mode

	// create a (hopefully) unique id using hashcode
	// and place id.  hashcode might not work in extrodinary
	// situations--should find something more robust for
	// production implementation
	// TODO: cmd find more robust implementation
	public long setUniqueTag(){return _uniqueTag = (location.id << 32) | hashCode(); }
	public long getUniqueTag(){ return _uniqueTag;}
	public long setUniqueTag(long t){ return _uniqueTag=t;}
	
	/**
	 * The actual type parameters with which this type was instanced.
	 */
	private final TypeArgument[] actualTypeArguments_;
	
	/**
	 * Convenience constructor that is only used for instances of non-generic. 
	 * types.
	 */
	public Object() {
		this(null);
	}
	
	/**
	 * @param actual_types Holds an array that specifies the actual classes
	 *                     passed as type parameters when this instance was created.
	 * 	                   If the class is not generic, the null is passed.  
	 */
	public Object(TypeArgument[] actual_type_args) {
		location = (this instanceof ValueType) ? null : Runtime.here();
		actualTypeArguments_ = actual_type_args;
	}
	
	public place getLocation() {
		return (this instanceof ValueType) ? Runtime.here() : location;
	}
	public TypeArgument getActualTypeArguments(int i) {
		// must be a generic type
		assert actualTypeArguments_ != null;
		assert i < actualTypeArguments_.length;
		 
		return actualTypeArguments_[i];
	}
}
