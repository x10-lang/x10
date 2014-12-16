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

package x10.util;

/**
 * Build a result of type U from a set of elements of type T
 */
public interface Builder[T,U] {

    /**
     * add an element to the builder
     * @param e an element
     * @return the builder 
     */
    public def add(e:T):Builder[T,U];
    
    /**
     * return a result
     * @return a result
     */
    public def result():U;
}
