package x10.lang;

/** The class of all multidimensional, settable distributed int arrays
 * in X10.  Specialized from ReferenceArray by replacing the type parameter
 * with double.

 * Handtranslated from the X10 code in x10/lang/DoubleReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class DoubleReferenceArray extends doubleArray {
	
	public DoubleReferenceArray( dist D) {
		super( D );
	}

	abstract public double set( double v, point/*(region)*/ p);
	abstract /*value*/ public double set(double v, int p);
	abstract /*value*/ public double set(double v, int p, int q);
	abstract /*value*/ public double set(double v, int p, int q, int r);
	abstract /*value*/ public double set(double v, int p, int q, int r, int s);
	
	public double addSet( double v, point/*(region)*/ p) {
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
	
	public double mulSet( double v, point/*(region)*/ p) {
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
	public double subSet( double v, point/*(region)*/ p) {
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
	public double divSet( double v, point/*(region)*/ p) {
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
	
	
}
