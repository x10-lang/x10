package x10.array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import x10.lang.Indexable;
import x10.lang.RankMismatchException;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;

/**
 * An implementation of the unique distribution defined over region=[lo1:hi1,...lok:hik]. 
 * We require that the size of the region <= place.MAX_PLACES. Every point p in the
 * region is assigned to the place places[region.ord(p)].
 * @author vj
 *
 *Implementation note. There are three types of MultiDimRegionRegionUniques. See the three constructors.
 *
 *(Type 1) The first is constructed from a single MultiDimRegion R. The size of this region must be 
 *no gresater than the number of places. The point p in R is mapped to place n, where n=R.ordinal(p).
 *
 *(Type 2) The second is constructed by restricting a base MultiDimRegionUnique to a region R
 *that is a subset of base.region. This distribution maps a point p in R to place z provided
 *that base maps p to z.
 *
 *(Type 3) The third is constructed by starting with a base MultiDimRegionUnique and projecting its
 * underlying region on the k'th dimension. 
 * 
 * See the definition of the get function to see how these three types of distributions are implemented.
 * 
 * HEre are some examples of the use of this distribution: GRID is a unique distribution over the 
 * region [0:1, 0:1]. 
 * GRID [0,0] = place(id=0)
GRID [0,1] = place(id=1)
GRID [1,0] = place(id=2)
GRID [1,1] = place(id=3)

d=GRID | [0:0, 0:1] = MDRU(#155453764){[0,0]->place(id=0),[0,1]->place(id=1),}
d1=d.project(0)=MDRU(#161614242){[0]->place(id=0),[1]->place(id=1),}

d=GRID | [1:1, 0:1] = MDRU(#263458740){[1,0]->place(id=2),[1,1]->place(id=3),}
d1=d.project(0)=MDRU(#269619218){[0]->place(id=2),[1]->place(id=3),}

d=GRID | [0:1,0:0] = MDRU(#386012930){[0,0]->place(id=0),[1,0]->place(id=2),}
d1=d.project(1)=MDRU(#392173408){[0]->place(id=0),[1]->place(id=2),}

d=GRID | [0:1,1:1] = MDRU(#494017906){[0,1]->place(id=1),[1,1]->place(id=3),}
d1=d.project(1)=MDRU(#500178384){[0]->place(id=1),[1]->place(id=3),}

//rows[0] = (GRID | [0:0,0:1]).project(0) 
// rows[1] = (GRID | [1:1,0:1]).project(0)
// cols[0]  = (GRID | [0:1,0:0]).project(1)
// cols[1] = (GRID | [0:1,1:1]).project(1)
rows [0] = MDRU(#161614242){[0]->place(id=0),[1]->place(id=1),}
rows [1] = MDRU(#269619218){[0]->place(id=2),[1]->place(id=3),}
columns [0] = MDRU(#392173408){[0]->place(id=0),[1]->place(id=2),}
columns [1] = MDRU(#500178384){[0]->place(id=1),[1]->place(id=3),}
 * 
 */
public class MultiDimRegionUnique extends Distribution_c {

	public MultiDimRegionUnique(MultiDimRegion R) {
		super(R, place.MAX_PLACES==1? place.FIRST_PLACE : null, R.size()==place.MAX_PLACES);
		int size = R.size();
		assert size <= place.MAX_PLACES : "Size of unique distribution (" + size 
		+ ") > place.MAX_PLACES (" + place.MAX_PLACES + ")";
	}

	MultiDimRegionUnique base;
	public MultiDimRegionUnique(MultiDimRegion R, MultiDimRegionUnique base) {
		super(R, place.MAX_PLACES==1? place.FIRST_PLACE : null, R.size()==place.MAX_PLACES);
		this.base = base;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder("MDRU(#" + hashCode() + "){");
		for (point p: region) {
			sb.append(p).append("->").append(get(p)).append(",");
		}
		return sb.toString()+"}";
	}
	int projectedOutDim = -1;
	int projectedOutVal = -1;
	
