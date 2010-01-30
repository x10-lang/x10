/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.MemberDef;
import polyglot.types.Package;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.Name;
import polyglot.types.Type;
import x10.types.constraints.CConstraint;


public interface TypeDef extends X10Def, MemberDef, X10ProcedureDef {
	public MacroType asType();

	public Ref<? extends Package> package_();
	public void setPackage(Ref<? extends Package> pkg);
	
	public Name name();
	public void setName(Name name);

	public List<Ref<? extends Type>> typeParameters();
	public void setTypeParameters(List<Ref<? extends Type>> typeParameters);
	
	public List<Ref<? extends Type>> formalTypes();
	public void setFormalTypes(List<Ref<? extends Type>> formalTypes);

	public Ref<CConstraint> guard();
	public void setGuard(Ref<CConstraint> guard);

	public Ref<? extends Type> definedType();
	public void setType(Ref<? extends Type> type);
}
