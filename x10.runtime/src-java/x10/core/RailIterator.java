package x10.core;

import java.util.Iterator;

public class RailIterator<T> implements Iterator<T> {
	int i;
	AnyRail<T> r;

	public RailIterator(AnyRail<T> r) {
		this.r = r;
		this.i = 0;
	}

	public boolean hasNext() {
		return i < r.length();
	}

	public T next() {
		return r.get(i++);
	}

	public void remove() {
		throw new UnsupportedOperationException("remove");
	}
}
