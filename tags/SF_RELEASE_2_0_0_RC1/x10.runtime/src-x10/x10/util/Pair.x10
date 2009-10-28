/*
 *
 * (C) Copyright IBM Corporation 2006-2009.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

/**
 * This struct allows treating a pair of values as a single value, for example when returning from a method.
 */
public struct Pair[T,U] {
    public val first:T;
    public val second:U;

    public def this(first:T, second:U):Pair[T,U] {
        this.first = first;
        this.second = second;
    }

    public def toString():String {
        return "(" + first + ", " + second + ")";
    }
    public def typeName():String = "x10.util.Pair";
}
