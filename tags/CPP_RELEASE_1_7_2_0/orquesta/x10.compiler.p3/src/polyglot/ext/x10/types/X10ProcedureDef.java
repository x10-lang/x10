package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.LocalDef;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import x10.constraint.XConstraint;

public interface X10ProcedureDef extends X10Def, ProcedureDef {
     Ref<XConstraint> guard();
     void setGuard(Ref<XConstraint> s);
     
     List<Ref<? extends Type>> typeParameters();
     void setTypeParameters(List<Ref<? extends Type>> typeParameters);
     
     List<LocalDef> formalNames();
     void setFormalNames(List<LocalDef> formalNames);
}
