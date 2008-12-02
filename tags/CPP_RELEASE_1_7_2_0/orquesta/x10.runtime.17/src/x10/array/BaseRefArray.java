/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array;

import x10.core.Settable;
import x10.types.Type;

/**
 * The class of all multidimensional, settable distributed int arrays
 * in X10.  Specialized from ReferenceArray by replacing the type parameter
 * with T.
 * 
 * Handtranslated from the X10 code in x10/lang/DoubleReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class BaseRefArray<T> extends BaseAnyArray<T> implements Settable<Point,T> {

    public BaseRefArray(Type<T> T, Dist D) {
        super(T, D, true);
    }

    /**
     * Set the element at position rawIndex in the backing store to v.
     * The canonical index has already be calculated and adjusted.
     * Can be used by any dimensioned array.
     */
    public abstract T setOrdinal(T v, int rawIndex);

    @Override
    public T set(T v, int d0, boolean chkPl, boolean chkAOB) {
        return super.set(v, d0, chkPl, chkAOB);
    }

    @Override
    public T set(T v, int d0, int d1, boolean chkPl, boolean chkAOB) {
        return super.set(v, d0, d1, chkPl, chkAOB);
    }

    @Override
    public T set(T v, int d0, int d1, int d2, boolean chkPl, boolean chkAOB) {
        return super.set(v, d0, d1, d2, chkPl, chkAOB);
    }

    @Override
    public T set(T v, int d0, int d1, int d2, int d3, boolean chkPl, boolean chkAOB) {
        return super.set(v, d0, d1, d2, d3, chkPl, chkAOB);
    }

    @Override
    public T set(T v, int d0, int d1, int d2, int d3) {
        return super.set(v, d0, d1, d2, d3);
    }

    @Override
    public T set(T v, int d0, int d1, int d2) {
        return super.set(v, d0, d1, d2);
    }

    @Override
    public T set(T v, int d0, int d1) {
        return super.set(v, d0, d1);
    }

    @Override
    public T set(T v, int d0) {
        return super.set(v, d0);
    }

    @Override
    public T set(T v, Point pos, boolean chkPl, boolean chkAOB) {
        return super.set(v, pos, chkPl, chkAOB);
    }

    @Override
    public T set(T v, Point pos) {
        return super.set(v, pos);
    }

}
