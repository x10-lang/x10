package x10.lang;

/** The class of all multidimensional, settable distributed int arrays
 * in X10.  Specialized from ReferenceArray by replacing the type parameter
 * with float.

 * Handtranslated from the X10 code in x10/lang/FloatReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class FloatReferenceArray extends floatArray {
	
	public FloatReferenceArray( distribution D) {
		super( D );
	}

	abstract public float set( float v, point/*(region)*/ p);
	abstract /*value*/ public float set(float v, int p);
	abstract /*value*/ public float set(float v, int p, int q);
	abstract /*value*/ public float set(float v, int p, int q, int r);
	abstract /*value*/ public float set(float v, int p, int q, int r, int s);
	
	public float addSet( float v, point/*(region)*/ p) {
		return set(get(p)+v,p);
	}
	public float addSet(float v, int p) {
		return set(get(p)+v,p);
	}
	public float addSet(float v, int p, int q) {
		return set(get(p,q)+v,p,q);
	}
	public float addSet(float v, int p, int q, int r) {
		return set(get(p,q,r)+v,p,q,r);
	}
	public float addSet(float v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)+v,p,q,r,s);
	}	
	public float mulSet( float v, point/*(region)*/ p) {
		return set(get(p)*v,p);
	}
	public float mulSet(float v, int p) {
		return set(get(p)*v,p);
	}
	public float mulSet(float v, int p, int q) {
		return set(get(p,q)*v,p,q);
	}
	public float mulSet(float v, int p, int q, int r) {
		return set(get(p,q,r)*v,p,q,r);
	}
	public float mulSet(float v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)*v,p,q,r,s);
	}
	public float subSet( float v, point/*(region)*/ p) {
		return set(get(p)-v,p);
	}
	public float subSet(float v, int p) {
		return set(get(p)-v,p);
	}
	public float subSet(float v, int p, int q) {
		return set(get(p,q)-v,p,q);
	}
	public float subSet(float v, int p, int q, int r) {
		return set(get(p,q,r)-v,p,q,r);
	}
	public float subSet(float v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)-v,p,q,r,s);
	}
	public float divSet( float v, point/*(region)*/ p) {
		return set(get(p)/v,p);
	}
	public float divSet(float v, int p) {
		return set(get(p)/v,p);
	}
	public float divSet(float v, int p, int q) {
		return set(get(p,q)/v,p,q);
	}
	public float divSet(float v, int p, int q, int r) {
		return set(get(p,q,r)/v,p,q,r);
	}
	public float divSet(float v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)/v,p,q,r,s);
	}
	
	
}
