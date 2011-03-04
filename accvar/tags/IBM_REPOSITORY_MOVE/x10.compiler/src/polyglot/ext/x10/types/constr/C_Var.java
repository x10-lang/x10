package polyglot.ext.x10.types.constr;

public interface C_Var extends C_Term {
	
	/** The name of this variable. Only Localvars/specials have names, not field selections.*/
	String name();
	
	/** In case this is a field selection x.f1...fn, return x, x.f1, x.f1.f2, ... x.f1.f2...fn */
	C_Var[] vars();
	/** In case this is a field selection x.f1...fn, return x, else this. */
	C_Var rootVar();
	
	/** In case this is a field selection x.f1...fn, return the path f1...fn, else null. */
	Path path();
}
