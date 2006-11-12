package polyglot.ext.x10.types.constr;

public interface C_UnaryTerm extends C_Term {
	String op();
	C_Term arg();
}
