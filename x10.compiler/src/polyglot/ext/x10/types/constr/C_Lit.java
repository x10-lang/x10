package polyglot.ext.x10.types.constr;

import polyglot.ext.x10.types.X10TypeSystem_c;

public interface C_Lit extends C_Term {
	
	public static final C_Lit_c TRUE = new C_Lit_c(new Boolean(true), 
			C_Term.typeSystem.Boolean());
	public static final C_Lit_c FALSE = new C_Lit_c(new Boolean(false), 
			C_Term.typeSystem.Boolean());
	Object val();
	C_Lit not();
}
