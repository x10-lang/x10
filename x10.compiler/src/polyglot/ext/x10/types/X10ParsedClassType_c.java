package polyglot.ext.x10.types;

import polyglot.ext.jl.types.ParsedClassType_c;
import polyglot.frontend.Source;
import polyglot.types.LazyClassInitializer;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

/**
 * X10ReferenceTypes have two additional dimensions in that they can be
 * futures or nullables.  This class extends the Java ClassType with
 * these two dimensions.
 * 
 * Note that an equivalent type for X10 Arrays will have to be added
 * once we start with support for arrays.  
 * 
 * @author Christian Grothoff
 */
public class X10ParsedClassType_c 
    extends ParsedClassType_c 
    implements X10ReferenceType {
    
    private final int flags_;
    
    protected X10ParsedClassType_c(int flags) {
        super();
        this.flags_ = flags;
    }

    public X10ParsedClassType_c(TypeSystem ts, 
                                LazyClassInitializer init, 
                                Source fromSource,
                                int flags) {
        super(ts, init, fromSource);
        this.flags_ = flags;
    }

    public boolean isNullable() {
        return (flags_ & NULLABLE) > 0;
    }
    
    public boolean isFuture() {
        return (flags_ & IS_FUTURE) > 0;
    }
    
    /** Returns true iff a cast from this to <code>toType</code> is valid. */
    public boolean isCastValidImpl(Type toType) {
        return toType.isPrimitive() && ts.equals(this, ts.Object()) ||
               super.isCastValidImpl(toType);
    }
    
} // end of ParsedX10ClassType_c
