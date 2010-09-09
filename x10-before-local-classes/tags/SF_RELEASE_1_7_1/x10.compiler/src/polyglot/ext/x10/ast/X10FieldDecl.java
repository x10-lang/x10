package polyglot.ext.x10.ast;

import polyglot.ast.FieldDecl;

public interface X10FieldDecl extends FieldDecl {
	DepParameterExpr thisClause();
}
