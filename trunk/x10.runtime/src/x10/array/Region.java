/*
 * Created on Oct 3, 2004
 */
package x10.array;

import java.util.Iterator;

/**
 * Implementation of Region. The Points in a region, aka. tuples, 
 * are implemented as one-dimensional int arrays. Instance of 
 * this class are immutable!
 *
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public interface Region {

    public int rank();

    public int size();
    
	public Region union(Region d);

	public Region difference(Region r);
	
	public Region intersect(Region r);
	
    public Range dim(int i);
    
    public Range[] dim();
	
    public boolean contains(Region r);
	
    public boolean contains(int[] p);
	
    public int ordinal(int[] p);
	
    public Iterator iterator();
}
