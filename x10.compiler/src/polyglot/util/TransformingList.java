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

