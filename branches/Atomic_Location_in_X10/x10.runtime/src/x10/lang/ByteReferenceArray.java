package x10.lang;

/** The class of all multidimensional, settable distributed int arrays
 * in X10.  Specialized from ReferenceArray by replacing the type parameter
 * with byte.

 * Handtranslated from the X10 code in x10/lang/ByteReferenceArray.x10
 * 
 * @author vj 1/9/2005
 */

public abstract class ByteReferenceArray extends byteArray {
	
	public ByteReferenceArray( dist D) {
		super( D );
	}

	abstract public byte set( byte v, point/*(region)*/ p);
	abstract /*value*/ public byte set(byte v, int p);
	abstract /*value*/ public byte set(byte v, int p, int q);
	abstract /*value*/ public byte set(byte v, int p, int q, int r);
	abstract /*value*/ public byte set(byte v, int p, int q, int r, int s);
	
	public byte addSet( byte v, point/*(region)*/ p) {
		return set((byte) (get(p)+v),p);
	}
	public byte addSet(byte v, int p) {
		return set((byte) (get(p)+v),p);
	}
	public byte addSet(byte v, int p, int q) {
		return set((byte) (get(p,q)+v),p,q);
	}
	public byte addSet(byte v, int p, int q, int r) {
		return set((byte) (get(p,q,r)+v),p,q,r);
	}
	public byte addSet(byte v, int p, int q, int r, int s) {
		return set((byte) (get(p,q,r,s)+v),p,q,r,s);
	}
	
	public byte mulSet( byte v, point/*(region)*/ p) {
		return set((byte) (get(p)*v),p);
	}
	public byte mulSet(byte v, int p) {
		return set((byte) (get(p)*v),p);
	}
	public byte mulSet(byte v, int p, int q) {
		return set((byte) (get(p,q)*v),p,q);
	}
	public byte mulSet(byte v, int p, int q, int r) {
		return set((byte) (get(p,q,r)*v),p,q,r);
	}
	public byte mulSet(byte v, int p, int q, int r, int s) {
		return set((byte) (get(p,q,r,s)*v),p,q,r,s);
	}
	public byte subSet( byte v, point/*(region)*/ p) {
		return set((byte) (get(p)-v),p);
	}
	public byte subSet(byte v, int p) {
		return set((byte) (get(p)-v),p);
	}
	public byte subSet(byte v, int p, int q) {
		return set((byte) (get(p,q)-v),p,q);
	}
	public byte subSet(byte v, int p, int q, int r) {
		return set((byte) (get(p,q,r)-v),p,q,r);
	}
	public byte subSet(byte v, int p, int q, int r, int s) {
		return set((byte) (get(p,q,r,s)-v),p,q,r,s);
	}
	public byte divSet( byte v, point/*(region)*/ p) {
		return set((byte) (get(p)/v),p);
	}
	public byte divSet(byte v, int p) {
		return set((byte) (get(p)/v),p);
	}
	public byte divSet(byte v, int p, int q) {
		return set((byte) (get(p,q)/v),p,q);
	}
	public byte divSet(byte v, int p, int q, int r) {
		return set((byte) (get(p,q,r)/v),p,q,r);
	}
	public byte divSet(byte v, int p, int q, int r, int s) {
		return set((byte) (get(p,q,r,s)/v),p,q,r,s);
	}	
}
