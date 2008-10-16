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
 */
public class MultiDimRegionUnique extends Distribution_c {

	public MultiDimRegionUnique(MultiDimRegion R) {
		super(R, place.MAX_PLACES==1? place.FIRST_PLACE : null, R.size()==place.MAX_PLACES);
		int size = R.size();
		assert size <= place.MAX_PLACES : "Size of unique distribution (" + size 
		+ ") > place.MAX_PLACES (" + place.MAX_PLACES + ")";
	}

	
	@Override
	public dist project(int dim) throws RankMismatchException {
		// The projection of a MultiDimRegion is a MultiDimRegion.
		MultiDimRegion r = (MultiDimRegion) region.project(dim);
		return new MultiDimRegionUnique(r);
	}
	@Override
	public dist difference(x10.lang.region R) {
		throw new UnsupportedOperationException();
	}

	@Override
	public place get(point p) throws MalformedError {
		assert p.rank==region.rank;
		int ord = region.ordinal(p);
		return place.places(ord);
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
		if (R instanceof MultiDimRegion)
			return new MultiDimRegionUnique((MultiDimRegion) R);
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
