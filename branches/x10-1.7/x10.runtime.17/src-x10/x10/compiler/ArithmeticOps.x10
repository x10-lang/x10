/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;

public interface ArithmeticOps[T] {
    def $plus(): T;
    def $minus(): T;

    def $plus(that: T): T;
    def $minus(that: T): T;
    def $times(that: T): T;
    def $over(that: T): T;
    def $percent(that: T): T;
}

