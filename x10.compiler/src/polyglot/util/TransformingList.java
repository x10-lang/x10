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
public class TransformingList<S,T> extends AbstractList<T> {
    protected final Transformation<S,T> trans;
    protected final List<S> underlying;
    

    public TransformingList(Collection<S> underlying, Transformation<S,T> trans) {
	this(new ArrayList<S>(underlying), trans);
    }

    public TransformingList(List<S> underlying, Transformation<S,T> trans) {
      assert underlying != null;
      assert trans != null;
      this.underlying = underlying;
      this.trans = trans;
    }

    public int size() { return underlying.size(); }

    public T get(int index) {
	return trans.transform(underlying.get(index));
    }

}

