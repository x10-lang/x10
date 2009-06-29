/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array;

import java.util.Iterator;

import x10.core.Indexable;
import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;
import x10.core.fun.Fun_0_3;
import x10.runtime.Configuration;
import x10.runtime.Place;
import x10.runtime.Runtime;
import x10.types.Type;

/**
 * The class of all multidimensional, distributed double arrays in X10. Has no
 * mutable data. Specialized from array by replacing the type parameter with
 * double.
 * 
 * Handtranslated from the X10 code in x10/lang/DoubleArray.x10
 * 
 * @author vj 12/24/2004
 */
abstract public class BaseAnyArray<T> extends BaseArray {

    /** Runtime representation of the parameter type--needed for instanceof and to get operators. */
    final Type<T> T;

    protected BaseAnyArray(Type<T> T, Dist D, boolean mutable) {
        super(D, mutable);
        this.T = T;
    }
    
    public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Point.RTT.it; }
    public Type<?> rtt_x10$lang$Fun_0_1_U()  { return T; }

    public abstract Object getBackingArray();

    /**
     * Convenience method for returning the sum of the array.
     * 
     * @return sum of the array.
     */
    public T sum() {
        return reduce(T.addOperator(), T.zeroValue());
    }

    public T sum(Region r) {
        return reduce(T.addOperator(), T.zeroValue(), r);
    }

    /**
     * Convenience method for returning the max of the array.
     * 
     * @return
     */
    public T max() {
        return reduce(T.maxOperator(), T.minValue());
    }

    public T max(Region r) {
        return reduce(T.maxOperator(), T.minValue(), r);
    }

    /**
     * Convenience method for returning the max of the array after applying the
     * given fun.
     * 
     * @param fun
     * @return
     */
    public T max(Fun_0_1<T, T> fun) {
        return lift(fun).reduce(T.maxOperator(), T.minValue());
    }

    /**
     * Convenience method for applying abs to each element in the array.
     * 
     * @return
     */
    public BaseAnyArray<T> abs() {
        return lift(T.absOperator());
    }

    /**
     * Convenience method for subtracting another array pointwise.
     * 
     * @return
     */
    public BaseAnyArray<T> sub(BaseAnyArray<T> s) {
        return lift(T.subOperator(), s);
    }

    /**
     * Convenience method for subtracting another array pointwise.
     * 
     * @return
     */
    public BaseAnyArray<T> add(BaseAnyArray<T> s) {
        return lift(T.addOperator(), s);
    }

    /**
     * Convenience method for subtracting another array pointwise.
     * 
     * @return
     */
    public BaseAnyArray<T> mul(BaseAnyArray<T> s) {
        return lift(T.mulOperator(), s);
    }

    /*
     * Given an array A, return A^k.
     */
    public BaseAnyArray<T> pow(int k) {
        return k <= 0 ? this : mul(pow(k - 1));
    }

    /**
     * Convenience method for subtracting another array pointwise.
     * 
     * @return
     */
    public BaseAnyArray<T> div(BaseAnyArray<T> s) {
        return lift(T.divOperator(), s);
    }

    /*
     * Given an array A, return k*A, where k is a scalar.
     */
    public BaseAnyArray<T> scale(final int k) {
        return lift(T.scaleOperator(k));
    }

    /**
     * Convenience method for applying max after applying abs.
     * 
     * @return
     */
    public T maxAbs() {
        return max(T.absOperator());
    }

    public T reduce(Fun_0_2<T, T, T> fun, T unit) {
        return reduce(fun, unit, dist.region);
    }

    protected abstract BaseAnyArray<T> newInstance(Dist d);
    
    protected BaseAnyArray<T> newInstance(Dist d, T c) {
        BaseAnyArray<T> res = newInstance(d);
        if (c != null) {
            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = dist.region.iterator(); it.hasNext(); ) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            if (res != null)
                                    res.set(c, p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
        }
        return res;
    }
    
    protected final BaseAnyArray<T> newInstance(Dist d, Fun_0_1<T, T> p) {
            BaseAnyArray<T> res = newInstance(d);
            if (p != null)
                    scan(res, p);
            return res;
    }

    /**
     * Return the element at position rawIndex in the backing store.
     * The canonical index has already be calculated and adjusted.
     * Can be used by any dimensioned array.
     */
    public abstract T getOrdinal(int rawIndex);

    /**
     * Set the element at position rawIndex in the backing store to v.
     * The canonical index has already be calculated and adjusted.
     * Can be used by any dimensioned array.
     */
    protected abstract T setOrdinal(T v, int rawIndex);

    public void set(Point i, T v) {
        set(v, i);
    }

    protected T set(T v, Point pos) { return set(v,pos,true,true); }
    protected T set(T v, Point pos,boolean chkPl,boolean chkAOB) {
            if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
                    Runtime.hereCheckPlace(dist.get(pos));
            int theIndex = Helper.ordinal(dist,pos,chkAOB);
            return setOrdinal(v, theIndex);
    }

    protected T set(T v, int d0) { return set(v,d0,true,true); }
    protected T set(T v, int d0,boolean chkPl,boolean chkAOB) {
            if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
                    Runtime.hereCheckPlace(dist.get(d0));
            int theIndex = Helper.ordinal(dist,d0,chkAOB);
            return setOrdinal(v, theIndex);
    }

    protected T set(T v, int d0,int d1) { return set(v,d0,d1,false,false); }
    protected T set(T v, int d0, int d1,boolean chkPl,boolean chkAOB) {
            if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
                    Runtime.hereCheckPlace(dist.get(d0, d1));
            int theIndex = Helper.ordinal(dist,d0,d1,chkAOB);
            return setOrdinal(v, theIndex);
    }

    protected T set(T v, int d0,int d1,int d2) {return set(v,d0,d1,d2,false,false);}
    protected T set(T v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
            if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
                    Runtime.hereCheckPlace(dist.get(d0, d1, d2));
            int theIndex = Helper.ordinal(dist,d0,d1,d2,chkAOB);
            return setOrdinal(v, theIndex);
    }

    protected T set(T v, int d0,int d1,int d2,int d3) {return set(v,d0,d1,d2,d3,true,true);}
    protected T set(T v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
            if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
                    Runtime.hereCheckPlace(dist.get(d0, d1, d2, d3));
            int theIndex = Helper.ordinal(dist,d0,d1,d2,d3,chkAOB);
            return setOrdinal(v, theIndex);
    }

    public T get(Point pos) {return get(pos,true,true);}
    public T get(Point pos,boolean chkPl,boolean chkAOB) {
            if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
                    Runtime.hereCheckPlace(dist.get(pos));
            int theIndex = Helper.ordinal(dist,pos,chkAOB);
            return getOrdinal(theIndex);
    }

    public T get(int d0) {return get(d0,true,true);}
    public T get(int d0,boolean chkPl,boolean chkAOB) {
            if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
                    Runtime.hereCheckPlace(dist.get(d0));
            int theIndex = Helper.ordinal(dist,d0,chkAOB);
            return getOrdinal(theIndex);
    }

    public T get(int d0,int d1) {return get(d0,d1,true,true);}
    public T get(int d0, int d1,boolean chkPl,boolean chkAOB) {
            if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
                    Runtime.hereCheckPlace(dist.get(d0, d1));
            int theIndex = Helper.ordinal(dist,d0,d1,chkAOB);
            return getOrdinal(theIndex);
    }

    public T get(int d0,int d1,int d2) {return get(d0,d1,d2,true,true);}
    public T get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
            if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
                    Runtime.hereCheckPlace(dist.get(d0, d1, d2));
            int theIndex = Helper.ordinal(dist,d0,d1,d2,chkAOB);
            return getOrdinal(theIndex);
    }

    public T get(int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
    public T get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
            if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
                    Runtime.hereCheckPlace(dist.get(d0, d1, d2, d3));
            int theIndex = Helper.ordinal(dist,d0,d1,d2,d3,chkAOB);
            return getOrdinal(theIndex);
    }

