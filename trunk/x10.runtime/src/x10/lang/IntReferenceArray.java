package x10.lang;

/** The class of all multidimensional, settable distributed int arrays
 * in X10.  Declares a non-"value" method, set (hence is not subclass-only 
 * immutable, hence name is capitalized).
 * Specialized from ReferenceArray by replacing the type parameter
 * with int.
 
 * Handtranslated from the X10 code in x10/lang/IntReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class IntReferenceArray extends intArray {
	
	public IntReferenceArray( distribution D) {
		super( D );
	}
	abstract public int set( int v, point/*(region)*/ p );
	abstract public int set(int v, int p);
	abstract public int set(int v, int p, int q);
	abstract public int set(int v, int p, int q, int r);
	abstract public int set(int v, int p, int q, int r, int s);
	
	public int addSet( int v, point/*(region)*/ p) {
		return set(get(p)+v,p);
	}
	public int addSet(int v, int p) {
		return set(get(p)+v,p);
	}
	public int addSet(int v, int p, int q) {
		return set(get(p,q)+v,p,q);
	}
	public int addSet(int v, int p, int q, int r) {
		return set(get(p,q,r)+v,p,q,r);
	}
	public int addSet(int v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)+v,p,q,r,s);
	}
	
	public int mulSet( int v, point/*(region)*/ p) {
		return set(get(p)*v,p);
	}
	public int mulSet(int v, int p) {
		return set(get(p)*v,p);
	}
	public int mulSet(int v, int p, int q) {
		return set(get(p,q)*v,p,q);
	}
	public int mulSet(int v, int p, int q, int r) {
		return set(get(p,q,r)*v,p,q,r);
	}
	public int mulSet(int v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)*v,p,q,r,s);
	}
	public int subSet( int v, point/*(region)*/ p) {
		return set(get(p)-v,p);
	}
	public int subSet(int v, int p) {
		return set(get(p)-v,p);
	}
	public int subSet(int v, int p, int q) {
		return set(get(p,q)-v,p,q);
	}
	public int subSet(int v, int p, int q, int r) {
		return set(get(p,q,r)-v,p,q,r);
	}
	public int subSet(int v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)-v,p,q,r,s);
	}
	public int divSet( int v, point/*(region)*/ p) {
		return set(get(p)/v,p);
	}
	public int divSet(int v, int p) {
		return set(get(p)/v,p);
	}
	public int divSet(int v, int p, int q) {
		return set(get(p,q)/v,p,q);
	}
	public int divSet(int v, int p, int q, int r) {
		return set(get(p,q,r)/v,p,q,r);
	}
	public int divSet(int v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)/v,p,q,r,s);
	}
	public int modSet( int v, point/*(region)*/ p) {
		return set(get(p)%v,p);
	}
	public int modSet(int v, int p) {
		return set(get(p)%v,p);
	}
	public int modSet(int v, int p, int q) {
		return set(get(p,q)%v,p,q);
	}
	public int modSet(int v, int p, int q, int r) {
		return set(get(p,q,r)%v,p,q,r);
	}
	public int modSet(int v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)%v,p,q,r,s);
	}
	public int bitAndSet( int v, point/*(region)*/ p) {
		return set(get(p)&v,p);
	}
	public int bitAndSet(int v, int p) {
		return set(get(p)&v,p);
	}
	public int bitAndSet(int v, int p, int q) {
		return set(get(p,q)&v,p,q);
	}
	public int bitAndSet(int v, int p, int q, int r) {
		return set(get(p,q,r)&v,p,q,r);
	}
	public int bitAndSet(int v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)&v,p,q,r,s);
	}
	public int bitOrSet( int v, point/*(region)*/ p) {
		return set(get(p)|v,p);
	}
	public int bitOrSet(int v, int p) {
		return set(get(p)|v,p);
	}
	public int bitOrSet(int v, int p, int q) {
		return set(get(p,q)|v,p,q);
	}
	public int bitOrSet(int v, int p, int q, int r) {
		return set(get(p,q,r)|v,p,q,r);
	}
	public int bitOrSet(int v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)|v,p,q,r,s);
	}
	public int bitXorSet( int v, point/*(region)*/ p) {
		return set(get(p)^v,p);
	}
	public int bitXorSet(int v, int p) {
		return set(get(p)^v,p);
	}
	public int bitXorSet(int v, int p, int q) {
		return set(get(p,q)^v,p,q);
	}
	public int bitXorSet(int v, int p, int q, int r) {
		return set(get(p,q,r)^v,p,q,r);
	}
	public int bitXorSet(int v, int p, int q, int r, int s) {
		return set(get(p,q,r,s)^v,p,q,r,s);
	}
	public int shlSet( int v, point/*(region)*/ p) {
		return set(get(p) << v,p);
	}
	public int shlSet(int v, int p) {
		return set(get(p) << v,p);
	}
	public int shlSet(int v, int p, int q) {
		return set(get(p,q) << v,p,q);
	}
	public int shlSet(int v, int p, int q, int r) {
		return set(get(p,q,r) << v,p,q,r);
	}
	public int shlSet(int v, int p, int q, int r, int s) {
		return set(get(p,q,r,s) << v,p,q,r,s);
	}
	
	public int shrSet( int v, point/*(region)*/ p) {
		return set(get(p) >> v,p);
	}
	public int shrSet(int v, int p) {
		return set(get(p) >> v,p);
	}
	public int shrSet(int v, int p, int q) {
		return set(get(p,q) >> v,p,q);
	}
	public int shrSet(int v, int p, int q, int r) {
		return set(get(p,q,r) >> v,p,q,r);
	}
	public int shrSet(int v, int p, int q, int r, int s) {
		return set(get(p,q,r,s) >> v,p,q,r,s);
	}
	public int ushrSet( int v, point/*(region)*/ p) {
		return set(get(p) >>> v,p);
	}
	public int ushrSet(int v, int p) {
		return set(get(p) >>> v,p);
	}
	public int ushrSet(int v, int p, int q) {
		return set(get(p,q) >>> v,p,q);
	}
	public int ushrSet(int v, int p, int q, int r) {
		return set(get(p,q,r) >>> v,p,q,r);
	}
	public int ushrSet(int v, int p, int q, int r, int s) {
		return set(get(p,q,r,s) >>> v,p,q,r,s);
	}
}
