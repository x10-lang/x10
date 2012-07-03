package x10.constraint.xsmt;

public interface SmtFuncSymbol extends SmtVariable {
	String getName(); 
	int getArity(); 
	SmtType getArgumentType(int i);
	SmtType getResultType(); 
}
