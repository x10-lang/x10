package polyglot.frontend;

import polyglot.types.Ref;
import polyglot.types.TypeObject;

public interface SymbolGoal<T extends TypeObject> extends Goal {
    Ref<T> typeRef();
}
