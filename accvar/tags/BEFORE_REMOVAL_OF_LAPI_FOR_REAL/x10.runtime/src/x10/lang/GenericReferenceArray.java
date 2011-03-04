package x10.lang;

import x10.compilergenerated.Parameter1;

/** The class of all multidimensional, settable distributed int arrays
 * in X10.  Specialized from ReferenceArray by replacing the type parameter
 * with Parameter1.

 * Handtranslated from the X10 code in x10/lang/DoubleReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class GenericReferenceArray extends genericArray {
	
	public GenericReferenceArray( dist D) {
		super( D );
	}

	abstract public Parameter1 set( Parameter1 v, point/*(region)*/ p);
	abstract /*value*/ public Parameter1 set(Parameter1 v, int p);
	abstract /*value*/ public Parameter1 set(Parameter1 v, int p, int q);
	abstract /*value*/ public Parameter1 set(Parameter1 v, int p, int q, int r);
	abstract /*value*/ public Parameter1 set(Parameter1 v, int p, int q, int r, int s);
	
	
	
}
