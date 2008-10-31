/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;


/**
 * The interface of all multidimensional, settable distributed int arrays
 * in X10.  Specialized from ReferenceArray by replacing the type parameter
 * with boolean.

 * Handtranslated from the X10 code in x10/lang/BooleanReferenceArray.x10
 * 
 * @author vj 1/9/2005
 * @author igor 09/13/2006 -- made an interface
 */
public abstract class BooleanReferenceArray extends booleanArray {

	protected BooleanReferenceArray(dist d, boolean mutable) {
		super(d, mutable);
	}

	abstract public boolean set(boolean v, point/*(region)*/ p);
	abstract public boolean setOrdinal(boolean v, int p);
	abstract /*value*/ public boolean set(boolean v, int p);
	abstract /*value*/ public boolean set(boolean v, int p, int q);
	abstract /*value*/ public boolean set(boolean v, int p, int q, int r);
	abstract /*value*/ public boolean set(boolean v, int p, int q, int r, int s);

	abstract public boolean bitAndSet(boolean v, point/*(region)*/p);
	abstract public boolean bitAndSet(boolean v, int p);
	abstract public boolean bitAndSet(boolean v, int p, int q);
	abstract public boolean bitAndSet(boolean v, int p, int q, int r);
	abstract public boolean bitAndSet(boolean v, int p, int q, int r, int s);

	abstract public boolean bitOrSet(boolean v, point/*(region)*/p);
	abstract public boolean bitOrSet(boolean v, int p);
	abstract public boolean bitOrSet(boolean v, int p, int q);
	abstract public boolean bitOrSet(boolean v, int p, int q, int r);
	abstract public boolean bitOrSet(boolean v, int p, int q, int r, int s);

	abstract public boolean bitXorSet(boolean v, point/*(region)*/p);
	abstract public boolean bitXorSet(boolean v, int p);
	abstract public boolean bitXorSet(boolean v, int p, int q);
	abstract public boolean bitXorSet(boolean v, int p, int q, int r);
	abstract public boolean bitXorSet(boolean v, int p, int q, int r, int s);

	/**
	 * Return the local chunk of this distributed array.  The result will have a
	 * 1-dimensional 0-based contiguous region.
	 * FIXME: this is just a stub for now.
	 * @return the local chunk of this array.
	 */
	public BooleanReferenceArray local() {
		return this;
	}
}
