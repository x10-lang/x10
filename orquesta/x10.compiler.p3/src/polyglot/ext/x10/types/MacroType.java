package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.MemberInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import x10.constraint.XConstraint;
import x10.constraint.XVar;

public interface MacroType extends ParametrizedType, MemberInstance<TypeDef>, X10ProcedureInstance<TypeDef> {
	Type definedType();
	MacroType definedType(Type t);
	
	MacroType instantiate(StructType container, List<Type> typeArgs, List<Type> argTypes) throws SemanticException;
	
	MacroType name(String name);
	MacroType typeParameters(List<Type> typeParams);
	MacroType formals(List<XVar> formals);
	MacroType formalTypes(List<Type> formalTypes);
	MacroType whereClause(XConstraint whereClause);
}
