/**
 * 
 */
package x10.types;

import polyglot.types.Ref;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.types.constraints.CConstraint;

/**
 * @author vj
 *
 */
public class ReinstantiatedLocalInstance extends X10LocalInstance_c {
	private static final long serialVersionUID = -4455351212229501992L;

	private final TypeParamSubst typeParamSubst;
	private final X10LocalInstance li;

	ReinstantiatedLocalInstance(TypeParamSubst typeParamSubst, TypeSystem ts, Position pos,
			Ref<? extends X10LocalDef> def, X10LocalInstance li) {
		super(ts, pos, def);
		this.typeParamSubst = typeParamSubst;
		this.li = li;
	}

	@Override
	public Type type() {
		if (type == null)
			return this.typeParamSubst.reinstantiate(li.type());
		return type;
	}

}
