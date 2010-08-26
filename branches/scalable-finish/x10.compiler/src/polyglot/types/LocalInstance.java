package polyglot.types;

public interface LocalInstance extends VarInstance<LocalDef> {
    LocalInstance flags(Flags flags);
    LocalInstance name(Name name);
    LocalInstance type(Type type);
    LocalInstance constantValue(Object o);
    LocalInstance notConstant();
}
