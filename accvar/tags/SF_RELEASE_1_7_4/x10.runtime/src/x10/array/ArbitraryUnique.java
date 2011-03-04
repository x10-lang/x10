package x10.array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;

public class ArbitraryUnique extends Distribution_c {
	
	static place onePlace(Map<point,place> map) {
		if (map.values().size() != 1) return null;
		for (place p : map.values() ) 
			return p;
		return null; // will never get here.
	}

	Map<point,place> map;
	/**
	 * Assumes that the map is defined over each point in region 
	 * and that each point is mapped to a different place.
	 * @param region
	 * @param map
	 */
	ArbitraryUnique(region region, Map<point,place> map) {
		// A unique distribution is required to map over every place.
		super(region, onePlace(map), map.values().size()==place.MAX_PLACES); 
		this.map=map;
	}


	@Override
	public place get(point p) {
		return map.get(p);
	}

	@Override
	public dist project(int dim) {
		Map<point, place> m = new HashMap<point, place>();
		for (point p : region) {
			point p1 = p.project(dim);
			m.put(p1, map.get(p));
		}
		return new ArbitraryUnique(region.project(dim), map);
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

}
