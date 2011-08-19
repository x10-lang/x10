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

public class FunType<T> extends RuntimeType<T> {

	private static final long serialVersionUID = 1L;

    public FunType(Class<?> c) {
        super(c);
    }
    
    public FunType(Class<?> c, Variance[] variances) {
        super(c, variances);
    }

    public FunType(Class<?> c, Type<?>[] parents) {
        super(c, parents);
    }
    
    public FunType(Class<?> c, Variance[] variances, Type<?>[] parents) {
        super(c, variances, parents);
    }

    @Override
    public String typeName(Object o) {
        return typeNameForFun(o);
    }

}
