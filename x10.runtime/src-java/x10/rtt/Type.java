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


public interface Type<T> extends java.io.Serializable {
    boolean instanceof$(Object o);
    
    boolean equals(Object o);
    boolean isSubtype(Type<?> o);
    
    Object makeArray(int length);
    Object makeArray(Object... elems);
//    T setArray(Object array, int i, T v);
    void setArray(Object array, int i, T v);
    T getArray(Object array, int i);
    int arrayLength(Object array);

    Class<?> getJavaClass();
    String typeName();
}
