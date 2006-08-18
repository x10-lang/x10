package x10.lang;
/**
 * @author cmd
 */

import java.util.Iterator; 

abstract public class structureArray /*( distribution distribution )*/ 
/*implements Cloneable, Serializable */
extends x10Array {

	protected int _elementSize;
	final public int getElementSize(){ return _elementSize;}
	
	final protected int getStartingElementIndex(int userIndex){
		return userIndex * _elementSize;
	}
	public final dist distribution;

	public dist getDistribution() { return distribution;}
	/*parameter*/ public final /*nat*/int rank /*= distribution.rank*/;
	/*parameter*/ public final region/*(rank)*/ region /*= distribution.region*/;
	
	protected structureArray( dist D,int elSize) {
		this.distribution = D;
		this.region = D.region;
		this.rank = D.rank;
		this._elementSize = elSize;
	}
	
	public static interface binaryOp {
		int apply(int r, int s);
	}
	public static final binaryOp sub = new binaryOp() { public int apply(int r, int s) { return r-s;}};
	public static final binaryOp add = new binaryOp() { public int apply(int r, int s) { return r+s;}};
	public static final binaryOp mul = new binaryOp() { public int apply(int r, int s) { return r*s;}};
	public static final binaryOp div = new binaryOp() { public int apply(int r, int s) { return r/s;}};
	public static final binaryOp max = new binaryOp() { public int apply(int r, int s) { return Math.max(r,s);}};
	public static interface unaryOp {
		int apply(int r);
	}
	public static final unaryOp abs = new unaryOp() { public int apply(int r) { return Math.abs(r);}};
	
	public static interface pointwiseOp/*(region r)*/ {
		int apply(point/*(r)*/ p);
	}
	
	abstract public static /*value*/ class factory {
		/** Return the unique int value array initialized with 0 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public structureArray structureValueArray( /*nat*/ int k,int elSize) {
			return structureValueArray(k, 0,elSize);
		}
		/** Return the unique int value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public structureArray/*(:rank=1)*/  structureValueArray(/*nat*/ int k, int initVal,int elSize) { 
			return structureValueArray(x10.lang.dist.factory.local(k), initVal,elSize);
		}
		/** Return the unique int value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public structureArray/*(:rank=1)*/ structureValueArray(/*nat*/ int k, pointwiseOp init,int elSize) {
			return structureValueArray( x10.lang.dist.factory.local(k), init,elSize);
		}
		
		abstract public 
		/*(distribution D)*/ structureArray/*(D)*/ structureValueArray(dist D, int init,int elSize);
		abstract public 
		/*(distribution D)*/ structureArray/*(D)*/ structureValueArray( dist D, 
				pointwiseOp/*(D.region)*/ init,int elSize);
		/** Return the unique int value array initialized with 0 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public StructureReferenceArray StructureReferenceArray( /*nat*/ int k,int elSize) {
			return StructureReferenceArray(k, (int) 0,elSize);
		}
		/** Return the unique int value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public StructureReferenceArray/*(:rank=1)*/  StructureReferenceArray(/*nat*/ int k, int initVal,int elSize) { 
			return StructureReferenceArray(x10.lang.dist.factory.local(k), initVal, elSize);
		}
		/** Return the unique int value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public StructureReferenceArray/*(:rank=1)*/ StructureReferenceArray(/*nat*/ int k, pointwiseOp init,int elSize) {
			return StructureReferenceArray( x10.lang.dist.factory.local(k), init,elSize);
		}
		
		public StructureReferenceArray StructureReferenceArray( dist D,int elSize) {
			return StructureReferenceArray( D, (int) 0,elSize);
		}
		abstract public 
		/*(distribution D)*/ StructureReferenceArray/*(D)*/ StructureReferenceArray(dist D, int init,int elSize);
		abstract public 
		/*(distribution D)*/ StructureReferenceArray/*(D)*/ StructureReferenceArray( dist D, 
				pointwiseOp/*(D.region)*/ init,int elSize);
	}
	
	public static final factory factory = Runtime.factory.getStructureArrayFactory();  
	
	/** Return the value of the array at the given point in the
	 * region.
	 */
	
	abstract public byte getByte( point/*(region)*/ p,int offset);
	abstract public char getChar( point/*(region)*/ p,int offset);
	abstract public boolean getBoolean( point/*(region)*/ p,int offset);
	abstract public short getShort( point/*(region)*/ p,int offset);
	abstract public int getInt( point/*(region)*/ p,int offset);
	abstract public long getLong( point/*(region)*/ p,int offset);
	abstract public float getFloat( point/*(region)*/ p,int offset);
	abstract public double getDouble( point/*(region)*/ p,int offset);
	
	abstract public byte    getByte( int p,int offset);
	abstract public char    getChar( int p,int offset);
	abstract public boolean getBoolean( int p,int offset);
	abstract public short   getShort( int p,int offset);
	abstract public int     getInt( int p,int offset);
	abstract public long    getLong( int p,int offset);
	abstract public float   getFloat( int p,int offset);
	abstract public double  getDouble( int p,int offset);
	
	
	abstract public byte    getByte( int p, int q,int offset);
	abstract public char    getChar( int p, int q,int offset);
	abstract public boolean getBoolean( int p, int q,int offset);
	abstract public short   getShort( int p, int q,int offset);
	abstract public int     getInt( int p, int q,int offset);
	abstract public long    getLong( int p, int q,int offset);
	abstract public float   getFloat( int p, int q,int offset);
	abstract public double  getDouble( int p, int q,int offset);
	
	
	abstract public byte    getByte( int p, int q, int r,int offset);
	abstract public char    getChar( int p, int q, int r,int offset);
	abstract public boolean getBoolean( int p, int q, int r,int offset);
	abstract public short   getShort( int p, int q, int r,int offset);
	abstract public int     getInt( int p, int q, int r,int offset);
	abstract public long    getLong( int p, int q, int r,int offset);
	abstract public float   getFloat( int p, int q, int r,int offset);
	abstract public double  getDouble( int p, int q, int r,int offset);
	
	
	abstract public byte    getByte( int p, int q, int r, int s,int offset);
	abstract public char    getChar( int p, int q, int r, int s,int offset);
	abstract public boolean getBoolean( int p, int q, int r, int s,int offset);
	abstract public short   getShort( int p, int q, int r, int s,int offset);
	abstract public int     getInt( int p, int q, int r, int s,int offset);
	abstract public long    getLong( int p, int q, int r, int s,int offset);
	abstract public float   getFloat( int p, int q, int r, int s,int offset);
	abstract public double  getDouble( int p, int q, int r, int s,int offset);
	
	
	    
    /** Convenience method for returning the sum of the array.
     * @return sum of the array.
     */
	public int sum() {
		if(true)throw new Exception();
		return 0;//return reduce(add, (short) 0);
	}
	
	
	/** Return an array of B@P defined on the intersection of the
	 region underlying the array and the parameter region R.
	 */
	abstract public /*(region(rank) R)*/
	StructureReferenceArray/*(distribution.restriction(R)())*/  restriction(region R);
	
	/** Return an array of B@P defined on the intersection of 
	 the region underlying this and the parametric distribution.
	 */    
	public  /*(distribution(:rank=this.rank) D)*/ 
	StructureReferenceArray/*(distribution.restriction(D.region)())*/ restriction(dist D) {
	 return restriction(D.region);
	}
	
	/** Take as parameter a distribution D of the same rank as *
	 * this, and defined over a disjoint region. Take as argument an *
	 * array other over D. Return an array whose distribution is the
	 * union of this and D and which takes on the value
	 * this.atValue(p) for p in this.region and other.atValue(p) for p
	 * in other.region.
	 */
	abstract public /*(distribution(:region.disjoint(this.region) && rank=this.rank) D)*/
	StructureReferenceArray/*(distribution.union(other.distribution))*/ union( structureArray other);
	
	/** Return the array obtained by overlaying this array on top of
	 other. The method takes as parameter a distribution D over the
	 same rank. It returns an array over the distribution
	 dist.asymmetricUnion(D).
	 */
	abstract public /*(distribution(:rank=this.rank) D)*/
	StructureReferenceArray/*(distribution.asymmetricUnion(D))*/ overlay( structureArray/*(D)*/ other);
	
	/** Update this array in place by overlaying the array other on top of this. The distribution
	 * of the input array must be a subdistribution of D.
	 * TODO: update the description of the parametric type.
	 */
    abstract public void update( structureArray/*(D)*/ other);
    
	/** Assume given a StructureArray a over the given distribution.
	 * Return a StructureArray with distribution dist 
	 * containing fun(this.atValue(p),a.atValue(p)) for each p in
	 * dist.region.
	 */
	abstract public 
	StructureReferenceArray/*(distribution)*/ lift(binaryOp fun, structureArray/*(distribution)*/ a);
	abstract public 
	StructureReferenceArray/*(distribution)*/ lift(unaryOp fun);
	
	
	abstract public structureArray toValueArray();
	
	public Iterator iterator() {
	 	return region.iterator();
	 }
	public dist toDistribution() {
		return distribution;
	}
}
