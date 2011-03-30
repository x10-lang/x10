package x10.types;

public interface ClosureType extends FunctionType {
    ClosureInstance closureInstance();
    ClosureType closureInstance(ClosureInstance ci);
    FunctionType functionInterface();
}
