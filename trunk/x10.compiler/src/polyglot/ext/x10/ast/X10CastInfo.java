package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;
import polyglot.types.Type;

public interface X10CastInfo {

	public boolean isPrimitiveCast();

	public boolean notNullRequired();

	public boolean isDepTypeCheckingNeeded();
	
	public Type type();

	public boolean isToTypeNullable();
	
    public TypeNode getTypeNode();

	public Expr expr();
}
