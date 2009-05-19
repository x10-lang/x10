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

public interface VoidFun_0_3<T1,T2,T3> {
    void apply(T1 o1, T2 o2, T3 o3);
    Type<?> rtt_x10$lang$VoidFun_0_3_Z1();
    Type<?> rtt_x10$lang$VoidFun_0_3_Z2();
    Type<?> rtt_x10$lang$VoidFun_0_3_Z3();

    public static class RTT extends RuntimeType<VoidFun_0_3<?,?,?>>{
        Type<?> T1;
        Type<?> T2;
        Type<?> T3;

        public RTT(Type T1, Type T2, Type T3) {
            super(VoidFun_0_3.class);
            this.T1 = T1;
            this.T2 = T2;
            this.T3 = T3;
        }

        @Override
        public boolean instanceof$(Object o) {
            if (o instanceof VoidFun_0_3) {
                VoidFun_0_3 v = (VoidFun_0_3) o;
                if (! T1.isSubtype(v.rtt_x10$lang$VoidFun_0_3_Z1())) return false; // contravariant
                if (! T2.isSubtype(v.rtt_x10$lang$VoidFun_0_3_Z2())) return false; // contravariant
                if (! T3.isSubtype(v.rtt_x10$lang$VoidFun_0_3_Z3())) return false; // contravariant
                return true;
            }
            return false;
        }

        @Override
        public boolean isSubtype(Type<?> o) {
            if (! super.isSubtype(o))
                return false;
            if (o instanceof VoidFun_0_3.RTT) {
                VoidFun_0_3.RTT t = (RTT) o;
                return t.T1.isSubtype(T1) && t.T2.isSubtype(T2) && t.T3.isSubtype(T3);
            }
            return false;
        }
    }

}
