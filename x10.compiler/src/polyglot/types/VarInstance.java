package polyglot.types;

import x10.types.constants.ConstantValue;

public interface VarInstance<T extends VarDef> extends Use<T> {
    /**
     * The flags of the variable.
     */
    Flags flags();
//    VarInstance<T> flags(Flags flags);   // FIXME causes problems with javac; eclipse doesn't complain

    /**
     * The name of the variable.
     */
    Name name();
    VarInstance<T> name(Name name);

    /**
     * The type of the variable.
     */
    Type type();
    VarInstance<T> type(Type type);

    /**
     * The variable's constant value, or null.
     */
    ConstantValue constantValue();
    VarInstance<T> constantValue(ConstantValue o);
    VarInstance<T> notConstant();

    /**
     * Whether the variable has a constant value.
     */
    boolean isConstant();

    boolean lval();
    VarInstance<T> lval(boolean lval);
}
