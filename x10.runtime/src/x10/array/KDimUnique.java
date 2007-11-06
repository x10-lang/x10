package x10.array;

import java.util.Set;

import x10.lang.Indexable;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;

public class KDimUnique extends dist {

	place[] places;
	region R;
	public KDimUnique(region R) {
		super(R, null, true);
		this.R=R;
		int size = R.size();
		assert size <= place.MAX_PLACES;
		places = new place[size];
		for (int i=0; i < places.length; ++i) places[i] = place.places(i);
		
	}

	@Override
	public dist difference(x10.lang.region R) {
		throw new UnsupportedOperationException();
	}

	@Override
	public place get(point p) throws MalformedError {
		assert p.rank==R.rank;
		int ord = R.ordinal(p);
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
	public Set places() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public x10.lang.region restrictToRegion(place P) {
		throw new UnsupportedOperationException();
	}

	@Override
	public dist restriction(Set Ps) {
		throw new UnsupportedOperationException();
	}

	@Override
	public dist restriction(x10.lang.region R) {
		throw new UnsupportedOperationException();
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
