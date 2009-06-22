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
 * with double.
 * 
 * Handtranslated from the X10 code in x10/lang/DoubleReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class DoubleReferenceArray extends doubleArray {
	
	public DoubleReferenceArray(dist D, boolean mutable) {
		super(D, mutable);
	}

	abstract public double set(double v, point/*(region)*/ p);
	abstract public double setOrdinal(double v, int p);
	abstract /*value*/ public double set(double v, int p);
	abstract /*value*/ public double set(double v, int p, int q);
	abstract /*value*/ public double set(double v, int p, int q, int r);
	abstract /*value*/ public double set(double v, int p, int q, int r, int s);
	
	public double addSet(double v, point/*(region)*/ p) {
		return set(get(p)+v,p);
	}
	public double addSet(double v, int p) {
		return set(get(p)+v,p);
	}
	public double addSet(double v, int p, int q) {
		return set(get(p,q)+v,p,q);
	}
	public double addSet(double v, int p, int q, int r) {
		return set(get(p,q,r)+v,p,q,r);
	}
	public double addSet(double v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)+v,p,q,r,s);
	}
	
	public double mulSet(double v, point/*(region)*/ p) {
		return set(get(p)*v,p);
	}
	public double mulSet(double v, int p) {
		return set(get(p)*v,p);
	}
	public double mulSet(double v, int p, int q) {
		return set(get(p,q)*v,p,q);
	}
	public double mulSet(double v, int p, int q, int r) {
		return set(get(p,q,r)*v,p,q,r);
	}
	public double mulSet(double v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)*v,p,q,r,s);
	}
	public double subSet(double v, point/*(region)*/ p) {
		return set(get(p)-v,p);
	}
	public double subSet(double v, int p) {
		return set(get(p)-v,p);
	}
	public double subSet(double v, int p, int q) {
		return set(get(p,q)-v,p,q);
	}
	public double subSet(double v, int p, int q, int r) {
		return set(get(p,q,r)-v,p,q,r);
	}
	public double subSet(double v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)-v,p,q,r,s);
	}
	public double divSet(double v, point/*(region)*/ p) {
		return set(get(p)/v,p);
	}
	public double divSet(double v, int p) {
		return set(get(p)/v,p);
	}
	public double divSet(double v, int p, int q) {
		return set(get(p,q)/v,p,q);
	}
	public double divSet(double v, int p, int q, int r) {
		return set(get(p,q,r)/v,p,q,r);
	}
	public double divSet(double v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)/v,p,q,r,s);
	}
	public double preInc(int p) {
		return set(get(p) + 1,p);
	}
	public double preInc(int p, int q) {
		return set(get(p,q) + 1,p,q);
	}
	public double preInc(int p, int q, int r) {
		return set(get(p,q,r) + 1,p,q,r);
	}
	public double preInc(int p, int q, int r, int s) {
		return set(get(p,q,r,s) + 1,p,q,r,s);
	}
	public double preDec(int p) {
		return set(get(p) - 1,p);
	}
	public double preDec(int p, int q) {
		return set(get(p,q) - 1,p,q);
	}
	public double preDec(int p, int q, int r) {
		return set(get(p,q,r) - 1,p,q,r);
	}
	public double preDec(int p, int q, int r, int s) {
		return set(get(p,q,r,s) - 1,p,q,r,s);
	}
	public double postInc(int p) {
		double v = get(p);
		set(v + 1, p);
		return v;
	}
	public double postInc(int p, int q) {
		double v = get(p,q);
		set(v + 1, p,q);
		return v;
	}
	public double postInc(int p, int q, int r) {
		double v = get(p,q,r);
		set(v + 1, p,q,r);
		return v;
	}
	public double postInc(int p, int q, int r, int s) {
		double v = get(p,q,r,s);
		set(v + 1, p,q,r,s);
		return v;
	}
	public double postDec(int p) {
		double v = get(p);
		set(v - 1, p);
		return v;
	}
	public double postDec(int p, int q) {
		double v = get(p,q);
		set(v - 1, p,q);
		return v;
	}
	public double postDec(int p, int q, int r) {
		double v = get(p,q,r);
		set(v - 1, p,q,r);
		return v;
	}
	public double postDec(int p, int q, int r, int s) {
		double v = get(p,q,r,s);
		set(v - 1, p,q,r,s);
		return v;
	}

	/**
	 * Return the local chunk of this distributed array.  The result will have a
	 * 1-dimensional 0-based contiguous region.
	 * FIXME: this is just a stub for now.
	 * @return the local chunk of this array.
	 */
	public DoubleReferenceArray local() {
		return this;
	}
}
