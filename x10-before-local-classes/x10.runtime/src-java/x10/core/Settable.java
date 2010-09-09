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

package x10.core;

import x10.rtt.RuntimeType;
import x10.rtt.RuntimeType.Variance;

public interface Settable<D,R> {
    R set$G(R v, D i);
    
    public static final RuntimeType<Settable<?,?>> _RTT = new RuntimeType<Settable<?,?>>(
        Settable.class, 
        new Variance[] {Variance.CONTRAVARIANT, Variance.INVARIANT}
    ) {
        @Override
        public String typeName() {
            return "x10.lang.Settable";
        }
    };

}
