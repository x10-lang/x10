/*
 * Created on Oct 1, 2004
 *
 */
package x10.lang;


/**
 * @author Christoph von Praun
 *
 * This class implements the root of the inheritance Hierarchy
 * for objects in an X10 program.
 */
public class X10Object extends java.lang.Object {
	/**
	 * the place where this object was allocated.
	 */
	public final Place place;
	
	/**
	 * The actual type parameters with which this type was instanced.
	 */
	private final Class[] actualTypes_;
	
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
	public X10Object(Class[] actual_types) {
		place = Runtime.here();
		actualTypes_ = actual_types;
	}
	
	public Class getTypeArg_(int i) {
		// must be a generic type
		assert actualTypes_ != null;
		assert i < actualTypes_.length;
		
		return actualTypes_[i];
	}
}
