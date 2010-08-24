package polyglot.types;

import polyglot.util.Position;

public class Def_c extends TypeObject_c {

    public Def_c() { }
    
    public Def_c(TypeSystem ts) {
        super(ts);
    }
    
    public Def_c(TypeSystem ts, Position pos) {
        super(ts, pos);
    }

    public final boolean equalsImpl(TypeObject o) {
        return this == o;
    }
    
    public final int hashCode() {
        return System.identityHashCode(this);
    }
    
    public Object copy() {
//        assert false;
        return super.copy();
    }

}
