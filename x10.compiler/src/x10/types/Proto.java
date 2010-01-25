package x10.types;

import polyglot.types.Type;


/**
 * Interface implemented by types that may be proto types, viz ConstrainedType or X10ClassType.
 * 
 * @author vj
 *
 */
public interface Proto extends Type {

	boolean isProto();

	/**
	 * Return T if this is proto T; else return this.
	 * @return
	 */
	Proto baseOfProto();

	/**
	 * @return this if this is proto T; else proto this
	 */
	Proto makeProto();  
}
