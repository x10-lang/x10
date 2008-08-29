package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.MemberInstance;
import polyglot.types.Named;
import polyglot.types.ProcedureInstance;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.XConstraint;
import x10.constraint.XVar;

public interface MacroType extends ParametrizedType, MemberInstance<TypeDef>, X10ProcedureInstance<TypeDef>, Named {
	Type definedType();
	MacroType definedType(Type t);
	
	TypeDef def();
	
	MacroType name(Name name);
	MacroType typeParameters(List<Type> typeParams);
	MacroType formals(List<XVar> formals);
	MacroType formalTypes(List<Type> formalTypes);
	MacroType guard(XConstraint guard);
}
