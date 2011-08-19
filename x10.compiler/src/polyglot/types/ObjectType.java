package polyglot.types;

import java.util.List;

/**
 * A <code>ReferenceType</code> represents a reference type: a type that is a subtype of Object.
 */
public interface ObjectType extends  ContainerType {
    /**
     * Return the type's super type.
     */
    Type superClass();

    /**
     * Return the type's interfaces.
     * @return A list of <code>Type</code>.
     * @see polyglot.types.Type
     */
    List<Type> interfaces();
}
