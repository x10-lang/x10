/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;


/**
 * The class of all multidimensional, settable distributed int arrays
 * in X10.  Specialized from ReferenceArray by replacing the type parameter
 * with float.
 * 
 * Handtranslated from the X10 code in x10/lang/FloatReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class FloatReferenceArray extends floatArray {
	
	public FloatReferenceArray(dist D, boolean mutable) {
		super(D, mutable);
	}

	abstract public float set(float v, point/*(region)*/ p);
	abstract public float setOrdinal(float v, int p);
	abstract /*value*/ public float set(float v, int p);
	abstract /*value*/ public float set(float v, int p, int q);
	abstract /*value*/ public float set(float v, int p, int q, int r);
	abstract /*value*/ public float set(float v, int p, int q, int r, int s);
	
	public float addSet(float v, point/*(region)*/ p) {
		return set(get(p)+v,p);
	}
	public float addSet(float v, int p) {
		return set(get(p)+v,p);
	}
	public float addSet(float v, int p, int q) {
		return set(get(p,q)+v,p,q);
	}
	public float addSet(float v, int p, int q, int r) {
		return set(get(p,q,r)+v,p,q,r);
	}
	public float addSet(float v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)+v,p,q,r,s);
	}	
	public float mulSet(float v, point/*(region)*/ p) {
		return set(get(p)*v,p);
	}
	public float mulSet(float v, int p) {
		return set(get(p)*v,p);
	}
	public float mulSet(float v, int p, int q) {
		return set(get(p,q)*v,p,q);
	}
	public float mulSet(float v, int p, int q, int r) {
		return set(get(p,q,r)*v,p,q,r);
	}
	public float mulSet(float v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)*v,p,q,r,s);
	}
	public float subSet(float v, point/*(region)*/ p) {
		return set(get(p)-v,p);
	}
	public float subSet(float v, int p) {
		return set(get(p)-v,p);
	}
	public float subSet(float v, int p, int q) {
		return set(get(p,q)-v,p,q);
	}
	public float subSet(float v, int p, int q, int r) {
		return set(get(p,q,r)-v,p,q,r);
	}
	public float subSet(float v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)-v,p,q,r,s);
	}
	public float divSet(float v, point/*(region)*/ p) {
		return set(get(p)/v,p);
	}
	public float divSet(float v, int p) {
		return set(get(p)/v,p);
	}
	public float divSet(float v, int p, int q) {
		return set(get(p,q)/v,p,q);
	}
	public float divSet(float v, int p, int q, int r) {
		return set(get(p,q,r)/v,p,q,r);
	}
	public float divSet(float v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)/v,p,q,r,s);
	}
	public float preInc(int p) {
		return set(get(p) + 1,p);
	}
	public float preInc(int p, int q) {
		return set(get(p,q) + 1,p,q);
	}
	public float preInc(int p, int q, int r) {
		return set(get(p,q,r) + 1,p,q,r);
	}
	public float preInc(int p, int q, int r, int s) {
		return set(get(p,q,r,s) + 1,p,q,r,s);
	}
	public float preDec(int p) {
		return set(get(p) - 1,p);
	}
	public float preDec(int p, int q) {
		return set(get(p,q) - 1,p,q);
	}
	public float preDec(int p, int q, int r) {
		return set(get(p,q,r) - 1,p,q,r);
	}
	public float preDec(int p, int q, int r, int s) {
		return set(get(p,q,r,s) - 1,p,q,r,s);
	}
	public float postInc(int p) {
		float v = get(p);
		set(v + 1, p);
		return v;
	}
	public float postInc(int p, int q) {
		float v = get(p,q);
		set(v + 1, p,q);
		return v;
	}
	public float postInc(int p, int q, int r) {
		float v = get(p,q,r);
		set(v + 1, p,q,r);
		return v;
	}
	public float postInc(int p, int q, int r, int s) {
		float v = get(p,q,r,s);
		set(v + 1, p,q,r,s);
		return v;
	}
	public float postDec(int p) {
		float v = get(p);
		set(v - 1, p);
		return v;
	}
	public float postDec(int p, int q) {
		float v = get(p,q);
		set(v - 1, p,q);
		return v;
	}
	public float postDec(int p, int q, int r) {
		float v = get(p,q,r);
		set(v - 1, p,q,r);
		return v;
	}
	public float postDec(int p, int q, int r, int s) {
		float v = get(p,q,r,s);
		set(v - 1, p,q,r,s);
		return v;
	}

	/**
	 * Return the local chunk of this distributed array.  The result will have a
	 * 1-dimensional 0-based contiguous region.
	 * FIXME: this is just a stub for now.
	 * @return the local chunk of this array.
	 */
	public FloatReferenceArray local() {
		return this;
	}
}
