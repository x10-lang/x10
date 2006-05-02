/*
 * Created on Oct 1, 2004
 *
 */
package x10.lang;

import x10.base.TypeArgument;
import x10.cluster.X10RemoteRef;
import x10.compilergenerated.Parameter1;

/**
 * @author Christoph von Praun
 *
 * This class implements the root of the inheritance Hierarchy
 * for objects in an X10 program.
 */
public class Object implements Parameter1/*, Serializable */{
	/**
	 * the place where this object was allocated.
	 */
	transient public /*final*/ place location;
	
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
		rref = null;
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
	
	/**
	 * Representation of a remote reference in X10.
	 */
	public X10RemoteRef rref = null;
}
