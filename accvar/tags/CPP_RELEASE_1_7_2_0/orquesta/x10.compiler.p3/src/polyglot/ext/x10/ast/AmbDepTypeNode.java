package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Ambiguous;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Prefix;
import polyglot.ast.TypeNode;

public interface AmbDepTypeNode extends TypeNode, Ambiguous {
    Id name();
    AmbDepTypeNode name(Id name);
    
    Prefix prefix();
    AmbDepTypeNode prefix(Prefix prefix);

    List<TypeNode> typeArgs();
    AmbDepTypeNode typeArgs(List<TypeNode> typeArgs);
    
    List<Expr> args();
    AmbDepTypeNode args(List<Expr> args);
    
    DepParameterExpr constraint();
    AmbDepTypeNode constraint(DepParameterExpr dep);
}
