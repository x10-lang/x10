/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

public interface ListIterator[+T] extends CollectionIterator[T] {
    public def hasNext():boolean;
    public def next():T;
    public def nextIndex(): Int;

    public def hasPrevious():boolean;
    public def previous():T;
    public def previousIndex(): Int;

    public def set(T): Void;
    public def add(T): Void;
}
