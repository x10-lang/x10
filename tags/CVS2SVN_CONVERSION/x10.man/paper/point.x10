package x10.lang;

public final class point( region region ) {
    parameter int dimension = region.dimension;
    // an array of the given size.
    int[dimension] val;

    /** Create a point with the given values in each dimension.
     */
    public point( int[dimension] val ) {
        this.val = val;
    }

    /** Return the value of this point on the i'th dimension.
     */
    public int valAt( int i) throws IndexOutOfBoundException {
        if (i < 1 || i > dimension) throw new IndexOutOfBoundException();
        return val[i];
    }

    /** Return the next point in the given region on this given
     * dimension, if any.
     */
    public void inc( int i )
        throws IndexOutOfBoundException, MalformedRegionException {
        int val = valAt(i);
        val[i] = region.dimension(i).next( val );
    }

    /** Return true iff the point is on the upper boundary of the i'th
     * dimension.
     */
    public boolean onUpperBoundary(int i)
        throws IndexOutOfBoundException {
        int val = valAt(i);
        return val == region.dimension(i).high();
    }

    /** Return true iff the point is on the lower boundary of the i'th
     * dimension.
     */
    public boolean onLowerBoundary(int i)
        throws IndexOutOfBoundException {
        int val = valAt(i);
        return val == region.dimension(i).low();
    }
}

