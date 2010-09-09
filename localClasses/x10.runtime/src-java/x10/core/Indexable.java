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

import x10.core.fun.Fun_0_1;
import x10.rtt.ParameterizedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.UnresolvedType;
import x10.rtt.RuntimeType.Variance;

public interface Indexable<D,R> extends Fun_0_1<D,R> {
    
    public static final RuntimeType<Indexable<?,?>> _RTT = new RuntimeType<Indexable<?,?>>(
        Indexable.class, 
        new Variance[] {Variance.CONTRAVARIANT, Variance.COVARIANT},
        new Type<?>[] {
            new ParameterizedType(Fun_0_1._RTT, new UnresolvedType(0), new UnresolvedType(1))
        }
    ) {
        @Override
        public String typeName() {
            return "x10.lang.Indexable";
        }
    };

}
