package polyglot.ext.x10.ast;

import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ext.x10.types.TypeProperty;

public interface TypePropertyNode extends Node {
	Id name();
	TypePropertyNode name(Id id);
	
	TypeProperty typeProperty();
	public TypeProperty.Variance variance();
	public TypePropertyNode variance(TypeProperty.Variance variance);
}
