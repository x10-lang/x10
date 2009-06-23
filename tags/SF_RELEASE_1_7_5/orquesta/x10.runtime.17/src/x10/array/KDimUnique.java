package x10.array;

import java.util.Set;

import x10.core.Indexable;
import x10.runtime.Place;
import x10.runtime.Runtime;

public class KDimUnique extends Dist {

	Place[] places;
	Region R;
	public KDimUnique(Region R) {
		super(R, null, true);
		this.R=R;
		int size = R.size();
		assert size <= Runtime.MAX_PLACES;
		places = new Place[size];
		for (int i=0; i < places.length; ++i) places[i] = Runtime.place(i);
		
	}

	@Override
	public Dist difference(Region R) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Place get(Point p) throws MalformedError {
		assert p.rank==R.rank;
		int ord = R.ordinal(p);
		return places[ord];
	}

	@Override
	public Dist intersection(Dist D) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Dist overlay(Dist D) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Place> places() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Region restrictToRegion(Place P) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Dist restriction(Set<Place> Ps) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Dist restriction(Region R) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean subDistribution(Region R, Dist D) {
		if (D==this) return true;
		throw new UnsupportedOperationException();
	}

	@Override
	public Dist union(Dist D) {
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
