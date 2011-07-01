/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

/*
 * TransformingList.java
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

