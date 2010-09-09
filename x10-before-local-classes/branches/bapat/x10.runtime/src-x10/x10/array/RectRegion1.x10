package x10.array;

/**
 * This class represents a one-dimensional rectangular region.
 * Implementation notes: This is needed because RectRegion has
 * min and max as instance variables and not properties, so they
 * are not accessible in constraints.  We would in reality likely
 * want RectRegion2 and RectRegion3.  We would also like to use
 * the names min/max rather than intervalMin/intervalMax.
 *
 * @author jgalens
 */

public final value class RectRegion1(intervalMin: Int, intervalMax: Int) extends RectRegion{self.rank == 1, self.rect} {
	
    def this(val min: Int, val max: Int, val pm: PolyMat{self.rank == 1}): RectRegion1{self.intervalMin == min, self.intervalMax == max} {
    	super(pm);
    	property(min, max);
    }
    
    public static def make(val min: Int, val max: Int): RectRegion1{self.rank == 1, self.rect, self.intervalMin == min, self.intervalMax == max} {
        val pmb = new PolyMatBuilder(1);
        pmb.add(pmb.X(0), pmb.GE, min);
        pmb.add(pmb.X(0), pmb.LE, max);
        val pm = pmb.toSortedPolyMat(true);
        return new RectRegion1(min, max, pm);
    }
    
    class It implements Iterator[Point(1)] {

        private var x: Int = intervalMin;
        
        public def hasNext(): boolean = x <= intervalMax;
        
        final public def next(): Point(1) = Point.make(x);

        incomplete public def remove(): void;
        
    }

    public def iterator(): Iterator[Point(this.rank)] {
        return new It() as Iterator[Point(this.rank)];
    }
	
}
