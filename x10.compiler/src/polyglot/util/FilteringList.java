/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.util;

import java.util.*;

/**
 * This unmodifiable List supports performing an arbitrary transformation on
 * the underlying list's elements.  The transformation is applied on every
 * access to the underlying members.
 */
public class FilteringList<T> extends AbstractList<T> {
    protected final Predicate<T> predicate;
    protected final List<T> underlying;
    protected int[] map;
    protected int size;
    
    public FilteringList(Collection<T> underlying, Predicate<T> predicate) {
	this(new ArrayList<T>(underlying), predicate);
    }

    public FilteringList(List<T> underlying, Predicate<T> predicate) {
      this.underlying = underlying;
      this.predicate = predicate;
      this.map = null;
    }

    public int size() {
        init();
        return size;
    }
    
    protected void init() {
        if (map != null) {
            return;
        }
        map = new int[underlying.size()];
        size = 0;
        for (int i = 0; i < underlying.size(); i++) {
            T x = underlying.get(i);
            if (predicate.isTrue(x)) {
                map[size++] = i;
            }
        }
    }

    public T get(int index) {
	init();
	if (index < 0 || index >= size) {
	    throw new ArrayIndexOutOfBoundsException(index);
	}
	return underlying.get(map[index]);
    }

    public Iterator<T> iterator() {
        return new FilteringIterator<T>(underlying, predicate);
    }
}

