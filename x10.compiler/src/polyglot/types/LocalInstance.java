package polyglot.types;

import x10.types.constants.ConstantValue;

public interface LocalInstance extends VarInstance<LocalDef> {
    LocalInstance flags(Flags flags);
    LocalInstance name(Name name);
    LocalInstance type(Type type);
    LocalInstance constantValue(ConstantValue o);
    LocalInstance notConstant();
}
