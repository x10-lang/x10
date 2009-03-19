package polyglot.ext.x10.ast;

import polyglot.ast.ClassDecl;
import polyglot.ast.TypeNode;

public interface X10ClassDecl extends ClassDecl {
	DepParameterExpr classInvariant();
}
