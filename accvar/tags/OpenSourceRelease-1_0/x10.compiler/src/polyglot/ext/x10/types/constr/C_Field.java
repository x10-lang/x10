package polyglot.ext.x10.types.constr;

import polyglot.types.FieldInstance;

/**
 * A representation of a Field.
 * @author vj
 *
 */
public interface C_Field extends C_Var {

	C_Var receiver();
	String name();
	FieldInstance fieldInstance();
}