//  public T get(int[] pos) {return get(pos,true,true);}
//  public T get(int[] pos,boolean chkPl,boolean chkAOB) {
//          if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
//                  Runtime.hereCheckPlace(dist.get(pos));
//          final Point p = Runtime.factory.getPointFactory().Point(pos);
//          return get(p);
//  }

    public boolean valueEquals(Indexable other) {
            BaseAnyArray<T> o = (BaseAnyArray<T>)other;
            if (!o.dist.equals(dist))
                    return false;
            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = dist.region.iterator(); it.hasNext(); ) {
                            Point pos = (Point) it.next();
                            Place pl = dist.get(pos);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            if (get(pos) != o.get(pos))
                                    return false;
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
            return true;
    }

    protected void assign(BaseAnyArray<T> rhs) {
            assert rhs instanceof BaseAnyArray;
            assert rhs.dist.equals(dist);

            Place here = x10.runtime.Runtime.runtime.currentPlace();
            BaseAnyArray<T> rhs_t = rhs;
            try {
                    for (Iterator it = rhs_t.dist.region.iterator(); it.hasNext(); ) {
                            Point pos = (Point) it.next();
                            Place pl = dist.get(pos);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            set(rhs_t.get(pos), pos);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
    }

    /*
     * Generic implementation - an array with fixed, known number of dimensions
     * can of course do without the Iterator.
     */
    public void pointwise(BaseAnyArray<T> res, Fun_0_3<Point,T,T,T> op, BaseAnyArray<T> arg) {
            assert res == null || res.dist.equals(dist);
            assert arg != null;
            assert arg.dist.equals(dist);

            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = dist.region.iterator(); it.hasNext(); ) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            T arg1 = get(p);
                            T arg2 = arg.get(p);
                            T val = op.apply(p, arg1, arg2);
                            if (res != null)
                                    res.set(val, p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
    }

    public void pointwise(BaseAnyArray<T> res, Fun_0_2<Point,T,T> op) {
            assert res == null || res.dist.equals(dist);

            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = dist.region.iterator(); it.hasNext(); ) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            T arg1 = get(p);
                            T val = op.apply(p, arg1);
                            if (res != null)
                                    res.set(val, p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
    }

    /* operations can be performed in any order */
    public <U> void reduction(Fun_0_1<T,U> op) {
            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = dist.region.iterator(); it.hasNext(); ) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            T arg1 = get(p);
                            op.apply(arg1);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
    }

    /* operations are performed in canonical order */
    public void scan(BaseAnyArray<T> res, Fun_0_1<T,T> op) {
            assert res == null || res instanceof BaseAnyArray;
            assert res.dist.equals(dist);

            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = dist.region.iterator(); it.hasNext(); ) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            T arg1 = get(p);
                            T val = op.apply(arg1);
                            if (res != null)
                                    res.set(val, p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
    }
    
    /**
     * Return a DoubleArray with the same distribution as this, by scanning this
     * with the function fun, and unit unit.
     */
    /* operations are performed in canonical order */
    public void scan(BaseAnyArray<T> res, Fun_0_2<Point, T, T> op) {
            assert res == null || res instanceof BaseAnyArray;
            assert res.dist.equals(dist);

            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = dist.region.iterator(); it.hasNext(); ) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            T val = op.apply(p, T.zeroValue());
                            if (res != null)
                                    res.set(val, p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
    }

    public BaseAnyArray<T> lift(Fun_0_2<T,T,T> op, BaseAnyArray<T> arg) {
            assert arg.dist.equals(dist);
            BaseAnyArray<T> result = newInstance(dist);
            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = dist.region.iterator(); it.hasNext();) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            result.set(op.apply(this.get(p), arg.get(p)),p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
            return result;
    }

    /**
     * Assume given a DoubleArray a over the given distribution. Assume given a
     * function f: double -> double -> double. Return a DoubleArray with
     * distribution dist containing fun(this.atValue(p),a.atValue(p)) for each p
     * in dist.region.
     */
    public BaseAnyArray<T> lift(Fun_0_1<T,T> op) {
            BaseAnyArray<T> result = newInstance(dist);
            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = dist.region.iterator(); it.hasNext();) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            result.set(op.apply(this.get(p)),p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
            return result;
    }


    /**
     * Return the value obtained by reducing the given array (viewed only
     * through the region r) with the function fun, which is assumed to be
     * associative and commutative. unit should satisfy
     * fun(unit,x)=x=fun(x,unit). Assume r is contained in the array's region.
     */

    /**
     * Assume that r is contained in dist.region.
     */
    public T reduce(Fun_0_2<T,T,T> op, T unit, Region r) {
            T result = unit;
            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = r.iterator(); it.hasNext();) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            result = op.apply(result, this.get(p));
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
            return result;
    }

    public BaseAnyArray<T> scan(Fun_0_2<T,T,T> op, T unit) {
            T temp = unit;
            BaseAnyArray<T> result = newInstance(dist);
            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = dist.region.iterator(); it.hasNext();) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            temp = op.apply(this.get(p), temp);
                            result.set(temp, p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
            return result;
    }

    /**
     * Return the array obtained by overlaying this array on top of other. The
     * method takes as parameter a distribution D over the same rank. It returns
     * an array over the distribution dist.asymmetricUnion(D).
     */
    /*
     * FIXME: this could be made much more efficient with knowledge of overlay() semantics.
     */
    public BaseAnyArray<T> overlay(BaseAnyArray<T> d) {
            Dist dist = this.dist.overlay(d.dist);
            BaseAnyArray<T> ret = newInstance(dist, T.zeroValue());
            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator<Point> it = dist.iterator(); it.hasNext(); ) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            T val = (d.dist.region.contains(p)) ? d.get(p) : get(p);
                            ret.set(val, p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
            return ret;
    }

    /**
     * Update this array in place by overlaying the array other on top of this.
     * The distribution of the input array must be a subdistribution of D. TODO:
     * update the description of the parametric type.
     */

    /*
     * FIXME: this could use the fact that d is rectangular
     * FIXME: (in fact, why are we even iterating over the unknown array here?)
     */
    public void update(BaseAnyArray<T> d) {
            assert (region.contains(d.region));
            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = d.iterator(); it.hasNext(); ) {
                            Point p = (Point) it.next();
                            Place pl = dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            set(d.get(p), p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
    }

    /**
     * Take as parameter a distribution D of the same rank as this, and defined
     * over a disjoint region. Take as argument an array other over D. Return an
     * array whose distribution is the union of this and D and which takes on
     * the value this.atValue(p) for p in this.region and other.atValue(p) for p
     * in other.region.
     */
    /*
     * FIXME: this could be made much more efficient with knowledge of union() semantics.
     */
    public BaseAnyArray<T> union(BaseAnyArray<T> d) {
            Dist Dist = dist.union(d.dist);
            BaseAnyArray<T> ret = newInstance(Dist, T.zeroValue());
            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = Dist.iterator(); it.hasNext(); ) {
                            Point p = (Point) it.next();
                            Place pl = Dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            T val = (dist.region.contains(p)) ? get(p) : d.get(p);
                            ret.set(val, p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
            return ret;
    }

    /**
     * Return an array of B@P defined on the intersection of the region
     * underlying this and the parametric distribution.
     */
    public BaseAnyArray<T> restriction(Dist d) {
            return restriction(d.region);
    }


    /**
     * Return an array of B@P defined on the intersection of the region
     * underlying the array and the parameter region R.
     */
    /*
     * FIXME: this could use the fact that the region is rectangular
     * FIXME: (in fact, why are we even iterating over the unknown array here?)
     */
    public BaseAnyArray<T> restriction(Region r) {
            Dist Dist = dist.restriction(r);
            BaseAnyArray<T> ret = newInstance(Dist, T.zeroValue());
            Place here = x10.runtime.Runtime.runtime.currentPlace();
            try {
                    for (Iterator it = Dist.iterator(); it.hasNext(); ) {
                            Point p = (Point) it.next();
                            Place pl = Dist.get(p);
                            x10.runtime.Runtime.runtime.setCurrentPlace(pl);
                            ret.set(get(p), p);
                    }
            } finally {
                    x10.runtime.Runtime.runtime.setCurrentPlace(here);
            }
            return ret;
    }

    public Object toJava() {
            final int[] dims_tmp = new int[dist.rank];
            for (int i = 0; i < dist.rank; ++i)
                    dims_tmp[i] = dist.region.rank(i).high() + 1;

            final Object ret = java.lang.reflect.Array.newInstance(T.getJavaClass(), dims_tmp);
            pointwise(null, new Fun_0_2<Point,T,T>() {
                    public T apply(Point p, T arg) {
                            Object handle = ret;
                            int i = 0;
                            for (; i < dims_tmp.length - 1; ++i) {
                                    handle = java.lang.reflect.Array.get(handle, p.get(i));
                            }
                            java.lang.reflect.Array.set(handle, p.get(i), arg);
                            return arg;
                    }
                    
                    public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Point.RTT.it; }
                    public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return T; }
                    public Type<?> rtt_x10$lang$Fun_0_2_U()  { return T; }
            });
            return ret;
    }

    /**
     * Return an immutable copy of this array. Note: The implementation actually returns a copy
     * at the representation of the X10 type x10.lang.doubleValueArray, which is doubleArray.
     * @return an immutable version of this array.
     */
    public BaseAnyArray<T> toValueArray() {
            if (!mutable_) return this;
            throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");
    }
}
