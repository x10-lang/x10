package polyglot.ext.x10.ast;

import polyglot.ast.CanonicalTypeNode;

public interface X10CanonicalTypeNode extends CanonicalTypeNode {
    public DepParameterExpr constraintExpr();
    public X10CanonicalTypeNode constraintExpr(DepParameterExpr e);
}
