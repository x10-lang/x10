package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.TypeNode;

public interface X10Call extends Call {
	List<TypeNode> typeArguments();

	X10Call typeArguments(List<TypeNode> args);


}
