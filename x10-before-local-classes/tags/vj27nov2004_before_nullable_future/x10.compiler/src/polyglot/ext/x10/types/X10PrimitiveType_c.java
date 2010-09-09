/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import polyglot.ext.jl.types.PrimitiveType_c;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

/**
 * @author praun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class X10PrimitiveType_c extends PrimitiveType_c {

	/** Used for deserializing types. */
    protected X10PrimitiveType_c() { }

    public X10PrimitiveType_c(TypeSystem ts, Kind kind) {
        super(ts, kind);
    }

    /**
     * Return true if this type strictly descends from <code>ancestor</code>.
     */
    public boolean descendsFromImpl(Type ancestor) {
        return ts.equals(ancestor, ts.Object());
    }

    /** Return true if this type can be assigned to <code>toType</code>. */
    public boolean isImplicitCastValidImpl(Type toType) {
        return ts.equals(toType, ts.Object()) ||
               super.isImplicitCastValidImpl(toType);
    }

    /** Returns true iff a cast from this to <code>toType</code> is valid. */
    public boolean isCastValidImpl(Type toType) {
        return ts.equals(toType, ts.Object()) || super.isCastValidImpl(toType);
    }
}
