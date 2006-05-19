/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import x10.array.Helper;
import x10.array.Operator;
import x10.array.StructureArray;
import x10.base.Allocator;
import x10.array.Distribution_c;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.lang.StructureReferenceArray;
import x10.lang.dist;
import x10.lang.point;
import x10.lang.region;
import x10.runtime.Configuration;


/**
 * @author Chris Donawa
 * 
 * A structure array represents an array of structures.  This is implemented on top
 * of an array of integers.
 */
public class StructureArray_c extends StructureArray implements UnsafeContainer, Cloneable {
	
	private final boolean safe_;
	private final MemoryBlock arr_;
	public final boolean mutable_;
	
	public boolean valueEquals(Indexable other) {
		return arr_.valueEquals(((StructureArray_c)other).arr_);
	}
	
	public StructureReferenceArray lift( StructureArray.binaryOp op, x10.lang.structureArray arg ) {
		assert arg.distribution.equals(distribution);
		if (true)
            throw new RuntimeException("unimplemented");
		
		StructureReferenceArray result = newInstance(distribution,arg.getElementSize());
		
		/*
		 IntReferenceArray result = newInstance(distribution);
		 for (Iterator it = distribution.region.iterator(); it.hasNext();) {
		 point p = (point) it.next();
		 result.set(op.apply(this.get(p), arg.get(p)),p);
		 }
		 */
		return result;
		
	}
	public StructureReferenceArray overlay(x10.lang.structureArray d) {
		if (true)
            throw new RuntimeException("unimplemented");
		
		dist dist = distribution.overlay(d.distribution);
		StructureArray_c ret = new StructureArray_c(dist, 0, false,false,d.getElementSize());        /*
		for (Iterator it = dist.iterator(); it.hasNext(); ) {
		point p = (point) it.next();
		int val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
		ret.set(val, p);
		}
		
		*/
		return ret;
		
	}
	
	public void update(x10.lang.structureArray d) {
		
		assert (region.contains(d.region));
		
		throw new RuntimeException("unimplemented");
		/*
		 for (Iterator it = d.iterator(); it.hasNext(); ) {
		 point p = (point) it.next();
		 set(d.get(p), p);
		 }
		 */
	}
	
	public StructureReferenceArray union(x10.lang.structureArray d) {
		if (true)
            throw new RuntimeException("unimplemented");
		dist dist = distribution.union(d.distribution);
		StructureArray_c ret = new StructureArray_c(dist, 0, safe_,false,d.getElementSize());
		/*
		 for (Iterator it = dist.iterator(); it.hasNext(); ) {
		 point p = (point) it.next();
		 int val = (distribution.region.contains(p)) ? get(p) : d.get(p);
		 ret.set(val, p);
		 }
		 */
		return ret;
	}
	
	/**
	 *  This constructor must not be used directly by an application programmer.
	 * Arrays are constructed by the corresponding factory methods in 
	 * x10.lang.Runtime.
	 */
	protected StructureArray_c(Distribution_c d, boolean safe,int elSize) {
		this(d, (Operator.Pointwise) null, safe,elSize);
	}
	
	protected StructureArray_c(Distribution_c d, Operator.Pointwise c, boolean safe,
			int elSize) {
		this( d, c, safe, true,elSize);
	}
    
	protected StructureArray_c(Distribution_c d, Operator.Pointwise c, boolean safe, boolean mutable,
			int elSize) {
		super(d,elSize);
		assert (this._elementSize == elSize);
		this.mutable_ = mutable;
		this.safe_ = safe;
		int count =  d.region.size() * elSize;
		
		assert(elSize >0);
		if (!safe) {
			int rank = d.region.rank;
			int ranks[] = new int[rank];
			for (int i = 0; i < rank; ++i) 
				ranks[i] = d.region.rank(i).size();
			this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_INT);
		} else {
			this.arr_ =Allocator.allocSafe(count, Integer.TYPE); 
		}
		
