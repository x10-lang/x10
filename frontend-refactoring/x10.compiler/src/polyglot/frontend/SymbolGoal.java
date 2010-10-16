package polyglot.frontend;

import x10.types.Ref;
import x10.types.TypeObject;

public interface SymbolGoal<T extends TypeObject> extends Goal {
    Ref<T> typeRef();
}
