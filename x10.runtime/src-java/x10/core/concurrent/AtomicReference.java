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

package x10.core.concurrent;

import x10.core.RefI;
import x10.rtt.RuntimeType;
import x10.rtt.RuntimeType.Variance;
import x10.rtt.Type;

public final class AtomicReference<T> extends java.util.concurrent.atomic.AtomicReference<T> implements RefI {

    public AtomicReference(Type<T> T) {
        super();
        this.T = T;
    }
    
    public AtomicReference(Type<T> T, T initialValue) {
        super(initialValue);
        this.T = T;
    }
    
    //
    // Runtime type information
    //
    public static final RuntimeType<AtomicReference> $RTT = new RuntimeType<AtomicReference>(
        AtomicReference.class,
        new Variance[] { Variance.INVARIANT },
        new Type[] { x10.rtt.Types.OBJECT }
    ) {
        @Override
        public String typeName() {
            return "x10.util.concurrent.AtomicReference";
        }
    };
    public RuntimeType<AtomicReference> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {
        return i == 0 ? T : null;
    }
    private final Type<T> T;
}
