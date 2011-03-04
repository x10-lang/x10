/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.util.Transformation;

public class UpcastTransform<T, S extends T> implements Transformation<S, T> {
    public T transform(S v) {
        return v;
    }
}
