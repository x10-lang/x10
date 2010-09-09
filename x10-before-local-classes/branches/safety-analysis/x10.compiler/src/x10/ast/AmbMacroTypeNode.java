package x10.ast;

import java.util.List;

import polyglot.ast.Ambiguous;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Prefix;
import polyglot.ast.TypeNode;

public interface AmbMacroTypeNode extends TypeNode, Ambiguous {
    Id name();
    AmbMacroTypeNode name(Id name);
    
    Prefix prefix();
    AmbMacroTypeNode prefix(Prefix prefix);

    List<TypeNode> typeArgs();
    AmbMacroTypeNode typeArgs(List<TypeNode> typeArgs);
    
    List<Expr> args();
    AmbMacroTypeNode args(List<Expr> args);
}
