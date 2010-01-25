package x10.types;

import polyglot.types.Type;



/**
 * Interface implemented by types that may represent an X10 struct, viz subtypes of ConstrainedType or X10ClassType.
 * 
 * @author vj
 *
 */
public interface X10Struct extends Type {
	/**
	 * Is this type an X10 struct type?
	 */
	boolean isX10Struct();
	X10Struct makeX10Struct();


}
