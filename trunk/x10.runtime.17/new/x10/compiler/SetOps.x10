/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;

public interface SetOps[T] {
    def $not(): T;
    def $and(that: T): T;
    def $or(that: T): T;
    def $minus(that: T): T;
}
