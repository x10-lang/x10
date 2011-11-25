/**
 * 
 */
package x10.types;

import java.util.List;

import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;

final class ReinstantiatedClosureInstance_c extends ClosureInstance_c {
	private static final long serialVersionUID = 5526029137687309137L;

	private final TypeParamSubst typeParamSubst;
	private final ClosureInstance fi;

	ReinstantiatedClosureInstance_c(TypeParamSubst typeParamSubst, TypeSystem ts, Position pos,
			Ref<? extends ClosureDef> def, ClosureInstance fi) {
		super(ts, pos, pos, def);
		this.typeParamSubst = typeParamSubst;
		this.fi = fi;
	}

	@Override
	public Ref<? extends Type> returnTypeRef() {
		if (returnType == null)
			return this.typeParamSubst.reinstantiate(fi.returnTypeRef());
		return returnType;
	}

	@Override
	public Type returnType() {
		if (returnType == null)
			return this.typeParamSubst.reinstantiate(fi.returnType());
		return returnType.get();
	}

	@Override
	public List<Type> formalTypes() {
		if (formalTypes == null)
			return this.typeParamSubst.reinstantiate(fi.formalTypes());
		return formalTypes;
	}

	@Override
	public CConstraint guard() {
		if (guard == null)
			return this.typeParamSubst.reinstantiate(fi.guard());
		return guard;
	}

	@Override
	public TypeConstraint typeGuard() {
	    if (typeGuard == null)
	        return this.typeParamSubst.reinstantiate(fi.typeGuard());
	    return typeGuard;
	}
}
