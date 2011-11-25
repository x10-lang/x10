package polyglot.types;

import polyglot.util.Position;

public class Def_c extends TypeObject_c {
    private static final long serialVersionUID = -3949064860322648267L;

    public Def_c() { }
    
    public Def_c(TypeSystem ts) {
        super(ts);
    }
    
    public Def_c(TypeSystem ts, Position pos, Position errorPosition) {
        super(ts, pos, errorPosition);
    }

    public final boolean equalsImpl(TypeObject o) {
        return this == o;
    }
    
    public final int hashCode() {
        return System.identityHashCode(this);
    }
    

}
