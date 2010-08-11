package x10.lang;

/** The class of all  multidimensional, distributed arrays in X10.

    <p> I dont yet know how to handle B@current base type for the
    array.

 * @author vj 12/24/2004
 */

public final value class array ( distribution dist )<B@P> {
    parameter int dimension = dist.dimension;
    parameter region(dimension) region = dist.region;

    /** Return an array initialized with the given function which
        maps each point in region to a value in B.
     */
    extern public array( Fun<point(region),B@P> init);

    /** Return the value of the array at the given point in the
     * region.
     */
    extern public B@P valueAt(point(region) p);

    /** Return the value obtained by reducing the given array with the
        function fun, which is assumed to be associative and
        commutative. unit should satisfy fun(unit,x)=x=fun(x,unit).
     */
    extern public B reduce(Fun<B@?,Fun<B@?,B@?>> fun, B@? unit);


    /** Return an array of B with the same distribution as this, by
        scanning this with the function fun, and unit unit.
     */
    extern public array(dist)<B> scan(Fun<B@?,Fun<B@?,B@?>> fun, B@? unit);

    /** Return an array of B@P defined on the intersection of the
        region underlying the array and the parameter region R.
     */
    extern public (region(dimension) R)
        array(dist.restriction(R)())<B@P>  restriction();

    /** Return an array of B@P defined on the intersection of
        the region underlying this and the parametric distribution.
     */
    public  (distribution(:dimension=this.dimension) D)
        array(dist.restriction(D.region)())<B@P> restriction();

    /** Take as parameter a distribution D of the same dimension as *
     * this, and defined over a disjoint region. Take as argument an *
     * array other over D. Return an array whose distribution is the
     * union of this and D and which takes on the value
     * this.atValue(p) for p in this.region and other.atValue(p) for p
     * in other.region.
     */
    extern public (distribution(:region.disjoint(this.region) &&
                                dimension=this.dimension) D)
        array(dist.union(D))<B@P> compose( array(D)<B@P> other);

    /** Return the array obtained by overlaying this array on top of
        other. The method takes as parameter a distribution D over the
        same dimension. It returns an array over the distribution
        dist.asymmetricUnion(D).
     */
    extern public (distribution(:dimension=this.dimension) D)
        array(dist.asymmetricUnion(D))<B@P> overlay( array(D)<B@P> other);

    extern public array<B> overlay(array<B> other);

    /** Assume given an array a over distribution dist, but with
     * basetype C@P. Assume given a function f: B@P -> C@P -> D@P.
     * Return an array with distribution dist over the type D@P
     * containing fun(this.atValue(p),a.atValue(p)) for each p in
     * dist.region.
     */
    extern public <C@P, D>
        array(dist)<D@P> lift(Fun<B@P, Fun<C@P, D@P>> fun, array(dist)<C@P> a);

    /**  Return an array of B with distribution d initialized
         with the value b at every point in d.
     */
    extern public static (distribution D) <B@P> array(D)<B@P> constant(B@? b);

}
