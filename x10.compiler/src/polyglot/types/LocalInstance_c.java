package polyglot.types;

import polyglot.util.Position;
import x10.types.constants.ConstantValue;

public class LocalInstance_c extends VarInstance_c<LocalDef> implements LocalInstance {
    private static final long serialVersionUID = -5115232710707624648L;

    public LocalInstance_c(TypeSystem ts, Position pos, Ref<? extends LocalDef> def) {
        super(ts, pos, def);
    }
    
    public LocalInstance flags(Flags flags) {
        return (LocalInstance) super.flags(flags);
    }

    public LocalInstance name(Name name) {
        return (LocalInstance) super.name(name);
    }

    public LocalInstance type(Type type) {
        return (LocalInstance) super.type(type);
    }

    public LocalInstance constantValue(ConstantValue o) {
        return (LocalInstance) super.constantValue(o);
    }

    public LocalInstance notConstant() {
        return (LocalInstance) super.notConstant();
    }
}
