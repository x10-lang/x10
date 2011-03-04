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

package x10.rtt;


public class UIntType<T> extends RuntimeType<T> {

    public UIntType(Class<?> c) {
        super(c);
    }

    public UIntType(Class<?>c, Type<?>[] parents) {
        super(c, parents);
    }

    @Override
    public String typeName() {
        return "x10.lang.UInt";
    }
}
