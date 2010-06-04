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

package x10.core;

import x10.core.fun.Fun_0_1;

public interface AnyRail<T> extends Indexable<Integer,T>, Fun_0_1<Integer,T>, Iterable<T> {
    public Iterator<T> iterator();

    // Methods to get the backing array.   May be called by generated code.
    public Object getBackingArray();
    
    public boolean[] getBooleanArray();
    public byte[] getByteArray();
    public short[] getShortArray();
    public char[] getCharArray();
    public int[] getIntArray();
    public long[] getLongArray();
    public float[] getFloatArray();
    public double[] getDoubleArray();
    public Object[] getObjectArray();
    
    public Object[] getBoxedArray();

    public Integer length();
    
    public T get(Integer i);

    public T apply(Integer i);
    
    public boolean isZero();
}
