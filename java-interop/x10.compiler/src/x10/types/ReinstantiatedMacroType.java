/**
 * 
 */
package x10.types;

import java.util.List;

import polyglot.types.ContainerType;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.types.constraints.CConstraint;

public final class ReinstantiatedMacroType extends MacroType_c {
	private static final long serialVersionUID = 198078123816586742L;

	private final TypeParamSubst typeParamSubst;
	private final MacroType fi;

	public ReinstantiatedMacroType(TypeParamSubst typeParamSubst, TypeSystem ts, Position pos,
			Ref<TypeDef> def, MacroType fi) {
		super(ts, pos, def);
		this.typeParamSubst = typeParamSubst;
		this.fi = fi;
	}

    public TypeParamSubst typeParamSubst() {
        ContainerType ct = fi.container();
        if (ct != null && ct.isClass()) {
            TypeParamSubst dsubst = ct.toClass().def().subst();
            if (dsubst != null) {
                return dsubst.reinstantiate(typeParamSubst);
            }
        }
        return typeParamSubst;
    }
	@Override
	public Ref<? extends Type> returnTypeRef() {
		if (definedType == null)
			return this.typeParamSubst().reinstantiate(fi.returnTypeRef());
		return definedType;
	}

	@Override
	public Type returnType() {
		if (definedType == null)
			return this.typeParamSubst().reinstantiate(fi.returnType());
		return definedType.get();
	}

	@Override
	public Ref<? extends Type> definedTypeRef() {
		if (definedType == null)
			return this.typeParamSubst().reinstantiate(fi.definedTypeRef());
		return definedType;
	}

	@Override
	public Type definedType() {
		if (definedType == null)
			return this.typeParamSubst().reinstantiate(fi.definedType());
		return definedType.get();
	}

	@Override
	public List<Type> formalTypes() {
		if (formalTypes == null)
			return this.typeParamSubst().reinstantiate(fi.formalTypes());
		return formalTypes;
	}

	@Override
	public CConstraint guard() {
		if (guard == null)
			return this.typeParamSubst().reinstantiate(fi.guard());
		return guard;
	}
}