	/**
	 * 
	 */
	@Override
	public dist project(int dim) throws RankMismatchException {
		// The projection of a MultiDimRegion is a MultiDimRegion.
		
		MultiDimRegion r = (MultiDimRegion) region.project(dim);
		int pDim =  dim;
		/*int pVal = 0;
		for (point p : region) {
			if (r.contains(p.project(dim))) {
				pVal = p.get(dim);
				break;
			}
		}*/
		//return new MultiDimRegionUnique(r, pDim, pVal, this);
		return new MultiDimRegionUnique(r, pDim, region.rank(dim).low(), this);
	}
	public MultiDimRegionUnique(MultiDimRegion R, int pDim, int pVal, MultiDimRegionUnique base) {
		super(R, place.MAX_PLACES==1? place.FIRST_PLACE : null, R.size()==place.MAX_PLACES);
		this.base=base;
		this.projectedOutDim = pDim;
		this.projectedOutVal = pVal;
		
	}
	@Override
	public dist difference(x10.lang.region R) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This is the key method implemented for this kind of distribution: it specifies the map of
	 * points to places. This map depends on how the distribution was constructed. 
	 * If base != null then the distribution is either a type (2) or a type (3) distribution.
	 * If additionally, projectedOutDim==-1 then this distribution is of type (2) 
	 * (obtained by restricting a base distribution over a given region). In this case
	 * the result is just base.get(p).  
	 * For a type (3) distribution, expand the point p in the given dimension with the given
	 * value to obtain the point q and return the place the base maps q to.
	 * For a type (1) distribution return place.places(region.ordinal(p)).
	 */
	@Override
	public place get(point p) throws MalformedError {
		assert p.rank==region.rank;
		assert (region.contains(p)) : "MultiDimRegionUnique: " + region + " must contain " + p;
		if (! region.contains(p)) throw new MalformedError();
		if (base != null) {
			if (projectedOutDim==-1)
			return base.get(p);
			point q = p.expand(projectedOutDim, projectedOutVal);
			return base.get(q);
		}
		int ord = region.ordinal(p);
		
		place q = place.places(ord);
		
		return q;
	}

	@Override
	public dist intersection(dist D) {
		throw new UnsupportedOperationException();
	}

	@Override
	public dist overlay(dist D) {
		throw new UnsupportedOperationException();
	}

	
	@Override
	public Set<place> places() {
	   Set<place> places = new TreeSet<place>();
	   for (point p : region)
		   places.add(place.places(region.ordinal(p)));
	   return places;
	}

	@Override
	public x10.lang.region restrictToRegion(place P) {
		point p = region.coord(P.id);
		return MultiDimRegion.make(p);
	}

	@Override
	public dist restriction(Set<place> Ps) {
		Set<point> points = new HashSet<point>();
		Map<point,place> map = new HashMap<point,place>();
		for (place p : Ps) {
			point q = region.coord(p.id);
			points.add(q);
			map.put(q, p);
		}
		return new ArbitraryUnique(new ArbitraryRegion(rank, points),map);
		
	}

	@Override
	public dist restriction(x10.lang.region R) {
		assert (region.contains(R)) : "MDRU(#" + hashCode() + ") has region " + region 
		+ " which does not contain intended restriction " + R;
		if (R instanceof MultiDimRegion)
			return new MultiDimRegionUnique((MultiDimRegion) R, this);
		return new KDimUnique(R);
	}

	@Override
	public boolean subDistribution(x10.lang.region R, dist D) {
		if (D==this) return true;
		throw new UnsupportedOperationException();
	}

	@Override
	public dist union(dist D) {
		throw new UnsupportedOperationException();
	}

	public boolean isValue() {
		return true;
	}

	public boolean valueEquals(Indexable other) {
		// TODO Auto-generated method stub
		return false;
	}

}
