/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.core.fun;

import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.VoidFunType;

public interface VoidFun_0_2<T1,T2> extends VoidFun {
    Object $apply(T1 o1, Type t1, T2 o2, Type t2);

    public static final RuntimeType<VoidFun_0_2<?,?>> $RTT = VoidFunType.<VoidFun_0_2<?,?>> make(
        VoidFun_0_2.class,
        2
    );
}
