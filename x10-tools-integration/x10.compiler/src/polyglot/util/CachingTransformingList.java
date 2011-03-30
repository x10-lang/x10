/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

/*
 * CachingTransformingList.java
 */
package polyglot.util;

import java.util.*;

/**
 * This subclass of TransformingList applies the transformation to each
 * element of the underlying list at most once.
 */
public class CachingTransformingList<S,T> extends TransformingList<S,T> {
    protected ArrayList<T> cache;

    public CachingTransformingList(Collection<S> underlying,
				   Transformation<S,T> trans)
	{
	    this(new ArrayList<S>(underlying), trans);
	}

    public CachingTransformingList(List<S> underlying, Transformation<S,T> trans) {
	super(underlying, trans);
	cache = new ArrayList<T>(Collections.<T>nCopies(underlying.size(), null));
    }

    public T get(int index) {
	T o = cache.get(index);
	if (o == null) {
	    o = trans.transform(underlying.get(index));
	    cache.set(index, o);
	}
	return o;
    }
}
