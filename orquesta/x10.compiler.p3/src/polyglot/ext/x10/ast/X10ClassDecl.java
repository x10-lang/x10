package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.ClassDecl;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.TypeDef;

public interface X10ClassDecl extends ClassDecl {
	DepParameterExpr classInvariant();
	X10ClassDecl classInvariant(DepParameterExpr classInvariant);
	
	List<TypePropertyNode> typeProperties();
	X10ClassDecl typeProperties(List<TypePropertyNode> typeProperties);

	TypeNode constrainedSuperClass();
	X10ClassDecl constrainedSuperClass(TypeNode tn);
	
	List<TypeNode> constrainedInterfaces();
	X10ClassDecl constrainedInterfaces(List<TypeNode> ts);
}
