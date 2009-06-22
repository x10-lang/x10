package polyglot.ext.x10.types;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;

public interface X10ProcedureDef extends X10Def, ProcedureDef {
     Ref<? extends Constraint> whereClause();
     void setWhereClause(Ref<? extends Constraint> s);
}
