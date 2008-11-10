/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import java.util.Iterator;

import x10.core.fun.Fun_0_1;
import x10.types.Type;
import x10.types.Types;

public final class Rail<T> extends AnyRail<T> implements Settable<Integer,T> {
    public Rail(Type<T> type, int length) {
        super(type, length);
    }
    
    public Rail(Type<T> type, int length, Object array) {
        super(type, length, array);
    }
    
    public T set(T v, Integer i) {
        return type.setArray(value, i, v);
    }

    //
    // boxed rail
    //
    
    @Override
    public Ref box$() {
    	return new BoxedRail(type, this);
    }
    
    public static class BoxedRail<T> extends Box<Rail<T>> implements Indexable<Integer,T>, Fun_0_1<Integer,T>, Settable<Integer,T> {
    	public BoxedRail(Type<T> T, Rail<T> v) {
    		super(new Rail.RTT(T), v);
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

		public T set(T v, Integer i) {
			return this.value.set(v, i);
		}
    }

    //
    // Runtime type information
    //
    
    static public class RTT<T> extends x10.types.RuntimeType<Rail<T>> {
        Type<T> type;
        
        public RTT(Type<T> type) {
            super(Rail.class);
            this.type = type;
        }

        public boolean instanceof$(java.lang.Object o) {
            if (!(o instanceof Rail))
                return false;
            Rail r = (Rail) o;
            if (! r.type.equals(type)) // invariant
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
//            if (type instanceof Settable.RTT) {
//                Settable.RTT r = (Settable.RTT) type;
//                return r.I.equals(Types.INT) && r.V.equals(this.type);
//            }
            return false;
        }
    }

    public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.INT; }
    public Type<?> rtt_x10$lang$Fun_0_1_U()  { return type; }
    public Type<?> rtt_x10$lang$Settable_I() { return Types.INT; }
    public Type<?> rtt_x10$lang$Settable_V() { return type; }
}
