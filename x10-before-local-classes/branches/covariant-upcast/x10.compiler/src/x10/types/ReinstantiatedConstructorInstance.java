/**
 * 
 */
package x10.types;

import java.util.List;

import polyglot.types.Ref;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.types.constraints.CConstraint;

public final class ReinstantiatedConstructorInstance extends
		X10ConstructorInstance_c {
	/**
	 * 
	 */
	private final TypeParamSubst typeParamSubst;
	private final X10ConstructorInstance fi;

	ReinstantiatedConstructorInstance(TypeParamSubst typeParamSubst, TypeSystem ts, Position pos,
			Ref<? extends X10ConstructorDef> def, X10ConstructorInstance fi) {
		super(ts, pos, def);
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
	public List<Type> throwTypes() {
		if (throwTypes == null)
			return this.typeParamSubst.reinstantiate(fi.throwTypes());
		return throwTypes;
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