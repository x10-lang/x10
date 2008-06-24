package polyglot.ext.x10.ast;

import polyglot.ast.Id;
import polyglot.ast.Term;
import polyglot.types.Ref;
import polyglot.types.Type;

public interface TypeParamNode extends Term {
	Id id();
	TypeParamNode id(Id id);

	/**
	 * Return the type of this node, or null if no type has been
	 * assigned yet.
	 */
	Ref<? extends Type> typeRef();
}
