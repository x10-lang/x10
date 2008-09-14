package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
   The representation of a distribution in X10.
   In this implementation, many methods are implemented natively. 
   We plan to implement distributions in X10 in future releases.
   
   TODO: Dist should implement (Point) => Place, and also
   (Place) => Region.
   
   @author nystrom
   @author vj, 08/31/08
   
*/
@NativeRep("java", "x10.array.Dist")
public abstract value Dist(
    region: Region,    // the region over which the distribution is defined.
    unique: boolean,   // true if the distribution maps precisely one point to each place.
    constant: boolean, // this maps all points to onePlace. constant == onePlace !=null.
    onePlace: Place    // if nonNull, this maps all points to onePlace. Implies constant.
) implements (Point)=> Place, Iterable[Point]
{

 /**
     * The property method rank may be used in constructing types derived
     * from the class distribution. For instance, distribution(rank=k)
     * is the type of all k-dimensional distributions.
     */
    property rank: int = region.rank;
    
    /**
    A distribution is rectangular if the underlying region is rectangular.
    */
    property rect: boolean = region.rect;
    
    /**
    A distribution is zeroBased if the underlying region is zeroBased.
    */
    property zeroBased: boolean = region.zeroBased;
    
    /** 
    A distribution is a rail if the underlying region is a rail, i.e.
    zero-Based, rectangular, rank==1.
    Note that this.unique implies this.rail.
    
    TODO: Figure out how to say this so that the compiler can make use of this
    assertion.
    */
    property rail: boolean = region.rail;

    const UNIQUE = makeUnique();

    // *** constant distributions
    /**
      Make a constant distribution defined over the given region and place.
    */

    @Native("java", "x10.array.DistFactory.makeConstant(#1, #2)")
    public static native def makeConstant(r: Region, p: Place)
    : Dist{region==r,onePlace==p};
   
   /** 
     Make a constant local distribution.
   */
   @Native("java", "x10.array.DistFactory.makeConstant(#1, x10.runtime.Runtime.here())")
   public native static def makeConstant(r: Region): Dist{region==r,onePlace==here};
    
   /**
    Make a constant local distribution. Same as makeConstant(r).
   */
   @Native("java", "x10.array.DistFactory.makeConstant(#1, x10.runtime.Runtime.here())")
   public native static def make(r: Region): Dist{region==r,onePlace==here};

   // *** unique distributions
   /**
     Make a unique distribution, defined over a region with one point for every
     place in the current execution.
   */
   @Native("java", "x10.array.DistFactory.makeUnique()")
     public native static def makeUnique(): Dist{unique};
     
    
   /**
     Make a unique distribution, defined over the given sequence of places.
   */
   @Native("java", "x10.array.DistFactory.makeUnique(#1)")
  native public static def makeUnique(ps: ValRail[Place])
    : Dist{rail};
    
// *** block distributions

/**
   Return a block distribution of the given region, blocked along the given dimension 
   (axis), across all places. (Dimensions are 0-based.)
*/
@Native("java", "x10.array.DistFactory.makeBlock(#1, #2)")
    public native static def makeBlock(r: Region, axis: int)
    : Dist(r);
    
@Native("java", "x10.array.DistFactory.makeBlock(#1, 1)")
    public native static def makeBlock(r: Region)
    : Dist(r);
    
    
/**
   Return a block distribution of the given region, blocked along the given dimension 
   (axis), across the given set of places. (Dimensions are 0-based.)
   TODO: Replace Set[Place] with ValRail[Place].
*/
@Native("java", "x10.array.DistFactory.makeBlock(#1, #2, #3)")
native public static def makeBlock(r: Region, axis: int, ps: ValRail[Place])
     : Dist(r);


    
// *** cyclic distributions
@Native("java", "x10.array.DistFactory.makeCyclic(#1)")
    public native static def makeCyclic(r: Region)
    : Dist(r);

@Native("java", "x10.array.DistFactory.makeCyclic(#1, #2)")
    public native static def makeCyclic(r: Region, axis: int)
    : Dist(r);

   incomplete public static def makeCyclic(r: Region, axis: int, ps: Set[Place])
   : Dist(r);

// *** BlockCyclic distributions
   
 //   incomplete public static def makeBlockCyclic(r: Region, axis: int, blockSize: int, ps: Set[Place]): Dist;
    
@Native("java", "x10.array.DistFactory.makeBlockCyclic(#1, #2, #3)")
    public native static def makeBlockCyclic(r: Region, axis: int, blockSize: int)
    : Dist(r);
 //   public abstract def regionMap(): Map[Place,Region];
    
    @Native("java", "x10.array.DistFactory.makeBlockCyclic(#1, 0, #2)")
    public native static def makeBlockCyclic(r: Region,  blockSize: int)
    : Dist(r);
    
// ***Random distributions
    
@Native("java", "x10.array.DistFactory.makeRandom(#1)")
    public native static def makeRandom(r: Region)
    : Dist(r);
    
// ***Arbitrary distributions
    
@Native("java", "x10.array.DistFactory.makeArbitrary(#1)")
    public native static def makeArbitrary(r: Region)
    : Dist(r);
   
    
@Native("java", "(#0).places()")
    public native def places(): ValRail[Place];
    
    /**
    The set of regions, one per place, defined by this distribution. 
    TODO: Should return a Map[Place,Region].
    */
    public abstract def regions(): Rail[Region];
    
    /** The region at a particular place, defined by this distribution.
    May be empty.
    */
    public abstract def get(p: Place): Region;
    
@Native("java", "(#0).get(#1)")
    public native def get(p: Point): Place;
    
@Native("java", "(#0).get(#1)")
    public native def apply(p: Point): Place;
    
    @Native("java", " (#0).get(#1)")
    public native def apply(i0: int){rank==1}: Place;
    @Native("java", "(#0).get(#1, #2)")
    public native def apply(i0: int, i1: int){rank==2}: Place;
    @Native("java", " (#0).get(#1, #2, #3)")
    public native def apply(i0: int, i1: int, i2: int){rank==3}: Place;

    
    @Native("java", "(#0).iterator()")
    public native def iterator(): Iterator[Point];

// Distribution operations.
/**
  Return the same distribution but restricted to this.region-r.
*/
@Native("java", "(#0).difference(#1)")
    public abstract def difference(d: Dist(this.rank)): Dist(this.rank);

@Native("java", "(#0).difference(#1)")
    public abstract def $minus(d: Dist(this.rank)): Dist(this.rank);

  /**
     * Takes as parameter a distribution d defined over a region
     * disjoint from this. Returns a distribution defined over 
     * this.region.union(d.region). 
     * The distribution maps p to this(p) if p in this.region, and
     * t d(p) if p in d.region.
     * 
     * TODO: Enahnce the type system to express that this.region is disjoint from
     * d.region.
     * @seeAlso distribution.asymmetricUnion.
     */
@Native("java", "(#0).union(#1)")
    public abstract def union(d: Dist(this.rank)): Dist(this.rank);
    
@Native("java", "(#0).union(#1)")
    public abstract def $or(d: Dist(this.rank)): Dist(this.rank);
    
     /**
     * Returns a distribution defined on region.union(R): it
     * takes on D.get(p) for all points p in R, and this.get(p) for
     * all remaining points.
     */
     @Native("java", "(#0).restriction(#1)")
     public abstract def overlay(d: Dist(this.rank)): Dist(this.rank);
  
    /**
      Return the distribution defined over the region this.region.intersection(d.region),
      and which maps each point p in this region to this(p).
    */
    @Native("java", "(#0).intersection((#1).region)")
    public abstract def intersection(d: Dist(this.rank)): Dist(this.rank);
    
     /**
      Return the distribution defined over the region this.region.intersection(r),
      and which maps each point p in this region to this(p).
    */
    @Native("java", "(#0).intersection(#1)")
    public abstract def intersection(r: Region(this.rank)): Dist(this.rank);
    

    /**
      Return true iff this.region is a subset of d.region, and for every p in this.region,
      this(p)==d(p).
    */
    @Native("java", "(#0).isSubDistribution(#1)")
    public abstract def isSubDistribution(d: Dist(this.rank)): boolean;
    
     /**
     * Returns a new distribution obtained by restricting this to the
     * domain region.intersection(r), where parameter r is a region
     * with the same rank.
     */
    @Native("java", "(#0).restriction(#1)")
    public native def restriction(r: Region(this.rank)): Dist(this.rank);

    /** The d | r operator, defined as d.restriction(r).
    */
    @Native("java", "(#0).restriction(#1)")
    public native def $bar(r: Region(this.rank)): Dist(this.rank);
    
      /**
     * Returns a local distribution over the region of all points that this maps
     * to p.
     */
    @Native("java", "(#0).restriction(#1)")
    public abstract def restriction(p: Place): Dist(this.rank){onePlace==p};
    
    /** The d | p operator, defined as d.restriction(p).
    */
    @Native("java", "(#0).restriction(#1)")
    public abstract def $bar(p: Place): Dist(this.rank){onePlace==p};
    

    protected def this(region: Region, unique: boolean, constant: boolean, onePlace: Place) = {
        property(region, unique, constant, onePlace);
    }
}
