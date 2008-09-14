package x10.core;

import x10.types.Type;
import x10.types.Types;

public final class ValRail<T> extends AnyRail<T> {
    public ValRail(Type<T> type, int length) {
        super(type, length);
    }
    
    public ValRail(Type<T> type, int length, Object array) {
        super(type, length, array);
    }
    
    
    
    //
    // Runtime type information
    //
    
    static public class RTT extends x10.types.RuntimeType<Rail<?>> {
        Type<?> type;
        
        public RTT(Type<?> type) {
            super(Rail.class);
            this.type = type;
        }

        public boolean instanceof$(java.lang.Object o) {
            if (!(o instanceof ValRail))
                return false;
            ValRail r = (ValRail) o;
            if (! r.type.isSubtype(type)) // covariant
                return false;
            return true;
        }
        
        
        public boolean isSubtype(Type<?> type) {
            if (type instanceof Rail.RTT) {
                Rail.RTT r = (Rail.RTT) type;
                return r.type.equals(this.type);
            }
//            if (type instanceof Fun_0_1.RTT) {
//                Fun_0_1.RTT r = (Fun_0_1.RTT) type;
//                return r.I.equals(Types.INT) && r.V.equals(this.type);
//            }
            return false;
        }
    }

    public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.INT; }
    public Type<?> rtt_x10$lang$Fun_0_1_U()  { return type; }
}