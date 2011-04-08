package polyglot.types;

import java.util.List;


public interface ConstructorInstance extends ProcedureInstance<ConstructorDef>, MemberInstance<ConstructorDef> {
    ConstructorInstance container(ContainerType container);
    ConstructorInstance formalTypes(List<Type> formalTypes);
    ConstructorInstance throwTypes(List<Type> throwTypes);
    void setOrigMI(ConstructorInstance orig);
    ConstructorInstance origMI();
}
