package x10.lang;

/** The class of all multidimensional, settable distributed int arrays
 * in X10.  Specialized from ReferenceArray by replacing the type parameter
 * with double.

 * Handtranslated from the X10 code in x10/lang/DoubleReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class DoubleReferenceArray extends doubleArray {
	
	public DoubleReferenceArray( distribution D) {
		super( D );
	}

	abstract public void set( double v, point/*(region)*/ p);
	abstract /*value*/ public void set(double v, int p);
	abstract /*value*/ public void set(double v, int p, int q);
	abstract /*value*/ public void set(double v, int p, int q, int r);
	abstract /*value*/ public void set(double v, int p, int q, int r, int s);
	
}
