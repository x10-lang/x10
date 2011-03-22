/**
 * 
 */
package x10.types;

import java.util.List;

import polyglot.types.Ref;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.LocalInstance;
import polyglot.util.Position;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;

final class ReinstantiatedConstructorInstance extends X10ConstructorInstance_c {
	private static final long serialVersionUID = -8401371385252808432L;

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

	/** Use the default formal names only if new names have not been explicitly
	 * provided.
	 * 
	 */
	@Override
	public List<LocalInstance> formalNames() {
		if (formalNames == null)
		return this.typeParamSubst.reinstantiate(fi.formalNames());
		return formalNames;
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

	@Override
	public ContainerType container() {
		if (container == null)
			return this.typeParamSubst.reinstantiate(fi.container());
		return container;
	}
}
