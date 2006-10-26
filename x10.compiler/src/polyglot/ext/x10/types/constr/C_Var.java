package polyglot.ext.x10.types.constr;

public interface C_Var extends C_Term {
	String name();
	C_Var findRootVar();
}
