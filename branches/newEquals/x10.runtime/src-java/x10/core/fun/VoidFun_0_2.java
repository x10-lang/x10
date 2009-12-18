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

public interface VoidFun_0_2<T1,T2> {
    void apply(T1 o1, T2 o2);
    Type<?> rtt_x10$lang$VoidFun_0_2_Z1();
    Type<?> rtt_x10$lang$VoidFun_0_2_Z2();

    public static class RTT extends RuntimeType<VoidFun_0_2<?,?>>{
        Type<?> T1;
        Type<?> T2;

        public RTT(Type<?> T1, Type<?> T2) {
            super(VoidFun_0_2.class);
            this.T1 = T1;
            this.T2 = T2;
        }

        @Override
        public boolean instanceof$(Object o) {
            if (o instanceof VoidFun_0_2) {
                VoidFun_0_2<?,?> v = (VoidFun_0_2<?,?>) o;
                if (! T1.isSubtype(v.rtt_x10$lang$VoidFun_0_2_Z1())) return false; // contravariant
                if (! T2.isSubtype(v.rtt_x10$lang$VoidFun_0_2_Z2())) return false; // contravariant
                return true;
            }
            return false;
        }

        @Override
        public boolean isSubtype(Type<?> o) {
            if (! super.isSubtype(o))
                return false;
            if (o instanceof VoidFun_0_2.RTT) {
                VoidFun_0_2.RTT t = (VoidFun_0_2.RTT) o;
                return t.T1.isSubtype(T1) && t.T2.isSubtype(T2);
            }
            return false;
        }
    }

}
