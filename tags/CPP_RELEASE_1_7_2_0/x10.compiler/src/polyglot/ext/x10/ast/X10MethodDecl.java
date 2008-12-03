package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.MethodDecl;

public interface X10MethodDecl extends MethodDecl {
	  // The representation of this( DepParameterExpr ) in the production.
    DepParameterExpr thisClause();
    // The reprsentation of the : Constraint in the parameter list.
    Expr whereClause();
    X10MethodDecl whereClause(Expr e);
}
