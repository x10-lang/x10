/*
 * Created on Oct 3, 2004
 */
package x10.array;

import x10.base.TypeArgument;

/**
 * Ranges are a collection of points in the int space.
 * Currently, only contiguous sets of points are supported.
 * Range objects are immutable.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public abstract class Range implements TypeArgument {

    /**
     * Cardinality of the range, i.e., the number of element in the integer
     * space it covers.
     */
    public final int count;

    public final int lo;

    public final int hi;

    
    public Range (int l, int h, int c) {
        lo = l;
        hi = h;
        count = c;
        assert hi >= lo;
    }
    
    /**
     * @param ord the ordinal number, must be smaller than size()
     * @return the coordinate that has ordinal number ord
     */
    public abstract int coord(int ord);
    
    /**
     * @param p a point in the coordinate space
     * @return the ordinal number of the point [0 ... size()[
     */
	public abstract int ordinal(int p);
	
	public abstract Range union(Range r);
	
	public abstract Range intersect(Range r);

	public abstract Range difference(Range r);

	public abstract boolean contains(int p);

	public abstract boolean contains(Range r);

	public abstract String toString(); 
	
	public abstract boolean equals(Object o);
	
	public abstract int hashCode();
}
