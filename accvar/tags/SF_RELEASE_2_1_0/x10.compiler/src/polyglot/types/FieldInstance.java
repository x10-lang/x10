package polyglot.types;


public interface FieldInstance extends VarInstance<FieldDef>, MemberInstance<FieldDef> {
    FieldInstance container(StructType container);
    FieldInstance flags(Flags flags);
    FieldInstance name(Name name);
    FieldInstance type(Type type);
    FieldInstance constantValue(Object o);
    FieldInstance notConstant();
}
