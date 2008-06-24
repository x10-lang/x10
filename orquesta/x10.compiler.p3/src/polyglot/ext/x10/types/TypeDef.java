package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.MemberDef;
import polyglot.types.Package;
import polyglot.types.Ref;
import polyglot.types.Type;
import x10.constraint.XConstraint;

public interface TypeDef extends X10Def, MemberDef {
	public abstract Type asType();

	public Ref<? extends Package> package_();
	public void setPackage(Ref<? extends Package> pkg);
	
	public String name();
	public void setName(String name);

	public abstract List<Ref<? extends Type>> typeParameters();
	public abstract void setTypeParameters(List<Ref<? extends Type>> typeParameters);
	
	public abstract List<String> formalNames();
	public abstract void setFormalNames(List<String> formalNames);
	
	public abstract List<Ref<? extends Type>> formalTypes();
	public abstract void setFormalTypes(List<Ref<? extends Type>> formalTypes);

	public abstract Ref<XConstraint> whereClause();
	public abstract void setWhereClause(Ref<XConstraint> whereClause);

	public abstract Ref<? extends Type> definedType();
	public abstract void setType(Ref<? extends Type> type);
}