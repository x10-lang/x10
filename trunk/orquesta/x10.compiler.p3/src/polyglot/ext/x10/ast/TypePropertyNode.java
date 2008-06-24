package polyglot.ext.x10.ast;

import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ext.x10.types.TypeProperty;

public interface TypePropertyNode extends Node {
	Id id();
	TypePropertyNode id(Id id);
	
	TypeProperty typeProperty();
}
