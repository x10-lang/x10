package x10.lang;

/**
   This class represents distributions. A distribution is a region r,
   together with a map (the placemap) which specifies a place for each
   point pt in r.

   @author vj, 06/14/08
 */
public class Dist(region: Region, unique: Boolean) /* {unique => region.rail} */ implements (Point{self in region}) => Place {
    
    public property rank() = region.rank;
    public property rect() = region.rect;
    public property zeroBased() = region.zeroBased;
    public property rail() = region.rail;
    public property colMajor() = region.colMajor;

    protected def this(r: Region, u: Boolean): Dist(r, u) = {
	property(r,u);
    }
    
    public def apply(p: Point{self in Region}): Place = {
        return places()(0);
    }
    
    public incomplete def coord(Place, Point): Point;
    
    public static def constant(r: Region, p: Place): Dist(r, false) = new Dist(r, false);
    
    /**
       Each distribution specifies a map from places to regions.
       this.region is the union of all the regions in the range of
       this map.
     */
    public abstract def regionMap(): Map[Place,Region{self.subset(region)}];
    /**
       Return the (possibly null) sub-region of this.region which is
       mapped to the given place p.
     */
    public def localRegion(p: Place):Region{self.subset(region)}= regionMap(p);
    

    /**
       The rail of places to which points are mapped by this
       distribution. The length of this rail is the number of places
       in the range of the map.

     */
    public def places(): ValRail[Place]=regionMap().keys();
    public def regions(): ValRail[Region(self.subset(region))] =regionMap().values();
    
    /**
       Return the backbone of this distribution. The backbone is
       defined over the region 0..this.places().length-1 and maps
       the i'th point in this region to this.places()(i).
       
       Note backbone.places()==this.places()

     */
    var backbone:Dist(unique);
    public def backbone(): Dist{unique} = {
	if (var != null) return backbone;
	backbone = Dist.makeUnique(places());
    }
			      
}
