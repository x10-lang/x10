package polyglot.types;

import polyglot.util.Position;

public class InitializerInstance_c extends Use_c<InitializerDef> implements InitializerInstance {
    public InitializerInstance_c(TypeSystem ts, Position pos, Ref<? extends InitializerDef> def) {
        super(ts, pos, def);
    }
}
