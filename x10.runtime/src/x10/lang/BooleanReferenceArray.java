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
public interface BooleanReferenceArray extends booleanArray {
	//public boolean[] getBackingArray() { throw new Error("should not be called");} cannot use this approach since igor changed this to an interface--unsupported for now
	abstract public boolean set( boolean v, point/*(region)*/ p);
	abstract /*value*/ public boolean set(boolean v, int p);
	abstract /*value*/ public boolean set(boolean v, int p, int q);
	abstract /*value*/ public boolean set(boolean v, int p, int q, int r);
	abstract /*value*/ public boolean set(boolean v, int p, int q, int r, int s);

	boolean bitAndSet(boolean v, point/*(region)*/p);
	boolean bitAndSet(boolean v, int p);
	boolean bitAndSet(boolean v, int p, int q);
	boolean bitAndSet(boolean v, int p, int q, int r);
	boolean bitAndSet(boolean v, int p, int q, int r, int s);

	boolean bitOrSet(boolean v, point/*(region)*/p);
	boolean bitOrSet(boolean v, int p);
	boolean bitOrSet(boolean v, int p, int q);
	boolean bitOrSet(boolean v, int p, int q, int r);
	boolean bitOrSet(boolean v, int p, int q, int r, int s);

	boolean bitXorSet(boolean v, point/*(region)*/p);
	boolean bitXorSet(boolean v, int p);
	boolean bitXorSet(boolean v, int p, int q);
	boolean bitXorSet(boolean v, int p, int q, int r);
	boolean bitXorSet(boolean v, int p, int q, int r, int s);
}
