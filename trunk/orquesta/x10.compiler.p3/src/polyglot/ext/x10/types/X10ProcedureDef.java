package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import x10.constraint.XConstraint;

public interface X10ProcedureDef extends X10Def, ProcedureDef {
     Ref<? extends XConstraint> whereClause();
     void setWhereClause(Ref<? extends XConstraint> s);
     
     List<Ref<? extends Type>> typeParameters();
     void setTypeParameters(List<Ref<? extends Type>> typeParameters);
}
