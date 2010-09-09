package polyglot.ext.x10.ast;

import polyglot.ast.Ambiguous;
import polyglot.ast.TypeNode;

public interface AmbDepTypeNode extends TypeNode, Ambiguous {

    TypeNode base();
    AmbDepTypeNode base(TypeNode base);

    DepParameterExpr dep();
    AmbDepTypeNode dep(DepParameterExpr dep);

    GenParameterExpr gen();
    AmbDepTypeNode gen(GenParameterExpr gen);
}
