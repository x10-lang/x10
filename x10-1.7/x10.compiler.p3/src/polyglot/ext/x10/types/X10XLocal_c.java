package polyglot.ext.x10.types;

import x10.constraint.XLocal_c;
import x10.constraint.XTerms;

public class X10XLocal_c extends XLocal_c {
    X10LocalDef def;
    
    public X10XLocal_c(X10LocalDef def) {
        super(XTerms.makeName(def));
        this.def = def;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof X10XLocal_c) {
            X10XLocal_c v = (X10XLocal_c) o;
            return def == v.def;
        }
        return false;
    }
    
    public int hashCode() {
        return def.hashCode();
    }
    
    public String toString() {
        return def.name().toString();
    }

}