		if (c != null)
			pointwise(this, c);
		
	}
	
	/** Create a new array per the given distribution, initialized to c.
	 * 
	 * @param d
	 * @param c
	 * @param safe
	 */
	public StructureArray_c( dist d, int c,int elSize) {
		this(d, c, true,elSize);
	}
    
	public StructureArray_c( dist d, int c, boolean safe,int elSize ) {
		this(d, c, safe, true,elSize);
	}
    
	public StructureArray_c( dist d, int c, boolean safe, boolean mutable,int elSize ) {
		super(d,elSize);
		assert(this._elementSize == elSize);
		this.mutable_ = mutable;
		int count =  d.region.size() * elSize;
		
		assert(elSize >0);
		this.safe_ = safe;
		if (!safe) {
			int rank = d.region.rank;
			int ranks[] = new int[rank];
			for (int i = 0; i < rank; ++i) 
				ranks[i] = d.region.rank(i).size();
			this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_INT);
		} else {
			this.arr_ =Allocator.allocSafe(count, Integer.TYPE);
		}
		scan(this, new Assign(c));
		
	}
    
	public StructureArray_c( dist d, StructureArray.pointwiseOp f,int elSize) {
		this(d, f, true,elSize);
	}
    
	public StructureArray_c( dist d, StructureArray.pointwiseOp f, boolean safe,int elSize) {
		this(d, f, safe, true,elSize);
	}
    
	public StructureArray_c( dist d, StructureArray.pointwiseOp f, boolean safe, boolean mutable,
			int elSize) {
		super(d,elSize);
		assert(this._elementSize == elSize);
		this.mutable_ = mutable;
		this._elementSize = elSize;
		assert(elSize >0);
		int count =  d.region.size() *elSize;
		this.safe_ = safe;
		if (!safe) {
			int rank = d.region.rank;
			int ranks[] = new int[rank];
			for (int i = 0; i < rank; ++i) 
				ranks[i] = d.region.rank(i).size();
			this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_INT);
		} else {
			this.arr_ =Allocator.allocSafe(count, Integer.TYPE);
		}
		if (f != null)
			scan(this, f);
	}
	
	private StructureArray_c( dist d, int[] a, boolean safe, boolean mutable,int elSize) {
		super(d,elSize);
		assert(this._elementSize == elSize);
		int count =  d.region.size()*elSize;
		this._elementSize = elSize;
		assert(elSize >0);
		this.safe_ = safe;
		if (!safe) {
			int rank = d.region.rank;
			int ranks[] = new int[rank];
			for (int i = 0; i < rank; ++i) 
				ranks[i] = d.region.rank(i).size();
			this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_INT);
		} else {
			this.arr_ =Allocator.allocSafe(count, Integer.TYPE);
		}
		this.mutable_ = mutable;
	}
	/** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
	 * 
	 * @param a
	 * @return
	 */
	public static StructureArray_c StructureArray_c( int[] a, boolean safe, boolean mutable,int elSize ) {
		dist d = Runtime.factory.getDistributionFactory().local(a.length);
		return new StructureArray_c(d, a, safe, mutable ,elSize);
	}
	
	public void keepItLive() {}
	
	public long getUnsafeAddress() {
		return arr_.getUnsafeAddress();
	}
	
	public long getUnsafeDescriptor() {
		return arr_.getUnsafeDescriptor();
	}
	
	/* Overrides the superclass method - this implementation is more efficient */
	public void reduction(Operator.Reduction op) {
		int count = arr_.count();
		throw new RuntimeException("unimplemented");
		/*
		 for (int i  = 0; i < count; ++i) 
		 op.apply(arr_.getStructure(i));
		 */
	}
	
	/* Overrides the superclass method - this implementation is more efficient */
	protected void assign(StructureArray rhs) {
		assert rhs instanceof StructureArray_c;
		throw new RuntimeException("unimplemented");
		
		/*
		 StructureArray_c rhs_t = (StructureArray_c) rhs;
		 if (rhs.distribution.equals(distribution)) {
		 int count = arr_.count();
		 for (int i  = 0; i < count; ++i) 
		 arr_.setStructure(rhs_t.arr_.getStructure(i), i);
		 } else 
		 // fall back to generic implementation
		  super.assign(rhs);
		  */
	}
	
	protected StructureArray newInstance(dist d,int elSize) {
		assert d instanceof Distribution_c;
		
		return new StructureArray_c((Distribution_c) d, safe_,elSize);	
	}
	
	protected StructureArray newInstance(dist d, Operator.Pointwise c,int elSize) {
		assert d instanceof Distribution_c;
		
		return new StructureArray_c((Distribution_c) d, c, safe_,elSize);	
	}
	
	
	
	public StructureReferenceArray lift( StructureArray.unaryOp op ) {
		StructureArray result = newInstance(distribution,0);// FIXME get elSize
		if(true)throw new RuntimeException("unimplemented");
		/*
		 for (Iterator it = distribution.region.iterator(); it.hasNext();) {
		 point p = (point) it.next();
		 result.set((short) op.apply(this.get(p)),p);
		 }
		 */
		return result;
	}
	
	public int reduce( StructureArray.binaryOp op, int unit ) {
		int result = unit;
		if(true)throw new RuntimeException("unimplemented");
		/*
		 for (Iterator it = distribution.region.iterator(); it.hasNext();) {
		 point p = (point) it.next();
		 result = op.apply(this.get(p), (short) result);
		 }
		 */
		return result;
	}
	
	public StructureReferenceArray scan( binaryOp op, int unit ) {
		int temp = unit;
		StructureArray result = newInstance(distribution,0);//FIXME
		if (true)
            throw new RuntimeException("unimplemented");
		/*
		 for (Iterator it = distribution.region.iterator(); it.hasNext();) {
		 point p = (point) it.next();
		 temp = (short) op.apply(this.get(p), temp);
		 result.set(temp, p);
		 }
		 */
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see x10.lang.StructureArray#set(int, int[])
	 */
	public byte    setByte( byte v, point/*(region)*/ p,int offset){ return v;}
	public char    setChar( char v, point/*(region)*/ p,int offset){ return v;}
	public boolean setBoolean( boolean v, point/*(region)*/ p,int offset){ return v;}
	public short   setShort( short v, point/*(region)*/ p,int offset){ return v;}
	public int     setInt( int v, point/*(region)*/ p,int offset){ return v;}
	public long    setLong( long v, point/*(region)*/ p,int offset){ return v;}
	public float   setFloat( float v, point/*(region)*/ p,int offset){ return v;}
	public double  setDouble( double v, point/*(region)*/ p,int offset){ return v;}
	
	
	public byte    setByte( byte v,int d0,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist, d0,true);
		return setNormalizedByte(v,d0,offset);
	}
    
	protected byte    setNormalizedByte( byte v,int d0,int offset){ 
		assert( offset>0);
		d0 =  getStartingElementIndex(d0);
		
		d0 = d0 + offset/4;
		int rawData = arr_.getInt(d0);
		switch(offset%4){
		case 0:
			rawData = rawData & ~0xff;
			rawData = rawData | ((int)v & 0xff);
			break;
		case 1:
			rawData = rawData & ~0xff00;
			rawData = rawData | (((int)v << 8) & 0xff00);
			break;
		case 2:
			rawData = rawData & ~0xff0000;
			rawData = rawData | (((int)v  << 16) & 0xff0000);
			break;
		case 3:
			rawData = rawData & ~0xff000000;
			rawData = rawData | (((int)v  << 24) & 0xff000000);
			break;
		}
		arr_.setInt(rawData,d0);
		return v;
	}
    
	public char    setChar( char v,int d0, int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,true);
		return setNormalizedChar(v,d0,offset);
	}
    
	public char    setNormalizedChar( char v,int d0, int offset){ 
		assert( offset>0 );
		
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		int rawData = arr_.getInt(d0);
		switch(offset%4){
		case 0:
			rawData = rawData & ~0xffff;
			rawData = rawData | ((int)v & 0xffff);
			break;
		case 1:
			throw new RuntimeException("invalid offset:"+offset);
			
		case 2:
			rawData = rawData & ~0xffff0000;
			rawData = rawData | (((int)v  << 16) & 0xffff0000);
			break;
			
		}
		arr_.setInt(rawData,d0);
		return v;
	}
	
	
	public boolean setBoolean( boolean v,int d0, int offset){
		byte value=0;
		if (v) value=1;
		setByte(value,d0,offset);
		return v;
	}
	
	public short   setShort( short v,int d0, int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,true);
		return setNormalizedShort(v,d0,offset);
	}
	public short   setNormalizedShort( short v,int d0, int offset){ 
		assert( offset>0 );
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		int rawData = arr_.getInt(d0);
		switch(offset%4){
		case 0:
			rawData = rawData & ~0xffff;
			rawData = rawData | ((int)v & 0xffff);
			break;
		case 1:
			throw new RuntimeException("invalid offset:"+offset);
		case 2:
			rawData = rawData & ~0xffff0000;
			rawData = rawData | (((int)v  << 16) & 0xffff0000);
			break;
			
		}
		arr_.setInt(rawData,d0);
		return v;
	}
	
	public int setInt (int v, int d0, int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,true);
		return setNormalizedInt(v,d0,offset);
	}
	
	public int     setNormalizedInt( int v,int d0,int offset){
		assert(0 == offset%4);
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		arr_.setInt(v,d0);
		return v;
	}
	
	public long    setLong( long v,int d0,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,true);
		return setNormalizedLong(v,d0,offset);
	}
	public long    setNormalizedLong( long v,int d0,int offset){
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		int highInt = (int)((v >> 32) & 0xffffffff);
		int lowInt = (int)(v & 0xffffffff);
		arr_.setInt(lowInt,d0);
		arr_.setInt(highInt,d0+1);
		return v;
	}
	
	public float   setFloat( float v,int d0, int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,true);
		return setNormalizedFloat(v,d0,offset);
	}
	public float   setNormalizedFloat( float v,int d0, int offset){ 
		
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		arr_.setInt(Float.floatToIntBits(v),d0);
		return v;}
	
	
	public double  setDouble( double v,int d0,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,true);
		return setNormalizedDouble(v,d0,offset);
	}
	public double  setNormalizedDouble( double v,int d0,int offset){ 
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		arr_.setLong(Double.doubleToLongBits(v),d0);
		return v;
	}
	
	
	
	public byte    setByte( byte v,int d0,int d1,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        d0 = Helper.ordinal(localDist,d0,d1,true);
		return setNormalizedByte(v,d0,offset);
	}
	public char    setChar( char v,int d0,int d1, int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        d0 = Helper.ordinal(localDist,d0,d1,true);
		return setNormalizedChar(v,d0,offset);
	}	
	
	public boolean setBoolean( boolean v,int d0, int d1,int offset){
		byte value=0;
		if (v) value=1;
		setByte(value,d0,d1,offset);
		return v;
	}
    
	public short   setShort( short v,int d0,int d1, int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        d0 = Helper.ordinal(localDist,d0,d1,true);
		return setNormalizedShort(v,d0,offset);
	}
    
	public int     setInt( int v,int d0,int d1,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        d0 = Helper.ordinal(localDist,d0,d1,true);
		return setNormalizedInt(v,d0,offset);
	}
    
	public long    setLong( long v,int d0,int d1,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));
        d0 = Helper.ordinal(localDist,d0,d1,true);
		return setNormalizedLong(v,d0,offset);
	}
    
	public float   setFloat( float v,int d0, int d1,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));
        d0 = Helper.ordinal(localDist,d0,d1,true);
		return setNormalizedFloat(v,d0,offset);
	}
	public double  setDouble( double v,int d0,int d1,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));
        d0 = Helper.ordinal(localDist,d0,d1,true);
		return setNormalizedDouble(v,d0,offset);
	}
	
	public byte    setByte( byte v,int d0,int d1,int d2,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
        d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return setNormalizedByte(v,d0,offset);
	}
	public char    setChar( char v,int d0,int d1,int d2, int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
        d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return setNormalizedChar(v,d0,offset);
	}	
	
	public boolean setBoolean( boolean v,int d0, int d1,int d2,int offset){
		byte value=0;
		if (v) value=1;
		setByte(value,d0,d1,d2,offset);
		return v;
	}
	public short   setShort( short v,int d0,int d1,int d2, int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
        d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return setNormalizedShort(v,d0,offset);
	}
	public int     setInt( int v,int d0,int d1,int d2,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
        d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return setNormalizedInt(v,d0,offset);
	}
	public long    setLong( long v,int d0,int d1,int d2,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
        d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return setNormalizedLong(v,d0,offset);
	}
	public float   setFloat( float v,int d0, int d1,int d2,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
        d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return setNormalizedFloat(v,d0,offset);
	}
	public double  setDouble( double v,int d0,int d1,int d2,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
        d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return setNormalizedDouble(v,d0,offset);
	}
		
	public byte    setByte( byte v,int d0,int d1,int d2,int d3,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
        d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return setNormalizedByte(v,d0,offset);
	}
	public char    setChar( char v,int d0,int d1,int d2,int d3, int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
        d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return setNormalizedChar(v,d0,offset);
	}	
	
	public boolean setBoolean( boolean v,int d0, int d1,int d2,int d3,int offset){
		byte value=0;
		if (v) value=1;
		setByte(value,d0,d1,d2,d3,offset);
		return v;
	}
	public short   setShort( short v,int d0,int d1,int d2,int d3, int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
        d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return setNormalizedShort(v,d0,offset);
	}
	public int     setInt( int v,int d0,int d1,int d2,int d3,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
        d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return setNormalizedInt(v,d0,offset);
	}
	public long    setLong( long v,int d0,int d1,int d2,int d3,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
        d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return setNormalizedLong(v,d0,offset);
	}
	public float   setFloat( float v,int d0, int d1,int d2,int d3,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
        d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return setNormalizedFloat(v,d0,offset);
	}
	public double  setDouble( double v,int d0,int d1,int d2,int d3,int offset){ 
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
        d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return setNormalizedDouble(v,d0,offset);
	}
		
	/* (non-Javadoc)
	 * @see x10.lang.StructureArray#get(int[])
	 */	
	public byte getByte( point/*(region)*/ p,int offset){
		int d0 = (int) localDist.region.ordinal(p);
		return getNormalizedByte(d0,offset);
	}	
	
	public char getChar( point/*(region)*/ p,int offset){
		assert( offset>0 && offset < 2);
		int d0 = (int) localDist.region.ordinal(p);
		return getNormalizedChar(d0,offset);
	}
	
	public boolean getBoolean( point/*(region)*/ p,int offset){
		int d0 = (int) localDist.region.ordinal(p);
		return getNormalizedBoolean(d0,offset);
	}
	
	public short getShort( point/*(region)*/ p,int offset){
		int d0 = (int) localDist.region.ordinal(p);
		return getNormalizedShort(d0,offset);
	}
    
	public int getInt( point/*(region)*/ p,int offset){
		
		int d0 = (int) localDist.region.ordinal(p);
		return getNormalizedInt(d0,offset);
	}
    
	public long getLong( point/*(region)*/ p,int offset){
		
		int d0 = (int) localDist.region.ordinal(p);
		return getNormalizedLong(d0,offset);
	}
    
	public float getFloat( point/*(region)*/ p,int offset){
		int d0 = (int) localDist.region.ordinal(p);
		return getNormalizedFloat(d0,offset);
	}
    
	public double getDouble( point/*(region)*/ p,int offset){
		long rawLong = getLong(p,offset);
		return Double.longBitsToDouble(rawLong);
	}
		
	/****************************************************/
	
	public byte    getByte( int d0,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));
        d0 = Helper.ordinal(localDist,d0,true);
		d0 = d0 + offset/4;
		return getNormalizedByte(d0,offset);
	}
    
	// offset is 0 to 3, 3 is rightmost byte
	protected byte    getNormalizedByte( int d0,int offset){
		assert( offset>0 && offset < 4);
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		int rawData = arr_.getInt(d0);
		byte returnVal = (byte)(0xff &(rawData >> (8 * (3 - offset))));
		return returnVal;
	}
	public char getChar(int d0,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,true);
		d0 = d0 + offset/4;
		return getNormalizedChar(d0,offset);
	}
	protected char getNormalizedChar(int d0,int offset){
		assert( offset>0 && offset < 2);
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		int rawData = arr_.getInt(d0);
		char returnVal = (char)(0xffff &(rawData >> (16 * (2 - offset))));
		
		return returnVal;
	}
	
	
	public boolean getBoolean(int d0,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,true);
		return getNormalizedBoolean(d0,offset);
	}
    
	public boolean getNormalizedBoolean(int d0,int offset){
		return 0 != getNormalizedByte(d0,offset);
	}
	
	public short   getShort(int d0,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,true);		
		return getNormalizedShort(d0,offset);
	}
	protected short   getNormalizedShort(int d0,int offset){
		assert( offset>0 );
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		int rawData = arr_.getInt(d0);
		short returnVal = (short)(0xffff &(rawData >> (16 * (2 - offset))));
		
		return returnVal;
	}
	
	public int     getInt(int d0,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,true);		
		return getNormalizedInt(d0,offset);
	}
	protected int     getNormalizedInt(int d0,int offset){
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		assert(offset%4 == 0);	
		return arr_.getInt(d0);
	}
	
	public long    getLong(int d0,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,true);
		return getNormalizedLong(d0,offset);
	}
	
	protected long    getNormalizedLong(int d0,int offset){
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		assert(0 == offset%8);
		int highInt = arr_.getInt(d0);
		long result = (long)highInt << 32;
		int lowInt = arr_.getInt(d0+1);
		result = result | (0x00000000ffffffff & (long)lowInt);
		return result;
	}
	
	public float   getFloat(int d0,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist, d0,true);		
		return getNormalizedFloat(d0,offset);
	}
	protected float   getNormalizedFloat(int d0,int offset){
		
		d0 =  getStartingElementIndex(d0);
		d0 = d0 + offset/4;
		return Float.intBitsToFloat(arr_.getInt(d0));
	}
	
	
	public double  getDouble(int d0,int offset){
		long rawLong = getLong(d0,offset);
		return Double.longBitsToDouble(rawLong);
	}
	
	
	public byte    getByte( int d0,int d1,int offset){
		assert( offset>0 && offset < 4);
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        d0 = Helper.ordinal(localDist,d0, d1,true);
		return getNormalizedByte(d0,offset);
	}
	public char getChar(int d0,int d1,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));
		d0 = Helper.ordinal(localDist,d0, d1,true);
		return getNormalizedChar(d0,offset);
	}	
	
	public boolean getBoolean(int d0,int d1,int offset){
		return 0 != getByte(d0,d1,offset);
	}
	
	public short   getShort(int d0,int d1,int offset){
		assert( offset>0 && offset < 2);
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        d0 = Helper.ordinal(localDist,d0, d1,true);
		return getNormalizedShort(d0,offset);
	}
	
	public int     getInt(int d0,int d1,int offset){
		if (offset != 0) 
            throw new RuntimeException("Invalid structure offset "+offset);
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));                
		d0 = Helper.ordinal(localDist,d0,d1,true);
		return getNormalizedInt(d0,0);
	}
	
	public long    getLong(int d0,int d1,int offset){
		d0 = Helper.ordinal(localDist,d0,d1,true);
		return getNormalizedLong(d0,offset);
	}
	
	public float   getFloat(int d0,int d1,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));
		d0 = Helper.ordinal(localDist,d0,d1,true);
		return getNormalizedFloat(d0,offset);
	}
	
	
	public double  getDouble(int d0,int d1,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));   
        long rawLong = getLong(d0,d1,offset);
		return Double.longBitsToDouble(rawLong);
	}
	
	
	public byte    getByte( int d0,int d1,int d2,int offset){
		assert( offset>0 && offset < 4);
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));   
        d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return getNormalizedByte(d0,offset);
	}
	public char getChar(int d0,int d1,int d2,int offset){
		assert( offset>0 && offset < 2);
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));  
        d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return getNormalizedChar(d0,offset);
	}
	
	
	public boolean getBoolean(int d0,int d1,int d2,int offset){
		return 0 != getByte(d0,d1,d2,offset);
	}
	
	public short   getShort(int d0,int d1,int d2,int offset){
		assert( offset>0 && offset < 2);
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));  
        d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return getNormalizedShort(d0,offset);
	}
	
	public int     getInt(int d0,int d1,int d2,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));  
		d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return getNormalizedInt(d0,offset);
	}
	
	public long    getLong(int d0,int d1,int d2,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));  
        d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return getNormalizedLong(d0,offset);
	}
	
	public float   getFloat(int d0,int d1,int d2,int offset){
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));  
		d0 = Helper.ordinal(localDist,d0,d1,d2,true);
		return getNormalizedFloat(d0,offset);
	}
		
	public double  getDouble(int d0,int d1,int d2,int offset){
		long rawLong = getLong(d0,d1,d2,offset);
		return Double.longBitsToDouble(rawLong);
	}	
	
	public byte    getByte( int d0,int d1,int d2,int d3,int offset){
		assert( offset>0 && offset < 4);
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));  
        d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return getNormalizedByte(d0,offset);
	}
	public char getChar(int d0,int d1,int d2,int d3,int offset){
		assert( offset>0 && offset < 2);
		d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return getNormalizedChar(d0,offset);
	}
	
	
	public boolean getBoolean(int d0,int d1,int d2,int d3,int offset){
		return 0 != getByte(d0,d1,d2,d3,offset);
	}
	
	public short   getShort(int d0,int d1,int d2,int d3,int offset){
		assert( offset>0 && offset < 2);
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));  
        d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return getNormalizedShort(d0,offset);
	}
	
	public int     getInt(int d0,int d1,int d2,int d3,int offset){
		if (offset != 0) 
            throw new RuntimeException("Invalid structure offset "+offset);
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));  
		d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return getNormalizedInt(d0,offset);
	}
	
	public long    getLong(int d0,int d1,int d2,int d3,int offset){
		if (offset != 0) 
            throw new RuntimeException("Invalid structure offset "+offset);
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));  
        d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return getNormalizedLong(d0,offset);
	}
	
	public float   getFloat(int d0,int d1,int d2,int d3,int offset){
		if(offset != 0) 
            throw new RuntimeException("Invalid structure offset "+offset);
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
		d0 = Helper.ordinal(localDist,d0,d1,d2,d3,true);
		return getNormalizedFloat(d0,offset);
	}
	
	
	public double  getDouble(int d0,int d1,int d2,int d3,int offset){
		long rawLong = getLong(d0,d1,d2,d3,offset);
		return Double.longBitsToDouble(rawLong);
	}
	
	public int get(int[] pos) {
		final point p = Runtime.factory.getPointFactory().point(pos);
		if (true)
            throw new RuntimeException("unimplemented");
		return 1;//get(p);
	}
	
	
	
	public StructureReferenceArray restriction(dist d) {
		return restriction(d.region);
	}
	
	public StructureReferenceArray restriction(region r) {
		dist dist = distribution.restriction(r);
		StructureArray_c ret = new StructureArray_c(dist, (int) 0, safe_,0);// FIXME
		if (true)
            throw new RuntimeException("unimplemented");
		/*
		 for (Iterator it = dist.iterator(); it.hasNext(); ) {
		 point p = (point) it.next();
		 ret.set(get(p), p);
		 }
		 */
		return ret;
	}
	
	public x10.lang.structureArray toValueArray() {
		if (! mutable_) return this;
		throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");   
	}
	public boolean isValue() {
		return ! this.mutable_;
	}
	
	
}
