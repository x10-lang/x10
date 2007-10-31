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
 * with short.
 * 
 * Handtranslated from the X10 code in x10/lang/ShortReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class ShortReferenceArray extends shortArray {
	
	public ShortReferenceArray(dist D, boolean mutable) {
		super(D, mutable);
	}

	abstract public short set(short v, point/*(region)*/ p);
	abstract public short setOrdinal(short v, int p);
	abstract /*value*/ public short set(short v, int p);
	abstract /*value*/ public short set(short v, int p, int q);
	abstract /*value*/ public short set(short v, int p, int q, int r);
	abstract /*value*/ public short set(short v, int p, int q, int r, int s);
	
	public short addSet(short v, point/*(region)*/ p) {
		return set((short) (get(p)+v),p);
	}
	public short addSet(short v, int p) {
		return set((short) (get(p)+v),p);
	}
	public short addSet(short v, int p, int q) {
		return set((short) (get(p,q)+v),p,q);
	}
	public short addSet(short v, int p, int q, int r) {
		return set((short) (get(p,q,r)+v),p,q,r);
	}
	public short addSet(short v, int p, int q, int r, int s) {
		return set((short) (get(p,q,r,s)+v),p,q,r,s);
	}
	public short mulSet(short v, point/*(region)*/ p) {
		return set((short) (get(p)*v),p);
	}
	public short mulSet(short v, int p) {
		return set((short) (get(p)*v),p);
	}
	public short mulSet(short v, int p, int q) {
		return set((short) (get(p,q)*v),p,q);
	}
	public short mulSet(short v, int p, int q, int r) {
		return set((short) (get(p,q,r)*v),p,q,r);
	}
	public short mulSet(short v, int p, int q, int r, int s) {
		return set((short) (get(p,q,r,s)*v),p,q,r,s);
	}
	public short subSet(short v, point/*(region)*/ p) {
		return set((short) (get(p)-v),p);
	}
	public short subSet(short v, int p) {
		return set((short) (get(p)-v),p);
	}
	public short subSet(short v, int p, int q) {
		return set((short) (get(p,q)-v),p,q);
	}
	public short subSet(short v, int p, int q, int r) {
		return set((short) (get(p,q,r)-v),p,q,r);
	}
	public short subSet(short v, int p, int q, int r, int s) {
		return set((short) (get(p,q,r,s)-v),p,q,r,s);
	}
	public short divSet(short v, point/*(region)*/ p) {
		return set((short) (get(p)/v),p);
	}
	public short divSet(short v, int p) {
		return set((short) (get(p)/v),p);
	}
	public short divSet(short v, int p, int q) {
		return set((short) (get(p,q)/v),p,q);
	}
	public short divSet(short v, int p, int q, int r) {
		return set((short) (get(p,q,r)/v),p,q,r);
	}
	public short divSet(short v, int p, int q, int r, int s) {
		return set((short) (get(p,q,r,s)/v),p,q,r,s);
	}
	public short preInc(int p) {
		return set((short)(get(p) + 1),p);
	}
	public short preInc(int p, int q) {
		return set((short)(get(p,q) + 1),p,q);
	}
	public short preInc(int p, int q, int r) {
		return set((short)(get(p,q,r) + 1),p,q,r);
	}
	public short preInc(int p, int q, int r, int s) {
		return set((short)(get(p,q,r,s) + 1),p,q,r,s);
	}
	public short preDec(int p) {
		return set((short)(get(p) - 1),p);
	}
	public short preDec(int p, int q) {
		return set((short)(get(p,q) - 1),p,q);
	}
	public short preDec(int p, int q, int r) {
		return set((short)(get(p,q,r) - 1),p,q,r);
	}
	public short preDec(int p, int q, int r, int s) {
		return set((short)(get(p,q,r,s) - 1),p,q,r,s);
	}
	public short postInc(int p) {
		short v = get(p);
		set((short)(v + 1), p);
		return v;
	}
	public short postInc(int p, int q) {
		short v = get(p,q);
		set((short)(v + 1), p,q);
		return v;
	}
	public short postInc(int p, int q, int r) {
		short v = get(p,q,r);
		set((short)(v + 1), p,q,r);
		return v;
	}
	public short postInc(int p, int q, int r, int s) {
		short v = get(p,q,r,s);
		set((short)(v + 1), p,q,r,s);
		return v;
	}
	public short postDec(int p) {
		short v = get(p);
		set((short)(v - 1), p);
		return v;
	}
	public short postDec(int p, int q) {
		short v = get(p,q);
		set((short)(v - 1), p,q);
		return v;
	}
	public short postDec(int p, int q, int r) {
		short v = get(p,q,r);
		set((short)(v - 1), p,q,r);
		return v;
	}
	public short postDec(int p, int q, int r, int s) {
		short v = get(p,q,r,s);
		set((short)(v - 1), p,q,r,s);
		return v;
	}

	/**
	 * Return the local chunk of this distributed array.  The result will have a
	 * 1-dimensional 0-based contiguous region.
	 * FIXME: this is just a stub for now.
	 * @return the local chunk of this array.
	 */
	public ShortReferenceArray local() {
		return this;
	}
}
