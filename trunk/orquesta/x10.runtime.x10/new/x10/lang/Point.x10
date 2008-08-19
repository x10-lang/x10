package x10.lang;

import x10.compiler.ArithmeticOps;
import x10.compiler.ComparisonOps;

public value class Point(rank: nat)
    implements Indexable[nat,int], ArithmeticOps[Point{rank==this.rank}], ComparisonOps[Point{rank==this.rank}]
{
    private val coords: ValRail[int];

    private def this(cs: ValRail[int]): Point{rank==cs.length} = {
        property(cs.length);
        this.coords = cs;
    }

    public def apply(i: nat): int = coords(i);
    public def coords(): ValRail[int] = coords;

    public static def make(cs: ValRail[int]): Point{rank==cs.length} = new Point(cs);
    public static def makeConstant(rank: nat, c: int): Point{self.rank==rank} = {
        val a: ValRail[int]{length==rank} = ValRail.make[int](rank, (i: nat) => c, true);
        return make(a);
    }
    public static def makeZero(rank: nat): Point{self.rank==rank} = makeConstant(rank, 0);

    incomplete public def $plus(): Point(rank);
    incomplete public def $minus(): Point(rank);

    incomplete public def $plus(that: Point(rank)): Point(rank);
    incomplete public def $minus(that: Point(rank)): Point(rank);
    incomplete public def $times(that: Point(rank)): Point(rank);
    incomplete public def $over(that: Point(rank)): Point(rank);
    incomplete public def $percent(that: Point(rank)): Point(rank);

    incomplete public def $eq(that: Point(rank)): boolean;
    incomplete public def $lt(that: Point(rank)): boolean;
    incomplete public def $gt(that: Point(rank)): boolean;
    incomplete public def $le(that: Point(rank)): boolean;
    incomplete public def $ge(that: Point(rank)): boolean;
    incomplete public def $ne(that: Point(rank)): boolean;

    incomplete public def toString(): String;
}
