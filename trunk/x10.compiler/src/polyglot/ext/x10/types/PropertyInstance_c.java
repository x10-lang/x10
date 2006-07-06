package polyglot.ext.x10.types;

import polyglot.ext.jl.types.FieldInstance_c;
import polyglot.types.Flags;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * An implementation of PropertyInstance
 * @author vj
 *
 */
public class PropertyInstance_c extends FieldInstance_c implements PropertyInstance {

    public PropertyInstance_c() {
        super();
    }

    public PropertyInstance_c(TypeSystem ts, Position pos,
            ReferenceType container, Flags flags, Type type, String name) {
        super(ts, pos, container, flags, type, name);
        
    }
    /**
     * A PropertyInstance is equal to another TypeObject only if the other TypeObject
     * represents a property, and the two are equal when viewed as fields.
     */
    public boolean equalsImpl(TypeObject o) {
        if (o instanceof PropertyInstance) {
        PropertyInstance i = (PropertyInstance) o;
        return super.equalsImpl(i);
    }

    return false;
    }
}
