/*
 * Created on Oct 1, 2004
 *
 */
package x10.lang;

import x10.base.TypeArgument;


/**
 * @author Christoph von Praun
 *
 * This class implements the root of the inheritance Hierarchy
 * for objects in an X10 program.
 */
public class X10Object {
	/**
	 * the place where this object was allocated.
	 */
	public final Place place;
	
	/**
	 * The actual type parameters with which this type was instanced.
	 */
	private final TypeArgument[] actualTypeArguments_;
	
	/**
	 * Convenience constructor that is only used for instances of non-generic. 
	 * types.
	 */
	public X10Object() {
		this(null);
	}
	
	/**
	 * @param actual_types Holds an array that specifies the actual classes
	 *                     passed as type parameters when this instance was created.
	 * 	                   If the class is not generic, the null is passed.  
	 */
	public X10Object(TypeArgument[] actual_type_args) {
		place = Runtime.here();
		actualTypeArguments_ = actual_type_args;
	}
	
	public TypeArgument getActualTypeArguments(int i) {
		// must be a generic type
		assert actualTypeArguments_ != null;
		assert i < actualTypeArguments_.length;
		 
		return actualTypeArguments_[i];
	}
}
