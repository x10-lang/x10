package polyglot.ext.x10.ast;

import polyglot.ast.Id;
import polyglot.ast.Term;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.types.Ref;
import polyglot.types.Type;

public interface TypeParamNode extends Term {
	Id name();
	TypeParamNode name(Id id);

	ParameterType type();
	TypeParamNode type(ParameterType type);
	public TypeProperty.Variance variance();
	public TypeParamNode variance(TypeProperty.Variance variance);
}
