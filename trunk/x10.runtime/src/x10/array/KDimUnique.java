package x10.array;


import java.util.HashSet;
import java.util.Set;

import x10.lang.Indexable;
import x10.lang.RankMismatchException;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import java.util.Arrays;

/**
 * The unique distribution for an arbitrary k-dimension region R. We require that the size
 * of R <= place.MAX_PLACES. 
 * @author vj
 *
 */
public class KDimUnique extends dist {

	place[] places;
	/**
	 * The unique distribution over arbitrary region R.
	 * @param R
	 */
	public KDimUnique(region R) {
		super(R, place.MAX_PLACES==1? place.FIRST_PLACE : null, true);
		
		int size = R.size();
		assert size <= place.MAX_PLACES : "Size of unique distribution (" + size 
		+ ") > place.MAX_PLACES (" + place.MAX_PLACES + ")";
		places = new place[size];
		for (int i=0; i < places.length; ++i) places[i] = place.places(i);
		
	}

	/**
	 * The unique region obtained by restricting U to R.
	 * @param R
	 * @param U
	 */
	KDimUnique(region R, KDimUnique U) {
		super(R, place.MAX_PLACES==1? place.FIRST_PLACE : null, true);
		region UR = U.region();
		assert UR.contains(R);
		int size = R.size();
		places = new place[size];
		for (point p: UR) {
			if (R.contains(p)) {
				places[R.ordinal(p)] = U.places[UR.ordinal(p)];
			}
		}
	}
	/**
	 * The unique distribution that maps each point q in R to p[R.ordinal(q)].
	 * @param R
	 * @param p
	 */
	KDimUnique(region R, place[] p) {
		super(R, null, true);
		places=p;
	}
	
	@Override
	public dist project(int dim) throws RankMismatchException {
		region r = region.project(dim);
		place[] pl = new place[places.length];
		for (point p: region) {
			int i = region.ordinal(p);
			int j = r.ordinal(p.project(dim));
			pl[j] = places[i];
		}
		return new KDimUnique(r, pl);
	}
	@Override
	public dist difference(x10.lang.region R) {
		throw new UnsupportedOperationException();
	}

	@Override
	public place get(point p) throws MalformedError {
		assert p.rank==region.rank;
		int ord = region.ordinal(p);
		return places[ord];
	}

	@Override
	public dist intersection(dist D) {
		throw new UnsupportedOperationException();
	}

	@Override
	public dist overlay(dist D) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<place> places() {
		return new HashSet<place>(Arrays.asList(places));
	}

	@Override
	public x10.lang.region restrictToRegion(place P) {
		throw new UnsupportedOperationException();
	}

	@Override
	public dist restriction(Set<place> Ps) {
		throw new UnsupportedOperationException();
	}

	@Override
	public dist restriction(x10.lang.region R) {
		return new KDimUnique(R, this);
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
