/*
 * Created on Oct 3, 2004
 */
package x10.lang;

import java.util.Iterator;

/**
 * Implementation of Region. The Points in a region, aka. tuples, 
 * are implemented as one-dimensional int arrays. Instance of 
 * this class are immutable!
 *
 * @author Christoph von Praun
 */
public abstract class Region extends X10Object implements TypeArgument {
	public final int rank;

	public Region(int r) {		
		this.rank = r;
	}
	
	public abstract Region sub(Range[] dims);
	
	public abstract Region combine(Region r);
	
	public abstract Range dim(int i);
	
	public abstract boolean contains(Region r);
	
	public abstract boolean contains(int[] p);
	
	public abstract int ordinal(int[] p);
	
	public abstract Iterator iterator();
	
	public abstract String toString();
	
	public abstract boolean equals(Object o);
	
	public abstract int hashCode();		
}
