package polyglot.ext.x10.types.constr;

import java.io.Serializable;

import polyglot.ast.Variable;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.Type;

public interface C_Term extends Serializable, C_Receiver {
	public static final X10TypeSystem typeSystem = X10TypeSystem_c.getTypeSystem();
	public static final TypeTranslator translator = typeSystem.typeTranslator();
	Type type();
	
}
