package polyglot.frontend;

import java.io.Serializable;

import polyglot.types.Ref;
import polyglot.types.TypeObject;

public interface TypeObjectGoal<T extends TypeObject> extends Goal, Serializable {
    Ref<T> typeRef();
}
