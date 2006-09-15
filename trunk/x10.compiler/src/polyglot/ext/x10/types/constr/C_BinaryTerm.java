package polyglot.ext.x10.types.constr;

public interface C_BinaryTerm extends C_Term {
	String op();
	C_Term left();
	C_Term right();
}
