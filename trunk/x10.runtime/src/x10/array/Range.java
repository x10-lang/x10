/*
 * Created on Oct 3, 2004
 */
package x10.array;

import x10.lang.point;
import x10.lang.region;
import x10.runtime.distributed.DeserializerBuffer;
import x10.runtime.distributed.SerializerBuffer;
/**
 * Ranges are a collection of points in the int space.
 * Currently, only contiguous sets of points are supported.
 * Range objects are immutable.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 * @author vj
 */
public abstract class Range 
extends region/*(1)*/ {
	
	/**
	 * Cardinality of the range, i.e., the number of element in the integer
	 * space it covers.
	 */
	public final int size;
	public final int lo;
	public final int hi;
	
	public Range (int l, int h, int c) {
		super(1);
		lo = l;
		hi = h;
		size = c;
		assert size == hi-lo+1;
		assert hi >= lo;
	}
	
	public /*nat*/int size() {
		return size;
	}
	
	public int ordinal(int j) {
		int ret = j-lo;
        if (ret < 0 || ret >= size)
            throw new ArrayIndexOutOfBoundsException(j);
        return ret;
	}
	
	public boolean contains(int i) {
		return lo <= i && i <= hi;
	}
	
	public boolean contains( int[] p) {
	    return contains(point.factory.point(Range.this, p));
	}
		
	public int high() {
		return hi;
	}
	
	public int low() {
		return lo;
	}
	
	public region union(region r ) {
		assert r != null;
		return ArbitraryRegion.union(this, r);
	}
	
	public region intersection( region r ) {
		assert r != null;
		return ArbitraryRegion.intersection(this, r);
	}
	
	public region difference(region r ) {
		assert r != null;
		return ArbitraryRegion.difference(this, r);
	}
	
	public boolean disjoint(region r ) {
		assert r != null;
		return intersection(r).size() == 0;
	}
	
	/**
	 * Returns itself -- this is the 1-d region at index (i % 1) = 0.
	 */
	public region rank(int i) {
	    assert i == 0;
		return this;
	}

// store in an array of longs
// [<unique id> <type>,<low>,<high>,<size>]
        public void serialize(SerializerBuffer outputBuffer){
        //System.out.println("serializing
          Integer originalIndex = outputBuffer.findOriginalRef(this);
        System.out.println("serializing range:"+this+" ("+this.hashCode()+") origid:"+originalIndex);
          if(originalIndex == null){
            originalIndex = new Integer(outputBuffer.getOffset());
            outputBuffer.recordRef(this,originalIndex);
          }
          else {
          outputBuffer.writeLong(originalIndex.intValue());
          System.out.println("reuse entry at "+originalIndex.intValue());
          return;
          }

          outputBuffer.writeLong(originalIndex.intValue());
          outputBuffer.writeLong(RANGE);
          outputBuffer.writeLong(low());  
          outputBuffer.writeLong(high());        
          outputBuffer.writeLong(size());
       
        }


public static region deserialize(DeserializerBuffer inputBuffer){
          
          int low,high,size;
          int thisIndex = inputBuffer.getOffset();
          int owningIndex = (int)inputBuffer.readLong();

          if(thisIndex != owningIndex){
         
            System.out.println("found a second reference "+thisIndex+" to "+owningIndex);
            return (region)inputBuffer.getCachedRef(owningIndex);
          }
          
          int type = (int)inputBuffer.readLong();
          assert(type == RANGE);
          low = (int)inputBuffer.readLong();
           high = (int)inputBuffer.readLong();
           size = (int)inputBuffer.readLong();
                      
           int stepSize;
           if(size>0)
              stepSize = (1+high-low)/size;
           else
              stepSize=1;

           region result = factory.region(low,high,stepSize);
           inputBuffer.cacheRef(owningIndex,result);
           return result;
        }

}
