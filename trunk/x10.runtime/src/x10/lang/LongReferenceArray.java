package x10.lang;

/** The class of all multidimensional, settable distributed long arrays
 * in X10.  Declares a non-"value" method, set (hence is not subclass-only 
 * immutable, hence name is capitalized).
 * Specialized from ReferenceArray by replacing the type parameter
 * with long.
 
 * Handtranslated from the X10 code in x10/lang/LongReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class LongReferenceArray extends longArray {
	
	public LongReferenceArray( distribution D) {
		super( D );
	}
	abstract public void set( long v, point/*(region)*/ p );
	abstract /*value*/ public void set(long v, int p);
	abstract /*value*/ public void set(long v, int p, int q);
	abstract /*value*/ public void set(long v, int p, int q, int r);
	abstract /*value*/ public void set(long v, int p, int q, int r, int s);
	
}
