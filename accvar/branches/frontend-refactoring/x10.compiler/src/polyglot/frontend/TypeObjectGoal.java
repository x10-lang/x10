package polyglot.frontend;

import java.io.Serializable;

import x10.types.Ref;
import x10.types.TypeObject;

public interface TypeObjectGoal<T extends TypeObject> extends Goal, Serializable {
    Ref<T> typeRef();
}
