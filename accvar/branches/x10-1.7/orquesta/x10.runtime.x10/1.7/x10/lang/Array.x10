package x10.lang;

/**
   Generic, distributed, multi-dimensional arrays.

   Note that an Array[T] is defined as an arithmetic type, if T is
   arithmetic. This permits all the arithmetic operators to be used
   (in pointwise fashion), with other Array[T]'s defined over the same
   distribution.

   There is an interesting issue about the type system. We really want
   get(pt)is permitted only if pt is in dist.region, and
   dist(pt)=here. Alternatively, we want to say that the Array
   satisfies the property that for all places p that it is defined
   over, Array implements Indexable(r), where r = (dist |
   here).region.

   Instead we say below that it implements Indexable(region), which is
   weaker. With this type, we have to permit BadPlaceException to be
   thrown.

   @author vj, 06/14/08
 */

public abstract value Array[T](dist: Dist) 
    implements Indexable[point,Region,T](region), Settable[point,Region,T](region), 
	    Arithmetic[Array[T]{dist==this.dist}](T <: Arithmetic[T](true)) {

    property region() = dist.region;
    property rank() = dist.rank;
    
    public static def make[T](d: Dist, maker: ((Point) => T)): Array[T]{dist==d, region==d.region, rank==d.rank} = Runtime.makeArray[T](dist, maker);

    val pieces: DistRail[Rail[T]{current}];
    
    protected def this(d :Dist, maker: ((Point) =>T)): 
	Array[T]{dist==d, region==d.region, rank==d.rank} = {
	    property(dist, dist.region, dist.rank);
	    // for any distribution d, d.backbone is the unique distribution 
	    // over the set of places in the range of d.
	    val u: dist{unique} = d.backbone();
	    pieces = DistRail.make[Rail[T]{current}](u, 
						     (pt: Point) => { 
							     val n: nat = (d | here).size();
							     new Rail[T](n, (pt: nat(n)) => maker(dist.coord(here, pt))) });
	}

    /**
       Return a view on this array, confined to r. This is a very
       general operation that can be used to return a view on any
       subset of the array. Accesses through this view are potentially
       expensive. Such views should be created only if there is no
       other recourse.
     */

    public abstract def view(r: Region{r in region}):Array[T]{dist==this.dist | r};

    /**
       Returns a Rail r representing the underlying memory used to
       represent the array at place p. Typical usage (A):

       val a:Array[T] = ...;
       val remoteRef = a.rawView(p);
       async (p) {
         // now remoteRef points to a local rail:
         for (int i=0; i < remoteRef.length-1; i++) 
	    remoteRef(i)=...;
       }

       This may generate better code than (B):

       val a:Array[T] = ...;
       async (p) {
         val ref = a.rawView(here);
         for (int i=0; i < ref.length-1; i++) 
	    ref(i)=...;
       }

       In (A), the runtime data passed into the async contains only
       the remote Reference to the local storage at p. In (B), with a
       straightforward compiler, the runtime data passed into the
       async could contain all the bits necessary to represent a ---
       this could include the meta-data bits necessary to represent
       the remote references at all places where a is located etc
       etc. So the compiler will have a harder time figuring out that
       it does not need to send all that information over, and only
       needs to send the reference to the locations at p over.

     */
    public abstract def rawView(p: place):Rail[T]{loc==p} = {
    }
    public def rawLocalView()=rawView(here);

    public def rawIterate(f: (nat, T)=>T) = {
	val rail = rawLocalView();
	for (var i: int = 0; i < rail.length; i++)
	    rail(i)=f(i, rail(i));
    }
    public def rawIterate(f: (nat) => T)=rawIterate((nat, T)=>f(p));
    public def rawIterate(f: () => T) = rawIterate((nat, T) => f());

    /** Fetch the value at this point in the array. Throws a
     * BadPlaceException if the point is not local. In general, this
     * is an expensive operation and should only be used with great
     * care. The programmer should attempt to write the code using
     * iterators rather than individual accessors.

     */
    public def get(point : point(region)):T = {
	val p = dist(point);
	// using the async expression syntax here. 
	p==here ? localGet(point) : async (p) localGet(point)
    }

    /** Set the value at this point in the array. Throws a
     * BadPlaceException if the point is not local. In general, this
     * is an expensive operation and should only be used with great
     * care. The programmer should attempt to write the code using
     * iterators rather than individual accessors.
     */
    public def set(pt : point(region), t: T) = set(pt, (point(region), T)=> t);
    public def set(pt : point(region), f: (point(region),T)=> T) = {
	val p = dist(point);
	if (p==here) {
	    localSet(point, f);
	} else {
	    finish async (p) localSet(point, f);
	}
    }

    /**
       Given a point pt (which is mapped by dist to the current
       place), set this(pt) to f(pt, this(pt)).l
     */
    public abstract def localSet(pt: point(dist(here)), f:(point(dist(here)), T)=>T): void;


/*
	
    val pieces: ObjectRail[Rail[T]{current}];

	public Array(:dist==_dist && region==_dist.region && rank==_dist.rank) 
	            (final dist _dist, final ObjectRailMaker maker) {
		property(_dist, _dist.region, _dist.rank);

		
	}
*/

}
