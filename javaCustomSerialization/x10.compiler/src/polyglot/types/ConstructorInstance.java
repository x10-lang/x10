package polyglot.types;

import x10.types.X10ProcedureInstance;

import java.util.List;


public interface ConstructorInstance extends X10ProcedureInstance<ConstructorDef>, MemberInstance<ConstructorDef> {
    ConstructorInstance container(ContainerType container);
    ConstructorInstance formalTypes(List<Type> formalTypes);
    ConstructorInstance throwTypes(List<Type> throwTypes);
    void setOrigMI(ConstructorInstance orig);
    ConstructorInstance origMI();
}
