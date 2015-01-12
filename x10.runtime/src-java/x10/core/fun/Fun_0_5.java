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

import x10.rtt.FunType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public interface Fun_0_5<T1,T2,T3,T4,T5,U> extends Fun {
    U $apply(T1 o1, Type t1, T2 o2, Type t2, T3 o3, Type t3, T4 o4, Type t4, T5 o5, Type t5);
    
    public static final RuntimeType<Fun_0_5<?,?,?,?,?,?>> $RTT = FunType.<Fun_0_5<?,?,?,?,?,?>> make(
        Fun_0_5.class,
        6
    );
}
