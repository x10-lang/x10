package x10.lang;

/** The class of all multidimensional, settable distributed int arrays
 * in X10.  Specialized from ReferenceArray by replacing the type parameter
 * with short.

 * Handtranslated from the X10 code in x10/lang/ShortReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class ShortReferenceArray extends shortArray {
	
	public ShortReferenceArray( distribution D) {
		super( D );
	}

	abstract public short set( short v, point/*(region)*/ p);
	abstract /*value*/ public short set(short v, int p);
	abstract /*value*/ public short set(short v, int p, int q);
	abstract /*value*/ public short set(short v, int p, int q, int r);
	abstract /*value*/ public short set(short v, int p, int q, int r, int s);
	
	public short addSet( short v, point/*(region)*/ p) {
		return set((short) (get(p)+v),p);
	}
	public short addSet(short v, int p) {
		return set((short) (get(p)+v),p);
	}
	public short addSet(short v, int p, int q) {
		return set((short) (get(p,q)+v),p,q);
	}
	public short addSet(short v, int p, int q, int r) {
		return set((short) (get(p,q,r)+v),p,q,r);
	}
	public short addSet(short v, int p, int q, int r, int s) {
		return set((short) (get(p,q,r,s)+v),p,q,r,s);
	}
	public short mulSet( short v, point/*(region)*/ p) {
		return set((short) (get(p)*v),p);
	}
	public short mulSet(short v, int p) {
		return set((short) (get(p)*v),p);
	}
	public short mulSet(short v, int p, int q) {
		return set((short) (get(p,q)*v),p,q);
	}
	public short mulSet(short v, int p, int q, int r) {
		return set((short) (get(p,q,r)*v),p,q,r);
	}
	public short mulSet(short v, int p, int q, int r, int s) {
		return set((short) (get(p,q,r,s)*v),p,q,r,s);
	}
	public short subSet( short v, point/*(region)*/ p) {
		return set((short) (get(p)-v),p);
	}
	public short subSet(short v, int p) {
		return set((short) (get(p)-v),p);
	}
	public short subSet(short v, int p, int q) {
		return set((short) (get(p,q)-v),p,q);
	}
	public short subSet(short v, int p, int q, int r) {
		return set((short) (get(p,q,r)-v),p,q,r);
	}
	public short subSet(short v, int p, int q, int r, int s) {
		return set((short) (get(p,q,r,s)-v),p,q,r,s);
	}
	public short divSet( short v, point/*(region)*/ p) {
		return set((short) (get(p)/v),p);
	}
	public short divSet(short v, int p) {
		return set((short) (get(p)/v),p);
	}
	public short divSet(short v, int p, int q) {
		return set((short) (get(p,q)/v),p,q);
	}
	public short divSet(short v, int p, int q, int r) {
		return set((short) (get(p,q,r)/v),p,q,r);
	}
	public short divSet(short v, int p, int q, int r, int s) {
		return set((short) (get(p,q,r,s)/v),p,q,r,s);
	}
}
