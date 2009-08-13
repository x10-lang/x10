
/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.List;

import polyglot.types.Type;

public interface X10Use<T extends X10Def> {
    public T x10Def();
    public List<Type> annotations();
    public List<Type> annotationsMatching(Type t);
}
