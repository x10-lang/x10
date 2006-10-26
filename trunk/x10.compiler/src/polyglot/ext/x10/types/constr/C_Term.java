package polyglot.ext.x10.types.constr;

import java.io.Serializable;

import polyglot.ast.Variable;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.Type;

public interface C_Term extends Serializable {
	//public static final  X10TypeSystem typeSystem = X10TypeSystem_c.getTypeSystem();
	//public static final TypeTranslator translator = typeSystem.typeTranslator();
	Type type();
	
	/**
	 * Return the result of substituting value for var in this.
	 * @param value -- the value to be substituted
	 * @param var  -- the variable which is being substituted for
	 * @return  the term with the substitution applied
	 */
	
	C_Term substitute(C_Term value, C_Term var);
	
}
