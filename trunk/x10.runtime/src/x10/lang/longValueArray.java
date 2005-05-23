package x10.lang;

/** The class of all multidimensional,  distributed long value arrays
 * in X10.  Specialized from valueArray by replacing the type parameter
 * with long.

 * Handtranslated from the X10 code in x10/lang/longValueArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract /*value*/ class longValueArray extends longArray implements ValueType {
	
	public longValueArray( dist D) {
		super( D );
	}
	
}
