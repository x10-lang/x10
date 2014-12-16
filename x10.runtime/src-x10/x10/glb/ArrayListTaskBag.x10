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

package x10.glb;

import x10.util.ArrayList;
/**
 * <p>A TaskBag[T] implemented with a backing ArrayList.
 * </p>
 */
public class ArrayListTaskBag[T] implements TaskBag {
    
    val bag = new ArrayList[T]();
    
    public def size():Long=bag.size();
    
    /**
     * Merge incoming taskbag with local bag.
     * @param tb0 incoming taskbag
     */
    public def merge(tb0:TaskBag) {
        assert tb0 instanceof ArrayListTaskBag[T];
        val tb = tb0 as ArrayListTaskBag[T];
        bag.addAll(tb.bag);
    }
    
    /**
     * Split local bag into two and return half of it
     * @return null if bag size is less than (or equal to) 1
     *         half of the local bag
     */
    public def split():ArrayListTaskBag[T] {
        if (bag.size() <= 1) return null;
        val size = bag.size()/2;
        val o = new ArrayListTaskBag[T]();
        for (i in 1..size) o.bag.add(bag.removeLast());
        return o;
    }
    
    /**
     * Return local bag.
     * @return local bag.
     */
    public def bag()=bag;
}
