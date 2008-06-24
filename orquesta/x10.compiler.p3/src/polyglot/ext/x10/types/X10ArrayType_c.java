package polyglot.ext.x10.types;

import polyglot.types.ArrayType_c;
import polyglot.types.Named;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

public class X10ArrayType_c extends ArrayType_c implements X10ArrayType {
    public X10ArrayType_c(TypeSystem ts, Position pos, Ref<? extends Type> base) {
        super(ts, pos, base);
    }
    
    public String name() { return ((Named) base).name();}
    public String fullName() { return ((Named) base).fullName();}

    public boolean safe() {
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        return sb.toString();
    }
}
