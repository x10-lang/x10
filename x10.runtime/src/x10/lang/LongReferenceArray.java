/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;


/**
 * The class of all multidimensional, settable distributed long arrays
 * in X10.  Declares a non-"value" method, set (hence is not subclass-only 
 * immutable, hence name is capitalized).
 * Specialized from ReferenceArray by replacing the type parameter
 * with long.
 * 
 * Handtranslated from the X10 code in x10/lang/LongReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class LongReferenceArray extends longArray {
	
	public LongReferenceArray(dist D, boolean mutable) {
		super(D, mutable);
	}
	abstract public long set(long v, point/*(region)*/ p);
	abstract public long setOrdinal(long v, int p);
	abstract public long set(long v, int p);
	abstract public long set(long v, int p, int q);
	abstract public long set(long v, int p, int q, int r);
	abstract public long set(long v, int p, int q, int r, int s);
	
	public long addSet(long v, point/*(region)*/ p) {
		return set(get(p)+v,p);
	}
	public long addSet(long v, int p) {
		return set(get(p)+v,p);
	}
	public long addSet(long v, int p, int q) {
		return set(get(p,q)+v,p,q);
	}
	public long addSet(long v, int p, int q, int r) {
		return set(get(p,q,r)+v,p,q,r);
	}
	public long addSet(long v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)+v,p,q,r,s);
	}
	
	public long mulSet(long v, point/*(region)*/ p) {
		return set(get(p)*v,p);
	}
	public long mulSet(long v, int p) {
		return set(get(p)*v,p);
	}
	public long mulSet(long v, int p, int q) {
		return set(get(p,q)*v,p,q);
	}
	public long mulSet(long v, int p, int q, int r) {
		return set(get(p,q,r)*v,p,q,r);
	}
	public long mulSet(long v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)*v,p,q,r,s);
	}
	public long subSet(long v, point/*(region)*/ p) {
		return set(get(p)-v,p);
	}
	public long subSet(long v, int p) {
		return set(get(p)-v,p);
	}
	public long subSet(long v, int p, int q) {
		return set(get(p,q)-v,p,q);
	}
	public long subSet(long v, int p, int q, int r) {
		return set(get(p,q,r)-v,p,q,r);
	}
	public long subSet(long v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)-v,p,q,r,s);
	}
	public long divSet(long v, point/*(region)*/ p) {
		return set(get(p)/v,p);
	}
	public long divSet(long v, int p) {
		return set(get(p)/v,p);
	}
	public long divSet(long v, int p, int q) {
		return set(get(p,q)/v,p,q);
	}
	public long divSet(long v, int p, int q, int r) {
		return set(get(p,q,r)/v,p,q,r);
	}
	public long divSet(long v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)/v,p,q,r,s);
	}
	public long modSet(long v, point/*(region)*/ p) {
		return set(get(p)%v,p);
	}
	public long modSet(long v, int p) {
		return set(get(p)%v,p);
	}
	public long modSet(long v, int p, int q) {
		return set(get(p,q)%v,p,q);
	}
	public long modSet(long v, int p, int q, int r) {
		return set(get(p,q,r)%v,p,q,r);
	}
	public long modSet(long v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)%v,p,q,r,s);
	}
	public long bitAndSet(long v, point/*(region)*/ p) {
		return set(get(p)&v,p);
	}
	public long bitAndSet(long v, int p) {
		return set(get(p)&v,p);
	}
	public long bitAndSet(long v, int p, int q) {
		return set(get(p,q)&v,p,q);
	}
	public long bitAndSet(long v, int p, int q, int r) {
		return set(get(p,q,r)&v,p,q,r);
	}
	public long bitAndSet(long v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)&v,p,q,r,s);
	}
	public long bitOrSet(long v, point/*(region)*/ p) {
		return set(get(p)|v,p);
	}
	public long bitOrSet(long v, int p) {
		return set(get(p)|v,p);
	}
	public long bitOrSet(long v, int p, int q) {
		return set(get(p,q)|v,p,q);
	}
	public long bitOrSet(long v, int p, int q, int r) {
		return set(get(p,q,r)|v,p,q,r);
	}
	public long bitOrSet(long v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)|v,p,q,r,s);
	}
	public long bitXorSet(long v, point/*(region)*/ p) {
		return set(get(p)^v,p);
	}
	public long bitXorSet(long v, int p) {
		return set(get(p)^v,p);
	}
	public long bitXorSet(long v, int p, int q) {
		return set(get(p,q)^v,p,q);
	}
	public long bitXorSet(long v, int p, int q, int r) {
		return set(get(p,q,r)^v,p,q,r);
	}
	public long bitXorSet(long v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)^v,p,q,r,s);
	}
	public long shlSet(long v, point/*(region)*/ p) {
		return set(get(p) << v,p);
	}
	public long shlSet(long v, int p) {
		return set(get(p) << v,p);
	}
	public long shlSet(long v, int p, int q) {
		return set(get(p,q) << v,p,q);
	}
	public long shlSet(long v, int p, int q, int r) {
		return set(get(p,q,r) << v,p,q,r);
	}
	public long shlSet(long v, int p, int q, int r, int s) {
		return set(get(p,q,r,s) << v,p,q,r,s);
	}
	
	public long shrSet(long v, point/*(region)*/ p) {
		return set(get(p) >> v,p);
	}
	public long shrSet(long v, int p) {
		return set(get(p) >> v,p);
	}
	public long shrSet(long v, int p, int q) {
		return set(get(p,q) >> v,p,q);
	}
	public long shrSet(long v, int p, int q, int r) {
		return set(get(p,q,r) >> v,p,q,r);
	}
	public long shrSet(long v, int p, int q, int r, int s) {
		return set(get(p,q,r,s) >> v,p,q,r,s);
	}
	public long ushrSet(long v, point/*(region)*/ p) {
		return set(get(p) >>> v,p);
	}
	public long ushrSet(long v, int p) {
		return set(get(p) >>> v,p);
	}
	public long ushrSet(long v, int p, int q) {
		return set(get(p,q) >>> v,p,q);
	}
	public long ushrSet(long v, int p, int q, int r) {
		return set(get(p,q,r) >>> v,p,q,r);
	}
	public long ushrSet(long v, int p, int q, int r, int s) {
		return set(get(p,q,r,s) >>> v,p,q,r,s);
	}
	public long preInc(int p) {
		return set(get(p) + 1,p);
	}
	public long preInc(int p, int q) {
		return set(get(p,q) + 1,p,q);
	}
	public long preInc(int p, int q, int r) {
		return set(get(p,q,r) + 1,p,q,r);
	}
	public long preInc(int p, int q, int r, int s) {
		return set(get(p,q,r,s) + 1,p,q,r,s);
	}
	public long preDec(int p) {
		return set(get(p) - 1,p);
	}
	public long preDec(int p, int q) {
		return set(get(p,q) - 1,p,q);
	}
	public long preDec(int p, int q, int r) {
		return set(get(p,q,r) - 1,p,q,r);
	}
	public long preDec(int p, int q, int r, int s) {
		return set(get(p,q,r,s) - 1,p,q,r,s);
	}
	public long postInc(int p) {
		long v = get(p);
		set(v + 1, p);
		return v;
	}
	public long postInc(int p, int q) {
		long v = get(p,q);
		set(v + 1, p,q);
		return v;
	}
	public long postInc(int p, int q, int r) {
		long v = get(p,q,r);
		set(v + 1, p,q,r);
		return v;
	}
	public long postInc(int p, int q, int r, int s) {
		long v = get(p,q,r,s);
		set(v + 1, p,q,r,s);
		return v;
	}
	public long postDec(int p) {
		long v = get(p);
		set(v - 1, p);
		return v;
	}
	public long postDec(int p, int q) {
		long v = get(p,q);
		set(v - 1, p,q);
		return v;
	}
	public long postDec(int p, int q, int r) {
		long v = get(p,q,r);
		set(v - 1, p,q,r);
		return v;
	}
	public long postDec(int p, int q, int r, int s) {
		long v = get(p,q,r,s);
		set(v - 1, p,q,r,s);
		return v;
	}

	/**
	 * Return the local chunk of this distributed array.  The result will have a
	 * 1-dimensional 0-based contiguous region.
	 * FIXME: this is just a stub for now.
	 * @return the local chunk of this array.
	 */
	public LongReferenceArray local() {
		return this;
	}
}
