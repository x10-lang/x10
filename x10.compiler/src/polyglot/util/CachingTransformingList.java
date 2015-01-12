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
 *  (C) Copyright IBM Corporation 2007-2015.
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
