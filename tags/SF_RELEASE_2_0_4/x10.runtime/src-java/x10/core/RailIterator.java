/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

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
