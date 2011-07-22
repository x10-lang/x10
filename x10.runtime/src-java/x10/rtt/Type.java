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


public interface Type<T> extends java.io.Serializable, x10.x10rt.X10JavaSerializable {
    boolean instanceOf(Object o);
    
    boolean equals(Object o);
    boolean isSubtype(Type<?> o);
    
    Object makeArray(int dim0);
    Object makeArray(int dim0, int dim1);
    Object makeArray(int dim0, int dim1, int dim2);
    Object makeArray(int dim0, int dim1, int dim2, int dim3);
    Object makeArray(Object... elems);
//    T setArray(Object array, int i, T v);
    void setArray(Object array, int i, T v);
    T getArray(Object array, int i);
    int arrayLength(Object array);

    Class<?> getImpl();
    String typeName();
}
