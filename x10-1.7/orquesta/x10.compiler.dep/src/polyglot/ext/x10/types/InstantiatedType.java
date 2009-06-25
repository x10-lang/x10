package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.Ref;
import polyglot.types.Type;

/** Instantiation of a generic type. */
public interface InstantiatedType {
    
    Ref<? extends Type> base();
    List<Ref<? extends Type>> typeArguments();

}
