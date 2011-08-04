package x10.types;

import polyglot.util.Position;

public class Use_c<T extends Def> extends TypeObject_c implements Use<T> {
    private static final long serialVersionUID = -6571291950402711547L;

    protected Ref<? extends T> def;

    public Use_c(TypeSystem ts, Position pos, Ref<? extends T> def) {
        super(ts, pos);
        this.def = def;
    }

    public T def() {
        return def.get();
    }
    
    public String toString() {
        return def.toString();
    }
}