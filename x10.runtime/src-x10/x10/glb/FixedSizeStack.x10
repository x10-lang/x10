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

package x10.glb;

import x10.compiler.*;

/**
 * <p> Stack implementation via an backing Rail.
 * </p>
 */
public final class FixedSizeStack[T]{T haszero} {
    
    /**
     * Backing Rail.
     */
    private val data:Rail[T];
    /**
     * Number of elements in the stack.
     */
    private var size:Long;

    /**
     * Constructor
     * @param n size of the backing Rail.
     */
    public def this(n:Long) {
        data = new Rail[T](n);
        size = 0;
    }

    /**
     * Pop method, returns the top of the stack
     * @return top of the stack
     */
    @Inline public def pop():T = data(--size);
    
    /**
     * Push method, pushes the element on top of the stack
     */
    @Inline public def push(t:T) { data(size++) = t; }
    
    /**
     * Returns the number of elements in the stack
     * @return number of elements in the stack
     */
    @Inline public def size() = size;
}
