/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.core.fun;

import x10.rtt.RuntimeType;
import x10.rtt.Type;

public interface VoidFun_0_1<T1> {
    void apply(T1 o);
    Type<?> rtt_x10$lang$VoidFun_0_1_Z1();


    public static class RTT extends RuntimeType<VoidFun_0_1<?>>{
        Type<?> T1;

        public RTT(Type<?> T1) {
            super(VoidFun_0_1.class);
            this.T1 = T1;
        }

        @Override
        public boolean instanceof$(Object o) {
            if (o instanceof VoidFun_0_1) {
                VoidFun_0_1<?> v = (VoidFun_0_1<?>) o;
                if (! T1.isSubtype(v.rtt_x10$lang$VoidFun_0_1_Z1())) return false; // contravariant
                return true;
            }
            return false;
        }

        @Override
        public boolean isSubtype(Type<?> o) {
            if (! super.isSubtype(o))
                return false;
            if (o instanceof VoidFun_0_1.RTT) {
                VoidFun_0_1.RTT t = (VoidFun_0_1.RTT) o;
                return t.T1.isSubtype(T1);
            }
            return false;
        }
    }

}
