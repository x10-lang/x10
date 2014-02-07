/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.rtt;


public interface Type<T> extends x10.serialization.X10JavaSerializable {
    boolean isInstance(Object o);
    
    boolean equals(Object o);
    boolean isAssignableTo(Type<?> superType);
    
    Object makeArray(int dim0);
    Object makeArray(int dim0, int dim1);
    Object makeArray(int dim0, int dim1, int dim2);
    Object makeArray(int dim0, int dim1, int dim2, int dim3);
    Object makeArray(int... dims);
    void setArray(Object array, int i, T v);
    T getArray(Object array, int i);
    int arrayLength(Object array);

    Class<?> getJavaClass();
    String typeName();

    boolean hasZero();
    boolean isref();
}
