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
 * with char.
 * 
 * Handtranslated from the X10 code in x10/lang/CharReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class CharReferenceArray extends charArray {
	
	public CharReferenceArray(dist D, boolean mutable) {
		super(D, mutable);
	}

	abstract public char set(char v, point/*(region)*/ p);
	abstract public char setOrdinal(char v, int p);
	abstract /*value*/ public char set(char v, int p);
	abstract /*value*/ public char set(char v, int p, int q);
	abstract /*value*/ public char set(char v, int p, int q, int r);
	abstract /*value*/ public char set(char v, int p, int q, int r, int s);	

	/**
	 * Return the local chunk of this distributed array.  The result will have a
	 * 1-dimensional 0-based contiguous region.
	 * FIXME: this is just a stub for now.
	 * @return the local chunk of this array.
	 */
	public CharReferenceArray local() {
		return this;
	}
}
