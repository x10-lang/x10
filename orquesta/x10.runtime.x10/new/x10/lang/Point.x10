package x10.lang;

import x10.compiler.ArithmeticOps;

public value class Point(rank: nat)
    implements Indexable[nat,int], ArithmeticOps[Point]
{

    incomplete public def get(i: int): int;
    incomplete public def coords(): Rail[int];

    incomplete public static def make(coords: Rail[int]): Point;
    incomplete public static def makeConstant(rank: int, c: int): Point;
    incomplete public static def makeZero(rank: int): Point;

    incomplete public def $plus(): Point;
    incomplete public def $minus(): Point;

    incomplete public def $plus(that: Point): Point;
    incomplete public def $minus(that: Point): Point;
    incomplete public def $times(that: Point): Point;
    incomplete public def $over(that: Point): Point;

    incomplete public def $eq(that: Point): boolean;
    incomplete public def $lt(that: Point): boolean;
    incomplete public def $gt(that: Point): boolean;
    incomplete public def $le(that: Point): boolean;
    incomplete public def $ge(that: Point): boolean;
    incomplete public def $ne(that: Point): boolean;

    incomplete public def toString(): String;

    private val coords: Rail[int];
    private def this[T](coords: Rail[int]) = {
        // XXX
    }

}
