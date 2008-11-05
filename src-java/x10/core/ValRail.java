/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import x10.core.fun.Fun_0_1;
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
    // boxed rail
    //
    
    @Override
    public Ref box$() {
    	return new BoxedValRail(type, this);
    }
    
    public static class BoxedValRail<T> extends Box<ValRail<T>> implements Indexable<Integer,T>, Fun_0_1<Integer,T> {
    	public BoxedValRail(Type<T> T, ValRail<T> v) {
    		super(new ValRail.RTT(T), v);
		}

		public T apply(Integer o) {
			return this.value.apply(o);
		}

		public Type<?> rtt_x10$lang$Fun_0_1_U() {
			throw new RuntimeException();
		}

		public Type<?> rtt_x10$lang$Fun_0_1_Z1() {
			throw new RuntimeException();
		}
    }

    
    //
    // Runtime type information
    //
    
    static public class RTT<T> extends x10.types.RuntimeType<ValRail<T>> {
        Type<T> type;
        
        public RTT(Type<T> type) {
            super(ValRail.class);
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
            if (type instanceof ValRail.RTT) {
                ValRail.RTT r = (ValRail.RTT) type;
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
