/**
 * 
 */
package x10.types;

import polyglot.types.Ref;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.types.constraints.CConstraint;

final class ReinstantiatedFieldInstance extends X10FieldInstance_c {
	/**
	 * 
	 */
	private final TypeParamSubst typeParamSubst;
	private final X10FieldInstance fi;

	ReinstantiatedFieldInstance(TypeParamSubst typeParamSubst, TypeSystem ts, Position pos,
			Ref<? extends X10FieldDef> def, X10FieldInstance fi) {
		super(ts, pos, def);
		this.typeParamSubst = typeParamSubst;
		this.fi = fi;
	}

	@Override
	public Type type() {
		if (type == null)
			return this.typeParamSubst.reinstantiate(fi.type());
		return type;
	}

	@Override
	public CConstraint guard() {
		if (guard == null)
			return this.typeParamSubst.reinstantiate(fi.guard());
		return guard;
	}

	@Override
	public StructType container() {
		if (container == null)
			return this.typeParamSubst.reinstantiate(fi.container());
		return container;
	}
}