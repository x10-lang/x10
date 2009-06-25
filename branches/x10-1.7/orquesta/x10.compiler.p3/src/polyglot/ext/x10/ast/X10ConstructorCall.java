package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.TypeNode;

public interface X10ConstructorCall {
	List<TypeNode> typeArguments();

	X10ConstructorCall typeArguments(List<TypeNode> args);


}
