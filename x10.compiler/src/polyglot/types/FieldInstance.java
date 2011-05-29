package polyglot.types;

import x10.types.constants.ConstantValue;


public interface FieldInstance extends VarInstance<FieldDef>, MemberInstance<FieldDef> {
    FieldInstance container(ContainerType container);
    FieldInstance flags(Flags flags);
    FieldInstance name(Name name);
    FieldInstance type(Type type);
    FieldInstance constantValue(ConstantValue o);
    FieldInstance notConstant();
}
