package polyglot.ext.x10.types;

import polyglot.frontend.Source;
import polyglot.types.*;

public interface X10TypeSystem extends TypeSystem {

    /**
     * Create a class type in the X10 Typesystem
     * @param fromSource
     * @param flags see X10ReferenceType flags (bitmask over NULLABLE, IS_FUTURE)
     */
    public ParsedClassType createClassType(int flags);

    /**
     * Create a class type in the X10 Typesystem
     * @param fromSource
     * @param flags see X10ReferenceType flags (bitmask over NULLABLE, IS_FUTURE)
     */
    public ParsedClassType createClassType(Source fromSource,
                                           int flags);

    /**
     * Create a class type in the X10 Typesystem
     * @param flags see X10ReferenceType flags (bitmask over NULLABLE, IS_FUTURE)
     */
    public ParsedClassType createClassType(LazyClassInitializer init, 
                                           Source fromSource,
                                           int flags);

    public ParsedClassType getRuntimeType();
    
    public ParsedClassType getActivityType();

    public ParsedClassType getFutureActivityType();
    
    public ParsedClassType getFutureType();
    
    public ParsedClassType getX10ObjectType();
    
    public ParsedClassType getPlaceType();
    
} // end of X10TypeSystem
