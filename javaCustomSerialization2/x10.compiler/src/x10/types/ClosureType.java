package x10.types;

import polyglot.types.CodeInstance;

public interface ClosureType extends FunctionType {
    ClosureInstance closureInstance();
    ClosureType closureInstance(ClosureInstance ci);
    CodeInstance<?> methodContainer();
    ClosureType methodContainer(CodeInstance<?> methodContainer);
    FunctionType functionInterface();
}
