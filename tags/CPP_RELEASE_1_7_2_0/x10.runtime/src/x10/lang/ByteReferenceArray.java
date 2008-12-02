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
 * with byte.
 * 
 * Handtranslated from the X10 code in x10/lang/ByteReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class ByteReferenceArray extends byteArray {
	
	public ByteReferenceArray(dist D, boolean mutable) {
		super(D, mutable);
	}

	abstract public byte set(byte v, point/*(region)*/ p);
	abstract public byte setOrdinal(byte v, int p);
	abstract /*value*/ public byte set(byte v, int p);
	abstract /*value*/ public byte set(byte v, int p, int q);
	abstract /*value*/ public byte set(byte v, int p, int q, int r);
	abstract /*value*/ public byte set(byte v, int p, int q, int r, int s);
	
	public byte addSet(byte v, point/*(region)*/ p) {
		return set((byte) (get(p)+v),p);
	}
	public byte addSet(byte v, int p) {
		return set((byte) (get(p)+v),p);
	}
	public byte addSet(byte v, int p, int q) {
		return set((byte) (get(p,q)+v),p,q);
	}
	public byte addSet(byte v, int p, int q, int r) {
		return set((byte) (get(p,q,r)+v),p,q,r);
	}
	public byte addSet(byte v, int p, int q, int r, int s) {
		return set((byte) (get(p,q,r,s)+v),p,q,r,s);
	}
	
	public byte mulSet(byte v, point/*(region)*/ p) {
		return set((byte) (get(p)*v),p);
	}
	public byte mulSet(byte v, int p) {
		return set((byte) (get(p)*v),p);
	}
	public byte mulSet(byte v, int p, int q) {
		return set((byte) (get(p,q)*v),p,q);
	}
	public byte mulSet(byte v, int p, int q, int r) {
		return set((byte) (get(p,q,r)*v),p,q,r);
	}
	public byte mulSet(byte v, int p, int q, int r, int s) {
		return set((byte) (get(p,q,r,s)*v),p,q,r,s);
	}
	public byte subSet(byte v, point/*(region)*/ p) {
		return set((byte) (get(p)-v),p);
	}
	public byte subSet(byte v, int p) {
		return set((byte) (get(p)-v),p);
	}
	public byte subSet(byte v, int p, int q) {
		return set((byte) (get(p,q)-v),p,q);
	}
	public byte subSet(byte v, int p, int q, int r) {
		return set((byte) (get(p,q,r)-v),p,q,r);
	}
	public byte subSet(byte v, int p, int q, int r, int s) {
		return set((byte) (get(p,q,r,s)-v),p,q,r,s);
	}
	public byte divSet(byte v, point/*(region)*/ p) {
		return set((byte) (get(p)/v),p);
	}
	public byte divSet(byte v, int p) {
		return set((byte) (get(p)/v),p);
	}
	public byte divSet(byte v, int p, int q) {
		return set((byte) (get(p,q)/v),p,q);
	}
	public byte divSet(byte v, int p, int q, int r) {
		return set((byte) (get(p,q,r)/v),p,q,r);
	}
	public byte divSet(byte v, int p, int q, int r, int s) {
		return set((byte) (get(p,q,r,s)/v),p,q,r,s);
	}	
	public byte preInc(int p) {
		return set((byte)(get(p) + 1),p);
	}
	public byte preInc(int p, int q) {
		return set((byte)(get(p,q) + 1),p,q);
	}
	public byte preInc(int p, int q, int r) {
		return set((byte)(get(p,q,r) + 1),p,q,r);
	}
	public byte preInc(int p, int q, int r, int s) {
		return set((byte)(get(p,q,r,s) + 1),p,q,r,s);
	}
	public byte preDec(int p) {
		return set((byte)(get(p) - 1),p);
	}
	public byte preDec(int p, int q) {
		return set((byte)(get(p,q) - 1),p,q);
	}
	public byte preDec(int p, int q, int r) {
		return set((byte)(get(p,q,r) - 1),p,q,r);
	}
	public byte preDec(int p, int q, int r, int s) {
		return set((byte)(get(p,q,r,s) - 1),p,q,r,s);
	}
	public byte postInc(int p) {
		byte v = get(p);
		set((byte)(v + 1), p);
		return v;
	}
	public byte postInc(int p, int q) {
		byte v = get(p,q);
		set((byte)(v + 1), p,q);
		return v;
	}
	public byte postInc(int p, int q, int r) {
		byte v = get(p,q,r);
		set((byte)(v + 1), p,q,r);
		return v;
	}
	public byte postInc(int p, int q, int r, int s) {
		byte v = get(p,q,r,s);
		set((byte)(v + 1), p,q,r,s);
		return v;
	}
	public byte postDec(int p) {
		byte v = get(p);
		set((byte)(v - 1), p);
		return v;
	}
	public byte postDec(int p, int q) {
		byte v = get(p,q);
		set((byte)(v - 1), p,q);
		return v;
	}
	public byte postDec(int p, int q, int r) {
		byte v = get(p,q,r);
		set((byte)(v - 1), p,q,r);
		return v;
	}
	public byte postDec(int p, int q, int r, int s) {
		byte v = get(p,q,r,s);
		set((byte)(v - 1), p,q,r,s);
		return v;
	}

	/**
	 * Return the local chunk of this distributed array.  The result will have a
	 * 1-dimensional 0-based contiguous region.
	 * FIXME: this is just a stub for now.
	 * @return the local chunk of this array.
	 */
	public ByteReferenceArray local() {
		return this;
	}
}
