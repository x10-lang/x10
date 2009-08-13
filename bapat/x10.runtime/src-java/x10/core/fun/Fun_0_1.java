/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core.fun;

import x10.types.RuntimeType;
import x10.types.Type;

public interface Fun_0_1<T1,U> {
    U apply(T1 o);
    Type<?> rtt_x10$lang$Fun_0_1_Z1();
    Type<?> rtt_x10$lang$Fun_0_1_U();

    public static class RTT extends RuntimeType<Fun_0_1<?,?>>{
        Type<?> T1;
        Type<?> U;

        public RTT(Type T1, Type U) {
            super(Fun_0_1.class);
            this.T1 = T1;
            this.U = U;
        }

        @Override
        public boolean instanceof$(Object o) {
            if (o instanceof Fun_0_1) {
                Fun_0_1 v = (Fun_0_1) o;
                if (! v.rtt_x10$lang$Fun_0_1_U().isSubtype(U)) return false; // covariant
                if (! T1.isSubtype(v.rtt_x10$lang$Fun_0_1_Z1())) return false; // contravariant
                return true;
            }
            return false;
        }

        @Override
        public boolean isSubtype(Type<?> o) {
            if (! super.isSubtype(o))
                return false;
            if (o instanceof Fun_0_1.RTT) {
                Fun_0_1.RTT t = (RTT) o;
                return U.isSubtype(t.U) && t.T1.isSubtype(T1);
            }
            return false;
        }
    }
}
