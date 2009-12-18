/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;

public interface ComparisonOps[T] {
    operator this == (that: T): boolean;
    operator this <  (that: T): boolean;
    operator this >  (that: T): boolean;
    operator this <= (that: T): boolean;
    operator this >= (that: T): boolean;
    operator this != (that: T): boolean;
}
