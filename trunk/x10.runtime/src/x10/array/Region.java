/*
 * Created on Oct 3, 2004
 */
package x10.array;

import java.util.Iterator;

import x10.base.TypeArgument;

/**
 * Implementation of Region. The Points in a region, aka. tuples, 
 * are implemented as one-dimensional int arrays. Instance of 
 * this class are immutable!
 *
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public interface Region extends TypeArgument {

    public int rank();

    public int count();
    
	public Region union(Region d);

	public Region difference(Region r);
	
	public Region intersection(Region r);
	
    public Range range(int i);
    
    public Range[] dim();
	
    public boolean subset(Region r);
	
    public boolean contains(int[] p);
	
    public int ordinal(int[] p);
	
    public Iterator iterator();
    
    public boolean equals(Object o);
    
    public int hashCode();
}
