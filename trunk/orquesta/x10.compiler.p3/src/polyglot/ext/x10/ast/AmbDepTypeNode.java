package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Ambiguous;
import polyglot.ast.Expr;
import polyglot.ast.TypeNode;

public interface AmbDepTypeNode extends TypeNode, Ambiguous {
    TypeNode base();
    AmbDepTypeNode base(TypeNode base);

    List<TypeNode> typeArgs();
    AmbDepTypeNode typeArgs(List<TypeNode> typeArgs);
    
    List<Expr> args();
    AmbDepTypeNode args(List<Expr> args);
    
    DepParameterExpr constraint();
    AmbDepTypeNode constraint(DepParameterExpr dep);
}
