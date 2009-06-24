package polyglot.ext.x10.types;

import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.FunctionDef;
import polyglot.types.MethodInstance;
import polyglot.types.Ref;

public interface ClosureDef extends FunctionDef, X10Def {
    
    Ref<? extends CodeInstance<?>> methodContainer();
    void setMethodContainer(Ref<? extends CodeInstance<?>> mi);
    
    Ref<? extends ClassType> typeContainer();
    void setTypeContainer(Ref<? extends ClassType> ct);

}
