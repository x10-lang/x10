package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.MemberInstance;
import polyglot.types.Type;
import x10.constraint.XConstraint;
import x10.constraint.XVar;

public interface MacroType extends ParametrizedType, MemberInstance<TypeDef> {
	Type definedType();
	MacroType definedType(Type t);
	
	ParametrizedType name(String name);
	ParametrizedType typeParams(List<Type> typeParams);
	ParametrizedType formals(List<XVar> formals);
	ParametrizedType formalTypes(List<Type> formalTypes);
	ParametrizedType whereClause(XConstraint whereClause);
}
