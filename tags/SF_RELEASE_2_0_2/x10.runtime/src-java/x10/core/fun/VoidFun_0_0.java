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

public interface VoidFun_0_0 {
    void apply();

    public static class RTT extends RuntimeType<VoidFun_0_0>{
        public static final RTT it = new RTT();

        public RTT() {
            super(VoidFun_0_0.class);
        }

        @Override
        public boolean instanceof$(Object o) {
            return o instanceof VoidFun_0_0;
        }
    }
}
