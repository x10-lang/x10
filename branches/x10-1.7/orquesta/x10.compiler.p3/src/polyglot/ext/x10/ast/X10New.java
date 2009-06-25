package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.New;
import polyglot.ast.TypeNode;

public interface X10New extends New {
	List<TypeNode> typeArguments();

	X10New typeArguments(List<TypeNode> args);


}
