package x10.lang;

import x10.compiler.ArithmeticOps;
import x10.compiler.ComparisonOps;

public value class Point(rank: nat)
    implements Indexable[nat,int], ArithmeticOps[Point{rank==this.rank}], ComparisonOps[Point{rank==this.rank}]
{
    private val coords: Rail[int];

    private def this(coords: Rail[int]): Point{rank==coords.length} = {
        property(coords.length);
        this.coords = coords;
    }

    public def apply(i: nat): int = coords(i);
    public def coords(): Rail[int] = coords;

    public static def make(coordsx: Rail[int]): Point{rank==coordsx.length} = new Point(coordsx);
    public static def makeConstant(rank: nat, c: int): Point{self.rank==rank} = make(Rail.make[int](rank, (i: nat) => c, true));
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
