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


public class UByteType<T> extends RuntimeType<T> {

    public UByteType(Class<?> c) {
        super(c);
    }

    public UByteType(Class<?> c, Variance... variances) {
        super(c, variances);
    }

    public UByteType(Class<?>c, Type<?>[] parents) {
        super(c, parents);
    }

    public UByteType(Class<?> c, Variance[] variances, Type<?>[] parents) {
        super(c, variances, parents);
    }

    @Override
    public String typeName() {
        return "x10.lang.UByte";
    }
}
