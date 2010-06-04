/**
 * 
 */
package x10.types;

import java.util.List;

import polyglot.types.LocalInstance;
import polyglot.types.Ref;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.types.constraints.CConstraint;

final class ReinstantiatedMethodInstance extends
		X10MethodInstance_c {
	/**
	 * 
	 */
	private final TypeParamSubst typeParamSubst;
	private final X10MethodInstance fi;

	ReinstantiatedMethodInstance(TypeParamSubst typeParamSubst, TypeSystem ts, Position pos,
			Ref<? extends X10MethodDef> def, X10MethodInstance fi) {
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
	public List<LocalInstance> formalNames() {
		if (formalNames == null) 
			return this.typeParamSubst.reinstantiate(fi.formalNames());
		return formalNames;
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