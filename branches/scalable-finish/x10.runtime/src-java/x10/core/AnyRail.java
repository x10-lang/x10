/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import x10.core.Iterator;
import x10.core.Iterable;
import x10.core.fun.Fun_0_1;
import x10.rtt.Type;

public interface AnyRail<T> extends Indexable<Integer,T>, Fun_0_1<Integer,T>, Iterable<T> {
	public Iterator<T> iterator();

    public Type<?> rtt_x10$lang$Fun_0_1_Z1();
    public Type<?> rtt_x10$lang$Fun_0_1_U();
    
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
