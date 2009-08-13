/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

public interface CollectionIterator[+T] extends Iterator[T] {
    public def remove(): Void;
}
