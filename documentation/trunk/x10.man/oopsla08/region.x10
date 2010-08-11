package x10.lang;

/** A region represents a k-dimensional space of points. A region is a
 * dependent class, with the value parameter specifying the dimension
 * of the region.
 * @author vj
 * @date 12/24/2004
 */

public final value class region( int dimension : dimension >= 0 )  {

    /** Construct a 1-dimensional region, if low <= high. Otherwise
     * through a MalformedRegionException.
     */
    extern public region (: dimension==1) (int low, int high)
        throws MalformedRegionException;

    /** Construct a region, using the list of region(1)'s passed as
     * arguments to the constructor.
     */
    extern public region( List(dimension)<region(1)> regions );

    /** Throws IndexOutOfBoundException if i > dimension. Returns the
        region(1) associated with the i'th dimension of this otherwise.
     */
    extern public region(1) dimension( int i )
        throws IndexOutOfBoundException;


    /** Returns true iff the region contains every point between two
     * points in the region.
     */
    extern public boolean isConvex();

    /** Return the low bound for a 1-dimensional region.
     */
    extern public (:dimension=1) int low();

    /** Return the high bound for a 1-dimensional region.
     */
    extern public (:dimension=1) int high();

    /** Return the next element for a 1-dimensional region, if any.
     */
    extern public (:dimension=1) int next( int current )
        throws IndexOutOfBoundException;

    extern public region(dimension) union( region(dimension) r);
    extern public region(dimension) intersection( region(dimension) r);
    extern public region(dimension) difference( region(dimension) r);
    extern public region(dimension) convexHull();

    /**
       Returns true iff this is a superset of r.
     */
    extern public boolean contains( region(dimension) r);
    /**
       Returns true iff this is disjoint from r.
     */
    extern public boolean disjoint( region(dimension) r);

    /** Returns true iff the set of points in r and this are equal.
     */
    public boolean equal( region(dimension) r) {
        return this.contains(r) && r.contains(this);
    }

    // Static methods follow.

    public static region(2) upperTriangular(int size) {
        return upperTriangular(2)( size );
    }
    public static region(2) lowerTriangular(int size) {
        return lowerTriangular(2)( size );
    }
    public static region(2) banded(int size, int width) {
        return banded(2)( size );
    }

    /** Return an \code{upperTriangular} region for a dim-dimensional
     * space of size \code{size} in each dimension.
     */
    extern public static (int dim) region(dim) upperTriangular(int size);

    /** Return a lowerTriangular region for a dim-dimensional space of
     * size \code{size} in each dimension.
     */
    extern public static (int dim) region(dim) lowerTriangular(int size);

    /** Return a banded region of width {\code width} for a
     * dim-dimensional space of size {\code size} in each dimension.
     */
    extern public static (int dim) region(dim) banded(int size, int width);


}
