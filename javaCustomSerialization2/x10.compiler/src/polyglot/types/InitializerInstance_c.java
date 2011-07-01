package polyglot.types;

import polyglot.util.Position;

public class InitializerInstance_c extends Use_c<InitializerDef> implements InitializerInstance {
    private static final long serialVersionUID = -1515752301378468964L;

    public InitializerInstance_c(TypeSystem ts, Position pos, Ref<? extends InitializerDef> def) {
        super(ts, pos, def);
    }
}
