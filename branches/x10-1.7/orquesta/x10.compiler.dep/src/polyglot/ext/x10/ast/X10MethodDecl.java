package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.MethodDecl;

public interface X10MethodDecl extends MethodDecl {
    // The representation of this( DepParameterExpr ) in the production.
    DepParameterExpr thisClause();
    X10MethodDecl thisClause(DepParameterExpr e);
    
    // The representation of the : Constraint in the parameter list.
    DepParameterExpr whereClause();
    X10MethodDecl whereClause(DepParameterExpr e);
}
